package org.apache.helix.api.config;

import java.util.Map;
import java.util.Set;

import org.apache.helix.api.config.builder.ResourceConfigBuilder;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.api.model.id.PartitionId;
import org.apache.helix.api.model.id.ResourceId;
import org.apache.helix.api.model.strategy.RebalancerConfiguration;

import com.google.common.collect.Sets;

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

/**
 * Full configuration of a Helix resource. Typically used to add or modify resources on a cluster
 */
public class ResourceConfig {
  private final ResourceId _id;
  private final Map<PartitionId, Partition> _partitionMap;
  private final RebalancerConfiguration _rebalancerConfig;
  private final SchedulerTaskConfig _schedulerTaskConfig;
  private final UserConfig _userConfig;
  private final int _bucketSize;
  private final boolean _batchMessageMode;

  /**
   * Instantiate a configuration. Consider using ResourceConfig.Builder
   * @param id resource id
   * @param partitionMap map of partition identifiers to partition objects
   * @param schedulerTaskConfig configuration for scheduler tasks associated with the resource
   * @param rebalancerConfig configuration for rebalancing the resource
   * @param userConfig user-defined resource properties
   * @param bucketSize bucket size for this resource
   * @param batchMessageMode whether or not batch messaging is allowed
   */
  public ResourceConfig(ResourceId id, Map<PartitionId, Partition> partitionMap,
      SchedulerTaskConfig schedulerTaskConfig, RebalancerConfiguration rebalancerConfig,
      UserConfig userConfig, int bucketSize, boolean batchMessageMode) {
    _id = id;
    _partitionMap = partitionMap;
    _schedulerTaskConfig = schedulerTaskConfig;
    _rebalancerConfig = rebalancerConfig;
    _userConfig = userConfig;
    _bucketSize = bucketSize;
    _batchMessageMode = batchMessageMode;
  }

  /**
   * Get the partitions of the resource
   * @return map of partition id to partition or empty map if none
   */
  public Map<PartitionId, Partition> getPartitionMap() {
    return _partitionMap;
  }

  /**
   * Get a partition that the resource contains
   * @param partitionId the partitionId id to look up
   * @return Partition or null if none is present with the given id
   */
  public Partition getPartition(PartitionId subUnitId) {
    return getPartitionMap().get(subUnitId);
  }

  /**
   * Get the set of partition ids that the resource contains
   * @return partition id set, or empty if none
   */
  public Set<PartitionId> getPartitionIdSet() {
    return getPartitionMap().keySet();
  }

  /**
   * Get the resource properties configuring rebalancing
   * @return RebalancerConfig properties
   */
  public RebalancerConfiguration getRebalancerConfig() {
    return _rebalancerConfig;
  }

  /**
   * Get the resource id
   * @return ResourceId
   */
  public ResourceId getId() {
    return _id;
  }

  /**
   * Get the properties configuring scheduler tasks
   * @return SchedulerTaskConfig properties
   */
  public SchedulerTaskConfig getSchedulerTaskConfig() {
    return _schedulerTaskConfig;
  }

  /**
   * Get user-specified configuration properties of this resource
   * @return UserConfig properties
   */
  public UserConfig getUserConfig() {
    return _userConfig;
  }

  /**
   * Get the bucket size for this resource
   * @return bucket size
   */
  public int getBucketSize() {
    return _bucketSize;
  }

  /**
   * Get the batch message mode
   * @return true if enabled, false if disabled
   */
  public boolean getBatchMessageMode() {
    return _batchMessageMode;
  }

  @Override
  public String toString() {
    return getPartitionMap().toString();
  }

  /**
   * Update context for a ResourceConfig
   */
  public static class Delta {
    private enum Fields {
      REBALANCER_CONFIG,
      USER_CONFIG,
      BUCKET_SIZE,
      BATCH_MESSAGE_MODE
    }

    private Set<Fields> _updateFields;
    private ResourceConfigBuilder _builder;

    /**
     * Instantiate the delta for a resource config
     * @param resourceId the resource to update
     */
    public Delta(ResourceId resourceId) {
      _builder = ResourceConfigBuilder.newInstance().with(resourceId);
      _updateFields = Sets.newHashSet();
    }

    /**
     * Set the rebalancer configuration
     * @param config properties of interest for rebalancing
     * @return Delta
     */
    public Delta setRebalancerConfig(RebalancerConfiguration config) {
      _builder.rebalancerConfig(config);
      _updateFields.add(Fields.REBALANCER_CONFIG);
      return this;
    }

    /**
     * Set the user configuration
     * @param userConfig user-specified properties
     * @return Delta
     */
    public Delta setUserConfig(UserConfig userConfig) {
      _builder.userConfig(userConfig);
      _updateFields.add(Fields.USER_CONFIG);
      return this;
    }

    /**
     * Set the bucket size
     * @param bucketSize the size to use
     * @return Delta
     */
    public Delta setBucketSize(int bucketSize) {
      _builder.bucketSize(bucketSize);
      _updateFields.add(Fields.BUCKET_SIZE);
      return this;
    }

    /**
     * Set the batch message mode
     * @param batchMessageMode true to enable, false to disable
     * @return Delta
     */
    public Delta setBatchMessageMode(boolean batchMessageMode) {
      _builder.batchMessageMode(batchMessageMode);
      _updateFields.add(Fields.BATCH_MESSAGE_MODE);
      return this;
    }

    /**
     * Create a ResourceConfig that is the combination of an existing ResourceConfig and this delta
     * @param orig the original ResourceConfig
     * @return updated ResourceConfig
     */
    public ResourceConfig mergeInto(ResourceConfig orig) {
      ResourceConfig deltaConfig = _builder.build();
      ResourceConfigBuilder builder =
          ResourceConfigBuilder.newInstance().with(orig.getId())
              .rebalancerConfig(orig.getRebalancerConfig())
              .schedulerTaskConfig(orig.getSchedulerTaskConfig()).userConfig(orig.getUserConfig())
              .bucketSize(orig.getBucketSize()).batchMessageMode(orig.getBatchMessageMode());
      for (Fields field : _updateFields) {
        switch (field) {
        case REBALANCER_CONFIG:
          builder.rebalancerConfig(deltaConfig.getRebalancerConfig());
          break;
        case USER_CONFIG:
          builder.userConfig(deltaConfig.getUserConfig());
          break;
        case BUCKET_SIZE:
          builder.bucketSize(deltaConfig.getBucketSize());
          break;
        case BATCH_MESSAGE_MODE:
          builder.batchMessageMode(deltaConfig.getBatchMessageMode());
          break;
        }
      }
      return builder.build();
    }
  }

}
