package org.apache.helix.integration;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.helix.HelixConnection;
import org.apache.helix.HelixDataAccessor;
import org.apache.helix.NotificationContext;
import org.apache.helix.PropertyKeyBuilder;
import org.apache.helix.TestHelper;
import org.apache.helix.ZkUnitTestBase;
import org.apache.helix.api.accessor.ClusterAccessor;
import org.apache.helix.core.config.builder.ClusterConfigBuilder;
import org.apache.helix.api.config.builder.ResourceConfigBuilder;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.ControllerId;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.model.PropertyKey;
import org.apache.helix.api.model.configuration.ParticipantConfiguration;
import org.apache.helix.api.model.ipc.Message;
import org.apache.helix.api.model.statemachine.State;
import org.apache.helix.api.model.statemachine.StateModelDefinition;
import org.apache.helix.api.model.strategy.RebalancerConfiguration;
import org.apache.helix.api.role.HelixParticipant;
import org.apache.helix.api.role.SingleClusterController;
import org.apache.helix.controller.rebalancer.config.SemiAutoRebalancerConfig;
import org.apache.helix.manager.zk.ZkHelixConnection;
import org.apache.helix.model.builder.StateModelDefinitionBuilder;
import org.apache.helix.model.composite.ExternalView;
import org.apache.helix.participant.statemachine.HelixStateModelFactory;
import org.apache.helix.participant.statemachine.StateModel;
import org.apache.helix.participant.statemachine.StateModelInfo;
import org.apache.helix.participant.statemachine.Transition;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestHelixConnection extends ZkUnitTestBase {
  private static final Logger LOG = Logger.getLogger(TestHelixConnection.class.getName());

  @StateModelInfo(initialState = "OFFLINE", states = {
      "MASTER", "SLAVE", "OFFLINE", "ERROR"
  })
  public static class MockStateModel extends StateModel {
    public MockStateModel() {

    }

    @Transition(to = "*", from = "*")
    public void onBecomeAnyFromAny(Message message, NotificationContext context) {
      String from = message.getFromState();
      String to = message.getToState();
      LOG.info("Become " + to + " from " + from);
    }
  }

  public static class MockStateModelFactory extends HelixStateModelFactory<MockStateModel> {

    public MockStateModelFactory() {
    }

    @Override
    public MockStateModel createNewStateModel(PartitionId partitionId) {
      MockStateModel model = new MockStateModel();

      return model;
    }
  }

  @Test
  public void test() throws Exception {
    String className = TestHelper.getTestClassName();
    String methodName = TestHelper.getTestMethodName();
    String clusterName = className + "_" + methodName;

    System.out.println("START " + clusterName + " at " + new Date(System.currentTimeMillis()));

    String zkAddr = ZK_ADDR;
    ClusterId clusterId = ClusterId.from(clusterName);
    ControllerId controllerId = ControllerId.from("controller");
    final ParticipantId participantId = ParticipantId.from("participant1");

    ResourceId resourceId = ResourceId.from("testDB");
    State master = State.from("MASTER");
    State slave = State.from("SLAVE");
    State offline = State.from("OFFLINE");
    State dropped = State.from("DROPPED");
    StateModelDefinitionId stateModelDefId = StateModelDefinitionId.from("MasterSlave");

    // create connection
    HelixConnection connection = new ZkHelixConnection(zkAddr);
    connection.connect();

    // setup cluster
    ClusterAccessor clusterAccessor = connection.createClusterAccessor(clusterId);
    clusterAccessor.dropCluster();

    StateModelDefinition stateModelDef =
        new StateModelDefinitionBuilder(stateModelDefId).addState(master, 1).addState(slave, 2)
            .addState(offline, 3).addState(dropped).addTransition(offline, slave, 3)
            .addTransition(slave, offline, 4).addTransition(slave, master, 2)
            .addTransition(master, slave, 1).addTransition(offline, dropped).initialState(offline)
            .upperBound(master, 1).dynamicUpperBound(slave, "R").build();
    RebalancerConfiguration rebalancerCtx =
        new SemiAutoRebalancerConfig.Builder().withResourceId(resourceId).withPartitionCount(1)
            .withReplicaCount(1).withStateModelDefId(stateModelDefId)
            .withPreferenceList(PartitionId.from("testDB_0"), Arrays.asList(participantId)).build();
    clusterAccessor.createCluster(new ClusterConfigBuilder().withClusterId(clusterId)
        .addStateModelDefinition(stateModelDef).build());
    clusterAccessor.addResourceToCluster(ResourceConfigBuilder.newInstance().with(resourceId)
        .rebalancerConfig(rebalancerCtx).build());
    clusterAccessor.addParticipantToCluster(new ParticipantConfiguration.Builder(participantId).build());

    // start controller
    SingleClusterController controller = connection.createController(clusterId, controllerId);
    controller.start();

    // start participant
    HelixParticipant participant = connection.createParticipant(clusterId, participantId);
    participant.getStateMachineEngine().registerStateModelFactory(
        StateModelDefinitionId.from("MasterSlave"), new MockStateModelFactory());

    participant.start();
    Thread.sleep(1000);

    // verify
    final HelixDataAccessor accessor = connection.createDataAccessor(clusterId);
    final PropertyKeyBuilder keyBuilder = accessor.keyBuilder();
    boolean success = TestHelper.verify(new TestHelper.Verifier() {

      @Override
      public boolean verify() throws Exception {
        ExternalView externalView = accessor.getProperty(keyBuilder.externalView("testDB"));
        Map<ParticipantId, State> stateMap = externalView.getStateMap(PartitionId.from("testDB_0"));

        if (stateMap == null || !stateMap.containsKey(participantId)) {
          return false;
        }

        return stateMap.get(participantId).equals(State.from("MASTER"));
      }
    }, 10 * 1000);

    Assert.assertTrue(success);

    // clean up
    controller.stop();
    participant.stop();
    connection.disconnect();

    System.out.println("END " + clusterName + " at " + new Date(System.currentTimeMillis()));
  }
}
