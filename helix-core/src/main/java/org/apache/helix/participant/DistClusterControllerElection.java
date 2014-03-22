package org.apache.helix.participant;

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

import org.apache.helix.ControllerChangeListener;
import org.apache.helix.HelixDataAccessor;
import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.NotificationContext;
import org.apache.helix.api.model.MemberType;
import org.apache.helix.PropertyKeyBuilder;
import org.apache.helix.controller.GenericHelixController;
import org.apache.helix.controller.HelixControllerMain;
import org.apache.helix.manager.zk.ZkHelixLeaderElection;
import org.apache.log4j.Logger;

// TODO: merge with GenericHelixController
public class DistClusterControllerElection implements ControllerChangeListener {
  private static Logger LOG = Logger.getLogger(DistClusterControllerElection.class);
  private final String _zkAddr;
  private final GenericHelixController _controller = new GenericHelixController();
  private HelixManager _leader;

  public DistClusterControllerElection(String zkAddr) {
    _zkAddr = zkAddr;
  }

  /**
   * may be accessed by multiple threads: zk-client thread and
   * ZkHelixManager.disconnect()->reset() TODO: Refactor accessing
   * HelixMaangerMain class statically
   */
  @Override
  public synchronized void onControllerChange(NotificationContext changeContext) {
    HelixManager manager = changeContext.getManager();
    if (manager == null) {
      LOG.error("missing attributes in changeContext. requires HelixManager");
      return;
    }

    MemberType type = manager.getInstanceType();
    if (type != MemberType.CONTROLLER && type != MemberType.CONTROLLER_PARTICIPANT) {
      LOG.error("fail to become controller because incorrect instanceType (was " + type.toString()
          + ", requires CONTROLLER | CONTROLLER_PARTICIPANT)");
      return;
    }

    try {
      if (changeContext.getType().equals(NotificationContext.Type.INIT)
          || changeContext.getType().equals(NotificationContext.Type.CALLBACK)) {
        // DataAccessor dataAccessor = manager.getDataAccessor();
        HelixDataAccessor accessor = manager.getHelixDataAccessor();
        PropertyKeyBuilder keyBuilder = accessor.keyBuilder();

        while (accessor.getProperty(keyBuilder.controllerLeader()) == null) {
          boolean success = ZkHelixLeaderElection.tryUpdateController(manager);
          if (success) {
            ZkHelixLeaderElection.updateHistory(manager);
            if (type == MemberType.CONTROLLER) {
              HelixControllerMain.addListenersToController(manager, _controller);
              manager.startTimerTasks();
            } else if (type == MemberType.CONTROLLER_PARTICIPANT) {
              String clusterName = manager.getClusterName();
              String controllerName = manager.getInstanceName();
              _leader =
                  HelixManagerFactory.getZKHelixManager(clusterName, controllerName,
                      MemberType.CONTROLLER, _zkAddr);

              _leader.connect();
              _leader.startTimerTasks();
              HelixControllerMain.addListenersToController(_leader, _controller);
            }

          }
        }
      } else if (changeContext.getType().equals(NotificationContext.Type.FINALIZE)) {
        if (_leader != null) {
          _leader.disconnect();
        }
        _controller.shutdownClusterStatusMonitor(manager.getClusterName());
      }
    } catch (Exception e) {
      LOG.error("Exception when trying to become leader", e);
    }
  }
}
