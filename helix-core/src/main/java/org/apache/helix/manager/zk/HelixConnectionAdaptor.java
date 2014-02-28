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

import org.apache.helix.ClusterMessagingService;
import org.apache.helix.ConfigAccessor;
import org.apache.helix.ConfigChangeListener;
import org.apache.helix.ControllerChangeListener;
import org.apache.helix.CurrentStateChangeListener;
import org.apache.helix.ExternalViewChangeListener;
import org.apache.helix.HealthStateChangeListener;
import org.apache.helix.HelixAdmin;
import org.apache.helix.HelixConnection;
import org.apache.helix.HelixDataAccessor;
import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerProperties;
import org.apache.helix.IdealStateChangeListener;
import org.apache.helix.InstanceConfigChangeListener;
import org.apache.helix.InstanceType;
import org.apache.helix.LiveInstanceChangeListener;
import org.apache.helix.LiveInstanceInfoProvider;
import org.apache.helix.MessageListener;
import org.apache.helix.PreConnectCallback;
import org.apache.helix.PropertyKey;
import org.apache.helix.ScopedConfigChangeListener;
import org.apache.helix.ZNRecord;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.Id;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.SessionId;
import org.apache.helix.api.role.MultiClusterController;
import org.apache.helix.api.role.SingleClusterController;
import org.apache.helix.api.role.HelixParticipant;
import org.apache.helix.api.role.HelixRole;
import org.apache.helix.healthcheck.ParticipantHealthReportCollector;
import org.apache.helix.model.HelixConfigScope.ConfigScopeProperty;
import org.apache.helix.participant.StateMachineEngine;
import org.apache.helix.store.zk.ZkHelixPropertyStore;
import org.apache.log4j.Logger;

/**
 * Adapt helix-connection to helix-manager, so we can pass to callback-handler and
 * notification-context
 */
public class HelixConnectionAdaptor implements HelixManager {
  private static Logger LOG = Logger.getLogger(HelixConnectionAdaptor.class);

  final HelixRole _role;
  final HelixConnection _connection;
  final ClusterId _clusterId;
  final Id _instanceId;
  final InstanceType _instanceType;
  final HelixDataAccessor _accessor;
  final ClusterMessagingService _messagingService;

  public HelixConnectionAdaptor(HelixRole role) {
    _role = role;
    _connection = role.getConnection();
    _clusterId = role.getClusterId();
    _accessor = _connection.createDataAccessor(_clusterId);

    _instanceId = role.getId();
    _instanceType = role.getType();
    _messagingService = role.getMessagingService();
  }

  @Override
  public void connect() throws Exception {
    _connection.connect();
  }

  @Override
  public boolean isConnected() {
    return _connection.isConnected();
  }

  @Override
  public void disconnect() {
    _connection.disconnect();
  }

  @Override
  public void addIdealStateChangeListener(IdealStateChangeListener listener) throws Exception {
    _connection.addIdealStateChangeListener(_role, listener, _clusterId);
  }

  @Override
  public void addLiveInstanceChangeListener(LiveInstanceChangeListener listener) throws Exception {
    _connection.addLiveInstanceChangeListener(_role, listener, _clusterId);
  }

  @Override
  public void addConfigChangeListener(ConfigChangeListener listener) throws Exception {
    _connection.addConfigChangeListener(_role, listener, _clusterId);
  }

  @Override
  public void addInstanceConfigChangeListener(InstanceConfigChangeListener listener)
      throws Exception {
    _connection.addInstanceConfigChangeListener(_role, listener, _clusterId);
  }

  @Override
  public void addConfigChangeListener(ScopedConfigChangeListener listener, ConfigScopeProperty scope)
      throws Exception {
    _connection.addConfigChangeListener(_role, listener, _clusterId, scope);
  }

  @Override
  public void addMessageListener(MessageListener listener, String instanceName) throws Exception {
    _connection.addMessageListener(_role, listener, _clusterId, ParticipantId.from(instanceName));
  }

  @Override
  public void addCurrentStateChangeListener(CurrentStateChangeListener listener,
      String instanceName, String sessionId) throws Exception {
    _connection.addCurrentStateChangeListener(_role, listener, _clusterId,
        ParticipantId.from(instanceName), SessionId.from(sessionId));
  }

  @Override
  public void addHealthStateChangeListener(HealthStateChangeListener listener, String instanceName)
      throws Exception {
    _connection.addHealthStateChangeListener(_role, listener, _clusterId,
        ParticipantId.from(instanceName));
  }

  @Override
  public void addExternalViewChangeListener(ExternalViewChangeListener listener) throws Exception {
    _connection.addExternalViewChangeListener(_role, listener, _clusterId);
  }

  @Override
  public void addControllerListener(ControllerChangeListener listener) {
    _connection.addControllerListener(_role, listener, _clusterId);
  }

  @Override
  public boolean removeListener(PropertyKey key, Object listener) {
    return _connection.removeListener(_role, listener, key);
  }

  @Override
  public HelixDataAccessor getHelixDataAccessor() {
    return _accessor;
  }

  @Override
  public ConfigAccessor getConfigAccessor() {
    return _connection.getConfigAccessor();
  }

  @Override
  public String getClusterName() {
    return _clusterId.stringify();
  }

  @Override
  public String getInstanceName() {
    return _instanceId.stringify();
  }

  @Override
  public String getSessionId() {
    return _connection.getSessionId().stringify();
  }

  @Override
  public long getLastNotificationTime() {
    return 0;
  }

  @Override
  public HelixAdmin getClusterManagmentTool() {
    return _connection.createClusterManagementTool();
  }

  @Override
  public ZkHelixPropertyStore<ZNRecord> getHelixPropertyStore() {
    return (ZkHelixPropertyStore<ZNRecord>) _connection.createPropertyStore(_clusterId);
  }

  @Override
  public ClusterMessagingService getMessagingService() {
    return _messagingService;
  }

  @Override
  public ParticipantHealthReportCollector getHealthReportCollector() {
    throw new UnsupportedOperationException();
  }

  @Override
  public InstanceType getInstanceType() {
    return _instanceType;
  }

  @Override
  public String getVersion() {
    return _connection.getHelixVersion();
  }

  @Override
  public HelixManagerProperties getProperties() {
    return _connection.getHelixProperties();
  }

  @Override
  public StateMachineEngine getStateMachineEngine() {
    StateMachineEngine engine = null;
    switch (_role.getType()) {
    case PARTICIPANT:
      HelixParticipant participant = (HelixParticipant) _role;
      engine = participant.getStateMachineEngine();
      break;
    case CONTROLLER_PARTICIPANT:
      MultiClusterController autoController = (MultiClusterController) _role;
      engine = autoController.getStateMachineEngine();
      break;
    default:
      LOG.info("helix manager type: " + _role.getType()
          + " does NOT have state-machine-engine");
      break;
    }

    return engine;
  }

  @Override
  public boolean isLeader() {
    boolean isLeader = false;
    switch (_role.getType()) {
    case CONTROLLER:
      SingleClusterController controller = (SingleClusterController) _role;
      isLeader = controller.isLeader();
      break;
    case CONTROLLER_PARTICIPANT:
      MultiClusterController autoController = (MultiClusterController) _role;
      isLeader = autoController.isLeader();
      break;
    default:
      LOG.info("helix manager type: " + _role.getType() + " does NOT support leadership");
      break;
    }
    return isLeader;
  }

  @Override
  public void startTimerTasks() {
    throw new UnsupportedOperationException(
        "HelixConnectionAdaptor does NOT support start timer tasks");
  }

  @Override
  public void stopTimerTasks() {
    throw new UnsupportedOperationException(
        "HelixConnectionAdaptor does NOT support stop timer tasks");
  }

  @Override
  public void addPreConnectCallback(PreConnectCallback callback) {
    switch (_role.getType()) {
    case PARTICIPANT:
      HelixParticipant participant = (HelixParticipant) _role;
      participant.addPreConnectCallback(callback);
      break;
    case CONTROLLER_PARTICIPANT:
      MultiClusterController autoController = (MultiClusterController) _role;
      autoController.addPreConnectCallback(callback);
      break;
    default:
      LOG.info("helix manager type: " + _role.getType()
          + " does NOT support add pre-connect callback");
      break;
    }
  }

  @Override
  public void setLiveInstanceInfoProvider(LiveInstanceInfoProvider liveInstanceInfoProvider) {
    switch (_role.getType()) {
    case PARTICIPANT:
      HelixParticipant participant = (HelixParticipant) _role;
      participant.setLiveInstanceInfoProvider(liveInstanceInfoProvider);
      break;
    case CONTROLLER_PARTICIPANT:
      MultiClusterController autoController = (MultiClusterController) _role;
      autoController.setLiveInstanceInfoProvider(liveInstanceInfoProvider);
      break;
    default:
      LOG.info("helix manager type: " + _role.getType()
          + " does NOT support set additional live instance information");
      break;
    }
  }

  @Override
  public void addControllerMessageListener(MessageListener listener) {
    // TODO Auto-generated method stub

  }

}
