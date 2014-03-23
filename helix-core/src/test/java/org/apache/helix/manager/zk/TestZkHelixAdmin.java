package org.apache.helix.manager.zk;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.helix.HelixDataAccessor;
import org.apache.helix.HelixException;
import org.apache.helix.PropertyKeyBuilder;
import org.apache.helix.TestHelper;
import org.apache.helix.ZkUnitTestBase;
import org.apache.helix.api.model.HelixConfigScope;
import org.apache.helix.api.model.PropertyKey;
import org.apache.helix.api.model.PropertyPathConfig;
import org.apache.helix.api.model.PropertyType;
import org.apache.helix.api.model.ZNRecord;
import org.apache.helix.api.model.HelixConfigScope.ConfigScopeProperty;
import org.apache.helix.api.model.constraint.ClusterConstraints;
import org.apache.helix.api.model.constraint.ConstraintItem;
import org.apache.helix.api.model.constraint.ConstraintItemBuilder;
import org.apache.helix.api.model.constraint.ClusterConstraints.ConstraintAttribute;
import org.apache.helix.api.model.constraint.ClusterConstraints.ConstraintType;
import org.apache.helix.api.model.id.ConstraintId;
import org.apache.helix.api.model.statemachine.StateModelDefinition;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.model.builder.HelixConfigScopeBuilder;
import org.apache.helix.model.composite.ExternalView;
import org.apache.helix.tools.StateModelConfigGenerator;
import org.apache.zookeeper.data.Stat;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class TestZkHelixAdmin extends ZkUnitTestBase {
  @Test()
  public void testZkHelixAdmin() {
    System.out.println("START testZkHelixAdmin at " + new Date(System.currentTimeMillis()));

    final String clusterName = getShortClassName();
    String rootPath = "/" + clusterName;
    if (_gZkClient.exists(rootPath)) {
      _gZkClient.deleteRecursive(rootPath);
    }

    ZKHelixAdmin tool = new ZKHelixAdmin(_gZkClient);
    tool.addCluster(clusterName, true);
    Assert.assertTrue(ZKUtil.isClusterSetup(clusterName, _gZkClient));
    tool.addCluster(clusterName, true);
    Assert.assertTrue(ZKUtil.isClusterSetup(clusterName, _gZkClient));

    List<String> list = tool.getClusters();
    AssertJUnit.assertTrue(list.size() > 0);

    try {
      Stat oldstat = _gZkClient.getStat(rootPath);
      Assert.assertNotNull(oldstat);
      boolean success = tool.addCluster(clusterName, false);
      // Even though it exists, it should return true but it should not make any changes in zk
      Assert.assertTrue(success);
      Stat newstat = _gZkClient.getStat(rootPath);
      Assert.assertEquals(oldstat, newstat);
    } catch (HelixException e) {
      // OK
    }

    InstanceConfig config = new InstanceConfig("host1_9999");
    config.setHostName("host1");
    config.setPort("9999");
    tool.addInstance(clusterName, config);
    tool.enableInstance(clusterName, "host1_9999", true);
    String path = PropertyPathConfig.getPath(PropertyType.INSTANCES, clusterName, "host1_9999");
    AssertJUnit.assertTrue(_gZkClient.exists(path));

    try {
      tool.addInstance(clusterName, config);
      Assert.fail("should fail if add an alredy-existing instance");
    } catch (HelixException e) {
      // OK
    }
    config = tool.getInstanceConfig(clusterName, "host1_9999");
    AssertJUnit.assertEquals(config.getId(), "host1_9999");

    tool.dropInstance(clusterName, config);
    try {
      tool.getInstanceConfig(clusterName, "host1_9999");
      Assert.fail("should fail if get a non-existent instance");
    } catch (HelixException e) {
      // OK
    }
    try {
      tool.dropInstance(clusterName, config);
      Assert.fail("should fail if drop on a non-existent instance");
    } catch (HelixException e) {
      // OK
    }
    try {
      tool.enableInstance(clusterName, "host1_9999", false);
      Assert.fail("should fail if enable a non-existent instance");
    } catch (HelixException e) {
      // OK
    }
    ZNRecord stateModelRecord = new ZNRecord("id1");
    try {
      tool.addStateModelDef(clusterName, "id1", new StateModelDefinition(stateModelRecord));
      path = PropertyPathConfig.getPath(PropertyType.STATEMODELDEFS, clusterName, "id1");
      AssertJUnit.assertTrue(_gZkClient.exists(path));
      Assert.fail("should fail");
    } catch (HelixException e) {
      // OK
    } catch (IllegalArgumentException ex) {
      // OK
    }

    tool.addStateModelDef(clusterName, "MasterSlave", new StateModelDefinition(
        StateModelConfigGenerator.generateConfigForMasterSlave()));
    stateModelRecord = StateModelConfigGenerator.generateConfigForMasterSlave();
    try {
      tool.addStateModelDef(clusterName, stateModelRecord.getId(), new StateModelDefinition(
          stateModelRecord));
      Assert.fail("should fail if add an already-existing state model");
    } catch (HelixException e) {
      // OK
    }
    list = tool.getStateModelDefs(clusterName);
    AssertJUnit.assertEquals(list.size(), 1);

    try {
      tool.addResource(clusterName, "resource", 10, "nonexistStateModelDef");
      Assert.fail("should fail if add a resource without an existing state model");
    } catch (HelixException e) {
      // OK
    }
    try {
      tool.addResource(clusterName, "resource", 10, "id1");
      Assert.fail("should fail");
    } catch (HelixException e) {
      // OK
    }
    list = tool.getResourcesInCluster(clusterName);
    AssertJUnit.assertEquals(list.size(), 0);
    try {
      tool.addResource(clusterName, "resource", 10, "id1");
      Assert.fail("should fail");
    } catch (HelixException e) {
      // OK
    }
    list = tool.getResourcesInCluster(clusterName);
    AssertJUnit.assertEquals(list.size(), 0);

    ExternalView resourceExternalView = tool.getResourceExternalView(clusterName, "resource");
    AssertJUnit.assertNull(resourceExternalView);

    // test config support
    // ConfigScope scope = new ConfigScopeBuilder().forCluster(clusterName)
    // .forResource("testResource").forPartition("testPartition").build();
    HelixConfigScope scope =
        new HelixConfigScopeBuilder(ConfigScopeProperty.PARTITION).forCluster(clusterName)
            .forResource("testResource").forPartition("testPartition").build();

    Map<String, String> properties = new HashMap<String, String>();
    properties.put("pKey1", "pValue1");
    properties.put("pKey2", "pValue2");

    // make sure calling set/getConfig() many times will not drain zkClient resources
    // int nbOfZkClients = ZkClient.getNumberOfConnections();
    for (int i = 0; i < 100; i++) {
      tool.setConfig(scope, properties);
      Map<String, String> newProperties =
          tool.getConfig(scope, new ArrayList<String>(properties.keySet()));
      Assert.assertEquals(newProperties.size(), 2);
      Assert.assertEquals(newProperties.get("pKey1"), "pValue1");
      Assert.assertEquals(newProperties.get("pKey2"), "pValue2");
    }
    // Assert.assertTrue(ZkClient.getNumberOfConnections() - nbOfZkClients < 5);

    System.out.println("END testZkHelixAdmin at " + new Date(System.currentTimeMillis()));
  }

  // drop resource should drop corresponding resource-level config also
  @Test
  public void testDropResource() {
    String className = TestHelper.getTestClassName();
    String methodName = TestHelper.getTestMethodName();
    String clusterName = className + "_" + methodName;

    System.out.println("START " + clusterName + " at " + new Date(System.currentTimeMillis()));

    ZKHelixAdmin tool = new ZKHelixAdmin(_gZkClient);
    tool.addCluster(clusterName, true);
    Assert.assertTrue(ZKUtil.isClusterSetup(clusterName, _gZkClient), "Cluster should be setup");

    tool.addStateModelDef(clusterName, "MasterSlave", new StateModelDefinition(
        StateModelConfigGenerator.generateConfigForMasterSlave()));
    tool.addResource(clusterName, "test-db", 4, "MasterSlave");
    Map<String, String> resourceConfig = new HashMap<String, String>();
    resourceConfig.put("key1", "value1");
    tool.setConfig(new HelixConfigScopeBuilder(ConfigScopeProperty.RESOURCE)
        .forCluster(clusterName).forResource("test-db").build(), resourceConfig);

    PropertyKeyBuilder keyBuilder = new PropertyKeyBuilder(clusterName);
    Assert.assertTrue(_gZkClient.exists(keyBuilder.idealStates("test-db").getPath()),
        "test-db ideal-state should exist");
    Assert.assertTrue(_gZkClient.exists(keyBuilder.resourceConfig("test-db").getPath()),
        "test-db resource config should exist");

    tool.dropResource(clusterName, "test-db");
    Assert.assertFalse(_gZkClient.exists(keyBuilder.idealStates("test-db").getPath()),
        "test-db ideal-state should be dropped");
    Assert.assertFalse(_gZkClient.exists(keyBuilder.resourceConfig("test-db").getPath()),
        "test-db resource config should be dropped");

    System.out.println("END " + clusterName + " at " + new Date(System.currentTimeMillis()));
  }

  // test add/remove message constraint
  @Test
  public void testAddRemoveMsgConstraint() {
    String className = TestHelper.getTestClassName();
    String methodName = TestHelper.getTestMethodName();
    String clusterName = className + "_" + methodName;

    System.out.println("START " + clusterName + " at " + new Date(System.currentTimeMillis()));

    ZKHelixAdmin tool = new ZKHelixAdmin(_gZkClient);
    tool.addCluster(clusterName, true);
    Assert.assertTrue(ZKUtil.isClusterSetup(clusterName, _gZkClient), "Cluster should be setup");

    // test admin.getMessageConstraints()
    ClusterConstraints constraints =
        tool.getConstraints(clusterName, ConstraintType.MESSAGE_CONSTRAINT);
    Assert.assertNull(constraints, "message-constraint should NOT exist for cluster: " + className);

    // remove non-exist constraint
    try {
      tool.removeConstraint(clusterName, ConstraintType.MESSAGE_CONSTRAINT, "constraint1");
      // will leave a null message-constraint znode on zk
    } catch (Exception e) {
      Assert.fail("Should not throw exception when remove a non-exist constraint.");
    }

    // add a message constraint
    ConstraintItemBuilder builder = new ConstraintItemBuilder();
    builder.addConstraintAttribute(ConstraintAttribute.RESOURCE.toString(), "MyDB")
        .addConstraintAttribute(ConstraintAttribute.CONSTRAINT_VALUE.toString(), "1");
    tool.setConstraint(clusterName, ConstraintType.MESSAGE_CONSTRAINT, "constraint1",
        builder.build());

    HelixDataAccessor accessor =
        new ZKHelixDataAccessor(clusterName, new ZkBaseDataAccessor<ZNRecord>(_gZkClient));
    PropertyKeyBuilder keyBuilder = new PropertyKeyBuilder(clusterName);
    constraints =
        accessor.getProperty(keyBuilder.constraint(ConstraintType.MESSAGE_CONSTRAINT.toString()));
    Assert.assertNotNull(constraints, "message-constraint should exist");
    ConstraintItem item = constraints.getConstraintItem(ConstraintId.from("constraint1"));
    Assert.assertNotNull(item, "message-constraint for constraint1 should exist");
    Assert.assertEquals(item.getConstraintValue(), "1");
    Assert.assertEquals(item.getAttributeValue(ConstraintAttribute.RESOURCE), "MyDB");

    // test admin.getMessageConstraints()
    constraints = tool.getConstraints(clusterName, ConstraintType.MESSAGE_CONSTRAINT);
    Assert.assertNotNull(constraints, "message-constraint should exist");
    item = constraints.getConstraintItem(ConstraintId.from("constraint1"));
    Assert.assertNotNull(item, "message-constraint for constraint1 should exist");
    Assert.assertEquals(item.getConstraintValue(), "1");
    Assert.assertEquals(item.getAttributeValue(ConstraintAttribute.RESOURCE), "MyDB");

    // remove a exist message-constraint
    tool.removeConstraint(clusterName, ConstraintType.MESSAGE_CONSTRAINT, "constraint1");
    constraints =
        accessor.getProperty(keyBuilder.constraint(ConstraintType.MESSAGE_CONSTRAINT.toString()));
    Assert.assertNotNull(constraints, "message-constraint should exist");
    item = constraints.getConstraintItem(ConstraintId.from("constraint1"));
    Assert.assertNull(item, "message-constraint for constraint1 should NOT exist");

    System.out.println("END " + clusterName + " at " + new Date(System.currentTimeMillis()));
  }
}
