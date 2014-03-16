package org.apache.helix.api.accessor;

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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.helix.HelixDataAccessor;
import org.apache.helix.PropertyKeyBuilder;
import org.apache.helix.api.model.statemachine.HelixDefinedState;
import org.apache.helix.api.model.statemachine.State;
import org.apache.helix.api.model.strategy.RebalancerConfiguration;
import org.apache.helix.api.model.PropertyKey;
import org.apache.helix.api.model.ResourceConfiguration;
import org.apache.helix.api.model.Scope;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.api.model.id.ClusterId;
import org.apache.helix.api.model.id.ParticipantId;
import org.apache.helix.api.model.id.PartitionId;
import org.apache.helix.api.model.id.ResourceId;
import org.apache.helix.api.snapshot.Resource;
import org.apache.helix.controller.rebalancer.config.BasicRebalancerConfig;
import org.apache.helix.model.ExternalView;
import org.apache.helix.model.IdealState;
import org.apache.helix.model.IdealState.RebalanceMode;
import org.apache.helix.model.ResourceAssignment;
import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

public class ResourceAccessor {
  private static final Logger LOG = Logger.getLogger(ResourceAccessor.class);
  private final ClusterId _clusterId;
  private final HelixDataAccessor _accessor;
  private final PropertyKeyBuilder _keyBuilder;

  public ResourceAccessor(ClusterId clusterId, HelixDataAccessor accessor) {
    _clusterId = clusterId;
    _accessor = accessor;
    _keyBuilder = accessor.keyBuilder();
  }

  /**
   * Read a single snapshot of a resource
   * @param resourceId the resource id to read
   * @return Resource or null if not present
   */
  public Resource readResource(ResourceId resourceId) {
    ResourceConfiguration config =
        _accessor.getProperty(_keyBuilder.resourceConfig(resourceId.stringify()));
    IdealState idealState = _accessor.getProperty(_keyBuilder.idealStates(resourceId.stringify()));

    if (config == null && idealState == null) {
      LOG.error("Resource " + resourceId + " not present on the cluster");
      return null;
    }

    ExternalView externalView =
        _accessor.getProperty(_keyBuilder.externalView(resourceId.stringify()));
    ResourceAssignment resourceAssignment =
        _accessor.getProperty(_keyBuilder.resourceAssignment(resourceId.stringify()));
    return createResource(resourceId, config, idealState, externalView, resourceAssignment);
  }

  /**
   * save resource assignment
   * @param resourceId
   * @param resourceAssignment
   * @return true if set, false otherwise
   */
  public boolean setResourceAssignment(ResourceId resourceId, ResourceAssignment resourceAssignment) {
    return _accessor.setProperty(_keyBuilder.resourceAssignment(resourceId.stringify()),
        resourceAssignment);
  }

  /**
   * get resource assignment
   * @param resourceId
   * @return resource assignment or null
   */
  public ResourceAssignment getResourceAssignment(ResourceId resourceId) {
    return _accessor.getProperty(_keyBuilder.resourceAssignment(resourceId.stringify()));
  }

  /**
   * Read the user config of the resource
   * @param resourceId the resource to to look up
   * @return UserConfig, or null
   */
  public UserConfig readUserConfig(ResourceId resourceId) {
    ResourceConfiguration resourceConfig =
        _accessor.getProperty(_keyBuilder.resourceConfig(resourceId.stringify()));
    return resourceConfig != null ? UserConfig.from(resourceConfig) : null;
  }

  /**
   * Add user configuration to the existing resource user configuration. Overwrites properties with
   * the same key
   * @param resourceId the resource to update
   * @param userConfig the user config key-value pairs to add
   * @return true if the user config was updated, false otherwise
   */
  public boolean updateUserConfig(ResourceId resourceId, UserConfig userConfig) {
    ResourceConfiguration resourceConfig = new ResourceConfiguration(resourceId);
    resourceConfig.addNamespacedConfig(userConfig);
    return _accessor.updateProperty(_keyBuilder.resourceConfig(resourceId.stringify()),
        resourceConfig);
  }

  /**
   * Get a resource configuration, which may include user-defined configuration, as well as
   * rebalancer configuration
   * @param resourceId
   * @return configuration or null
   */
  public ResourceConfiguration getConfiguration(ResourceId resourceId) {
    return _accessor.getProperty(_keyBuilder.resourceConfig(resourceId.stringify()));
  }

  /**
   * set external view of a resource
   * @param resourceId
   * @param extView
   * @return true if set, false otherwise
   */
  public boolean setExternalView(ResourceId resourceId, ExternalView extView) {
    return _accessor.setProperty(_keyBuilder.externalView(resourceId.stringify()), extView);
  }

  /**
   * get the external view of a resource
   * @param resourceId the resource to look up
   * @return external view or null
   */
  public ExternalView readExternalView(ResourceId resourceId) {
    return _accessor.getProperty(_keyBuilder.externalView(resourceId.stringify()));
  }

  /**
   * drop external view of a resource
   * @param resourceId
   * @return true if dropped, false otherwise
   */
  public boolean dropExternalView(ResourceId resourceId) {
    return _accessor.removeProperty(_keyBuilder.externalView(resourceId.stringify()));
  }

  /**
   * reset resources for all participants
   * @param resetResourceIdSet the resources to reset
   * @return true if they were reset, false otherwise
   */
  public boolean resetResources(Set<ResourceId> resetResourceIdSet) {
    ParticipantAccessor accessor = participantAccessor();
    List<ExternalView> extViews = _accessor.getChildValues(_keyBuilder.externalViews());
    for (ExternalView extView : extViews) {
      if (!resetResourceIdSet.contains(extView.getResourceId())) {
        continue;
      }

      Map<ParticipantId, Set<PartitionId>> resetPartitionIds = Maps.newHashMap();
      for (PartitionId partitionId : extView.getPartitionIdSet()) {
        Map<ParticipantId, State> stateMap = extView.getStateMap(partitionId);
        for (ParticipantId participantId : stateMap.keySet()) {
          State state = stateMap.get(participantId);
          if (state.equals(HelixDefinedState.from(HelixDefinedState.ERROR))) {
            if (!resetPartitionIds.containsKey(participantId)) {
              resetPartitionIds.put(participantId, new HashSet<PartitionId>());
            }
            resetPartitionIds.get(participantId).add(partitionId);
          }
        }
      }
      for (ParticipantId participantId : resetPartitionIds.keySet()) {
        accessor.resetPartitionsForParticipant(participantId, extView.getResourceId(),
            resetPartitionIds.get(participantId));
      }
    }
    return true;
  }

  /**
   * Create a resource snapshot instance from the physical model
   * @param resourceId the resource id
   * @param resourceConfiguration physical resource configuration
   * @param idealState ideal state of the resource
   * @param externalView external view of the resource
   * @param resourceAssignment current resource assignment
   * @return Resource
   */
  static Resource createResource(ResourceId resourceId,
      ResourceConfiguration resourceConfiguration, IdealState idealState,
      ExternalView externalView, ResourceAssignment resourceAssignment) {
    UserConfig userConfig;
    RebalancerConfiguration rebalancerConfig = null;
    if (resourceConfiguration != null) {
      userConfig = resourceConfiguration.getUserConfig();
    } else {
      userConfig = new UserConfig(Scope.resource(resourceId));
    }
    int bucketSize = 0;
    boolean batchMessageMode = false;
    if (idealState != null) {
      if (resourceConfiguration != null
          && idealState.getRebalanceMode() == RebalanceMode.USER_DEFINED) {
        // prefer rebalancer config for user_defined data rebalancing
        rebalancerConfig = resourceConfiguration.getRebalancerConfiguration();
      }
      if (rebalancerConfig == null) {
        // prefer ideal state for non-user_defined data rebalancing
        rebalancerConfig = BasicRebalancerConfig.from(idealState);
      }
      bucketSize = idealState.getBucketSize();
      batchMessageMode = idealState.getBatchMessageMode();
      idealState.updateUserConfig(userConfig);
    } else if (resourceConfiguration != null) {
      bucketSize = resourceConfiguration.getBucketSize();
      batchMessageMode = resourceConfiguration.getBatchMessageMode();
      rebalancerConfig = resourceConfiguration.getRebalancerConfiguration();
    }
    if (rebalancerConfig == null) {
      rebalancerConfig = new BasicRebalancerConfig.Builder().build();
    }
    return new Resource(resourceId, idealState, resourceAssignment, externalView, rebalancerConfig,
        userConfig, bucketSize, batchMessageMode);
  }

  /**
   * Get a ParticipantAccessor instance
   * @return ParticipantAccessor
   */
  protected ParticipantAccessor participantAccessor() {
    return new ParticipantAccessor(_clusterId, _accessor);
  }

  /**
   * Get the cluster ID this accessor is connected to
   * @return ClusterId
   */
  protected ClusterId clusterId() {
    return _clusterId;
  }

  /**
   * Get the accessor for the properties stored for this cluster
   * @return HelixDataAccessor
   */
  protected HelixDataAccessor dataAccessor() {
    return _accessor;
  }
}
