package org.apache.helix.controller.rebalancer;

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

import java.util.Map;
import java.util.Set;

import org.apache.helix.HelixManager;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.model.rebalancer.RebalancerConfiguration;
import org.apache.helix.api.model.statemachine.State;
import org.apache.helix.api.model.statemachine.StateModelDefinition;
import org.apache.helix.api.snapshot.Cluster;
import org.apache.helix.controller.context.ControllerContextProvider;
import org.apache.helix.controller.rebalancer.config.BasicRebalancerConfig;
import org.apache.helix.controller.rebalancer.config.CustomRebalancerConfig;
import org.apache.helix.controller.rebalancer.util.ConstraintBasedAssignment;
import org.apache.helix.controller.stages.ResourceCurrentState;
import org.apache.helix.model.ResourceAssignment;
import org.apache.log4j.Logger;

public class CustomRebalancer implements HelixRebalancer {

  private static final Logger LOG = Logger.getLogger(CustomRebalancer.class);

  @Override
  public void init(HelixManager helixManager, ControllerContextProvider contextProvider) {
    // do nothing
  }

  @Override
  public ResourceAssignment computeResourceMapping(RebalancerConfiguration rebalancerConfig,
      ResourceAssignment prevAssignment, Cluster cluster, ResourceCurrentState currentState) {
    CustomRebalancerConfig config =
        BasicRebalancerConfig.convert(rebalancerConfig, CustomRebalancerConfig.class);
    StateModelDefinition stateModelDef =
        cluster.getStateModelMap().get(config.getStateModelDefId());
    if (LOG.isDebugEnabled()) {
      LOG.debug("Processing resource:" + config.getResourceId());
    }
    ResourceAssignment partitionMapping = new ResourceAssignment(config.getResourceId());
    for (PartitionId partition : config.getPartitionSet()) {
      Map<ParticipantId, State> currentStateMap =
          currentState.getCurrentStateMap(config.getResourceId(), partition);
      Set<ParticipantId> disabledInstancesForPartition =
          ConstraintBasedAssignment.getDisabledParticipants(cluster.getParticipantMap(), partition);
      Map<ParticipantId, State> bestStateForPartition =
          ConstraintBasedAssignment.computeCustomizedBestStateForPartition(cluster
              .getLiveParticipantMap().keySet(), stateModelDef, config.getPreferenceMap(partition),
              currentStateMap, disabledInstancesForPartition);
      partitionMapping.addReplicaMap(partition, bestStateForPartition);
    }
    return partitionMapping;
  }
}
