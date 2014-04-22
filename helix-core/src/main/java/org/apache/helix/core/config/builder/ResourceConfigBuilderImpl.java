package org.apache.helix.core.config.builder;

import java.util.Map;

import org.apache.helix.api.config.Partition;
import org.apache.helix.api.config.ResourceConfig;
import org.apache.helix.api.config.SchedulerTaskConfig;
import org.apache.helix.api.config.builder.ResourceConfigBuilder;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;
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
import org.apache.helix.api.model.Scope;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.api.rebalancer.RebalancerConfiguration;

import com.google.common.collect.Maps;

/**
 */
public class ResourceConfigBuilderImpl extends ResourceConfigBuilder {
  /**
   * Assembles a ResourceConfig
   */
  private ResourceId _id;
  private RebalancerConfiguration _rebalancerConfig;
  private SchedulerTaskConfig _schedulerTaskConfig;
  private UserConfig _userConfig;
  private int _bucketSize;
  private boolean _batchMessageMode;

  /**
   * Build a Resource with an id
   * @param id resource id
   */
  public ResourceConfigBuilderImpl() {

  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.core.config.builder.ResourceConfigBuilder#with(org.apache.helix.api.id.ResourceId
   * )
   */
  @Override
  public ResourceConfigBuilder with(ResourceId id) {
    _id = id;
    _bucketSize = 0;
    _batchMessageMode = false;
    _userConfig = new UserConfig(Scope.resource(id));
    return this;
  }

  /**
   * Set the rebalancer configuration
   * @param rebalancerConfig properties of interest for rebalancing
   * @return Builder
   */
  @Override
  public ResourceConfigBuilder rebalancerConfig(RebalancerConfiguration rebalancerConfig) {
    _rebalancerConfig = rebalancerConfig;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.core.config.builder.ResourceConfigBuilder#userConfig(org.apache.helix.api.
   * config.UserConfig)
   */
  @Override
  public ResourceConfigBuilder userConfig(UserConfig userConfig) {
    _userConfig = userConfig;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.core.config.builder.ResourceConfigBuilder#schedulerTaskConfig(org.apache.helix
   * .api.config.SchedulerTaskConfig)
   */
  @Override
  public ResourceConfigBuilder schedulerTaskConfig(SchedulerTaskConfig schedulerTaskConfig) {
    _schedulerTaskConfig = schedulerTaskConfig;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.core.config.builder.ResourceConfigBuilder#bucketSize(int)
   */
  @Override
  public ResourceConfigBuilder bucketSize(int bucketSize) {
    _bucketSize = bucketSize;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.core.config.builder.ResourceConfigBuilder#batchMessageMode(boolean)
   */
  @Override
  public ResourceConfigBuilder batchMessageMode(boolean batchMessageMode) {
    _batchMessageMode = batchMessageMode;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.core.config.builder.ResourceConfigBuilder#build()
   */
  @Override
  public ResourceConfig build() {
    // TODO: when we support partition user configs, allow passing in fully-qualified partition
    // instances
    Map<PartitionId, Partition> partitionMap = Maps.newHashMap();
    for (PartitionId partitionId : _rebalancerConfig.getPartitionSet()) {
      partitionMap.put(partitionId, new Partition(partitionId));
    }
    return new ResourceConfig(_id, partitionMap, _schedulerTaskConfig, _rebalancerConfig,
        _userConfig, _bucketSize, _batchMessageMode);
  }
}
