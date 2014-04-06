package org.apache.helix.api.snapshot;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.helix.api.config.Partition;
import org.apache.helix.api.config.ResourceConfig;
import org.apache.helix.api.config.SchedulerTaskConfig;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.api.model.ipc.Message;
import org.apache.helix.api.model.rebalancer.RebalancerConfiguration;
import org.apache.helix.model.IdealState;
import org.apache.helix.model.ResourceAssignment;
import org.apache.helix.model.composite.ExternalView;

import com.google.common.collect.Maps;

/**
 * Represent a resource entity in helix cluster
 */
public class Resource {
  private final ResourceConfig _config;
  private final ExternalView _externalView;
  private final ResourceAssignment _resourceAssignment;
  private final IdealState _idealState;

  /**
   * Construct a resource
   * @param id resource id
   * @param idealState ideal state of the resource
   * @param externalView external view of the resource
   * @param resourceAssignment current resource assignment of the cluster
   * @param rebalancerConfig parameters that the rebalancer should be aware of
   * @param userConfig any resource user-defined configuration
   * @param bucketSize the bucket size to use for physically saved state
   * @param batchMessageMode true if batch messaging allowed, false otherwise
   */
  public Resource(ResourceId id, IdealState idealState, ResourceAssignment resourceAssignment,
      ExternalView externalView, RebalancerConfiguration rebalancerConfig, UserConfig userConfig,
      int bucketSize, boolean batchMessageMode) {
    SchedulerTaskConfig schedulerTaskConfig = schedulerTaskConfig(idealState);
    Map<PartitionId, Partition> partitionMap = Maps.newHashMap();
    for (PartitionId partitionId : rebalancerConfig.getPartitionSet()) {
      partitionMap.put(partitionId, new Partition(partitionId));
    }
    _config =
        new ResourceConfig(id, partitionMap, schedulerTaskConfig, rebalancerConfig, userConfig,
            bucketSize, batchMessageMode);
    _externalView = externalView;
    _resourceAssignment = resourceAssignment;
    _idealState = idealState;
  }

  /**
   * Extract scheduler-task config from ideal-state if state-model-def is SchedulerTaskQueue
   * @param idealState
   * @return scheduler-task config or null if state-model-def is not SchedulerTaskQueue
   */
  SchedulerTaskConfig schedulerTaskConfig(IdealState idealState) {
    if (idealState == null) {
      return null;
    }
    // TODO refactor get timeout
    Map<String, Integer> transitionTimeoutMap = new HashMap<String, Integer>();
    for (String simpleKey : idealState.getRecord().getSimpleFields().keySet()) {
      if (simpleKey.indexOf(Message.Attributes.TIMEOUT.name()) != -1) {
        try {
          String timeoutStr = idealState.getRecord().getSimpleField(simpleKey);
          int timeout = Integer.parseInt(timeoutStr);
          transitionTimeoutMap.put(simpleKey, timeout);
        } catch (Exception e) {
          // ignore
        }
      }
    }

    Map<PartitionId, Message> innerMsgMap = new HashMap<PartitionId, Message>();
    if (idealState.getStateModelDefId().equalsIgnoreCase(StateModelDefinitionId.SCHEDULER_TASK_QUEUE)) {
      for (PartitionId partitionId : idealState.getPartitionIdSet()) {
        // TODO refactor: scheduler-task-queue state model uses map-field to store inner-messages
        // this is different from all other state-models
        Map<String, String> innerMsgStrMap =
            idealState.getRecord().getMapField(partitionId.toString());
        if (innerMsgStrMap != null) {
          Message innerMsg = Message.toMessage(innerMsgStrMap);
          innerMsgMap.put(partitionId, innerMsg);
        }
      }
    }

    // System.out.println("transitionTimeoutMap: " + transitionTimeoutMap);
    // System.out.println("innerMsgMap: " + innerMsgMap);
    return new SchedulerTaskConfig(transitionTimeoutMap, innerMsgMap);
  }

  /**
   * Get the partitions of the resource
   * @return map of partition id to partition or empty map if none
   */
  public Map<PartitionId, Partition> getPartitionMap() {
    return _config.getPartitionMap();
  }

  /**
   * Get a partition that the resource contains
   * @param partitionId the partitionId id to look up
   * @return Partition or null if none is present with the given id
   */
  public Partition getPartition(PartitionId partitionId) {
    return _config.getPartition(partitionId);
  }

  /**
   * Get the set of subunit ids that the resource contains
   * @return subunit id set, or empty if none
   */
  public Set<? extends PartitionId> getPartitionSet() {
    return _config.getPartitionIdSet();
  }

  /**
   * Get the external view of the resource
   * @return the external view of the resource
   */
  public ExternalView getExternalView() {
    return _externalView;
  }

  /**
   * Get the current resource assignment
   * @return ResourceAssignment, or null if no current assignment
   */
  public ResourceAssignment getResourceAssignment() {
    return _resourceAssignment;
  }

  /**
   * Get the resource properties configuring rebalancing
   * @return RebalancerConfig properties
   */
  public RebalancerConfiguration getRebalancerConfig() {
    return _config.getRebalancerConfig();
  }

  /**
   * Get user-specified configuration properties of this resource
   * @return UserConfig properties
   */
  public UserConfig getUserConfig() {
    return _config.getUserConfig();
  }

  /**
   * Get the resource id
   * @return ResourceId
   */
  public ResourceId getId() {
    return _config.getId();
  }

  /**
   * Get the properties configuring scheduler tasks
   * @return SchedulerTaskConfig properties
   */
  public SchedulerTaskConfig getSchedulerTaskConfig() {
    return _config.getSchedulerTaskConfig();
  }

  /**
   * Get bucket size
   * @return bucket size
   */
  public int getBucketSize() {
    return _config.getBucketSize();
  }

  /**
   * Get batch message mode
   * @return true if in batch message mode, false otherwise
   */
  public boolean getBatchMessageMode() {
    return _config.getBatchMessageMode();
  }

  /**
   * Get the configuration of this resource
   * @return ResourceConfig that backs this Resource
   */
  public ResourceConfig getConfig() {
    return _config;
  }

  /**
   * Get the ideal state of the resource
   * @return IdealState for this resource, if it exists
   */
  public IdealState getIdealState() {
    return _idealState;
  }
}
