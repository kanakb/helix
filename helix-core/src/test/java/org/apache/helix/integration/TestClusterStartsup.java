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

import java.util.Date;

import org.apache.helix.HelixException;
import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.api.model.PropertyType;
import org.apache.helix.api.role.MemberRole;
import org.apache.helix.tools.ClusterSetup;
import org.apache.helix.util.HelixUtil;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestClusterStartsup extends ZkStandAloneCMTestBase {
  void setupCluster() throws HelixException {
    System.out.println("START " + CLASS_NAME + " at " + new Date(System.currentTimeMillis()));

    String namespace = "/" + CLUSTER_NAME;
    if (_gZkClient.exists(namespace)) {
      _gZkClient.deleteRecursive(namespace);
    }
    _setupTool = new ClusterSetup(ZK_ADDR);

    // setup storage cluster
    _setupTool.addCluster(CLUSTER_NAME, true);
    _setupTool.addResourceToCluster(CLUSTER_NAME, TEST_DB, 20, STATE_MODEL);
    for (int i = 0; i < NODE_NR; i++) {
      String storageNodeName = "localhost_" + (START_PORT + i);
      _setupTool.addInstanceToCluster(CLUSTER_NAME, storageNodeName);
    }
    _setupTool.rebalanceStorageCluster(CLUSTER_NAME, TEST_DB, 3);
  }

  @Override
  @BeforeClass()
  public void beforeClass() throws Exception {

  }

  @Override
  @AfterClass()
  public void afterClass() {
  }

  @Test()
  public void testParticipantStartUp() throws Exception {
    setupCluster();
    String controllerMsgPath =
        HelixUtil.getControllerPropertyPath(CLUSTER_NAME, PropertyType.MESSAGES_CONTROLLER);
    _gZkClient.deleteRecursive(controllerMsgPath);
    HelixManager manager = null;

    try {
      manager =
          HelixManagerFactory.getZKHelixManager(CLUSTER_NAME, "localhost_" + (START_PORT + 1),
              MemberRole.PARTICIPANT, ZK_ADDR);
      manager.connect();
      Assert.fail("Should fail on connect() since cluster structure is not set up");
    } catch (HelixException e) {
      // OK
    }

    if (manager != null) {
      AssertJUnit.assertFalse(manager.isConnected());
    }

    try {
      manager =
          HelixManagerFactory.getZKHelixManager(CLUSTER_NAME, "localhost_" + (START_PORT + 3),
              MemberRole.PARTICIPANT, ZK_ADDR);
      manager.connect();
      Assert.fail("Should fail on connect() since cluster structure is not set up");
    } catch (HelixException e) {
      // OK
    }

    if (manager != null) {
      AssertJUnit.assertFalse(manager.isConnected());
    }

    setupCluster();
    String stateModelPath = HelixUtil.getStateModelDefinitionPath(CLUSTER_NAME);
    _gZkClient.deleteRecursive(stateModelPath);

    try {
      manager =
          HelixManagerFactory.getZKHelixManager(CLUSTER_NAME, "localhost_" + (START_PORT + 1),
              MemberRole.PARTICIPANT, ZK_ADDR);
      manager.connect();
      Assert.fail("Should fail on connect() since cluster structure is not set up");
    } catch (HelixException e) {
      // OK
    }
    if (manager != null) {
      AssertJUnit.assertFalse(manager.isConnected());
    }

    setupCluster();
    String instanceStatusUpdatePath =
        HelixUtil.getInstancePropertyPath(CLUSTER_NAME, "localhost_" + (START_PORT + 1),
            PropertyType.STATUSUPDATES);
    _gZkClient.deleteRecursive(instanceStatusUpdatePath);

    try {
      manager =
          HelixManagerFactory.getZKHelixManager(CLUSTER_NAME, "localhost_" + (START_PORT + 1),
              MemberRole.PARTICIPANT, ZK_ADDR);
      manager.connect();
      Assert.fail("Should fail on connect() since cluster structure is not set up");
    } catch (HelixException e) {
      // OK
    }
    if (manager != null) {
      AssertJUnit.assertFalse(manager.isConnected());
    }

  }
}
