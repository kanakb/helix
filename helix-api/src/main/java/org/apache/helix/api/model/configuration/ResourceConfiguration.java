package org.apache.helix.api.model.configuration;

import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.model.HelixProperty;
import org.apache.helix.api.model.NamespacedConfig;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.api.model.ZNRecord;

import com.google.common.base.Enums;
import com.google.common.base.Optional;

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
 * Persisted configuration properties for a resource
 */
public class ResourceConfiguration extends HelixProperty implements HelixConfiguration {
  private enum Fields {
  }

  private RebalancerConfiguration rebalancerConfiguration;

  /**
   * Instantiate for an id
   * @param id resource id
   */
  public ResourceConfiguration(ResourceId id) {
    super(id.toString());
  }

  /**
   * Get the resource that is rebalanced
   * @return resource id
   */
  public ResourceId getResourceId() {
    return ResourceId.from(getId());
  }

  /**
   * Instantiate from a record
   * @param record configuration properties
   * @deprecated Creating resource configuration from a ZNRecord
   *             will be removed from API, the code will be moved to SPI
   */
  @Deprecated
  public ResourceConfiguration(ZNRecord record) {
    super(record);
  }

  /**
   * Get a backward-compatible resource user config
   * @return UserConfig
   */
  public UserConfig getUserConfig() {
    UserConfig userConfig = UserConfig.from(this);
    for (String simpleField : _record.getSimpleFields().keySet()) {
      Optional<Fields> enumField = Enums.getIfPresent(Fields.class, simpleField);
      if (!simpleField.contains(NamespacedConfig.PREFIX_CHAR + "") && !enumField.isPresent()) {
        userConfig.setSimpleField(simpleField, _record.getSimpleField(simpleField));
      }
    }
    for (String listField : _record.getListFields().keySet()) {
      if (!listField.contains(NamespacedConfig.PREFIX_CHAR + "")) {
        userConfig.setListField(listField, _record.getListField(listField));
      }
    }
    for (String mapField : _record.getMapFields().keySet()) {
      if (!mapField.contains(NamespacedConfig.PREFIX_CHAR + "")) {
        userConfig.setMapField(mapField, _record.getMapField(mapField));
      }
    }
    return userConfig;
  }

  /**
   * Get a RebalancerConfig if available
   * @return RebalancerConfig, or null
   */
  // public <T extends RebalancerConfig> T getRebalancerConfig(Class<T> clazz) {
  // RebalancerConfigHolder config = new RebalancerConfigHolder(this);
  // return config.getRebalancerConfig(clazz);
  // }
  //
  // /**
  // * Check if this resource config has a rebalancer config
  // * @return true if a rebalancer config is attached, false otherwise
  // */
  // public boolean hasRebalancerConfig() {
  // return _record.getSimpleFields().containsKey(
  // RebalancerConfigHolder.class.getSimpleName() + NamespacedConfig.PREFIX_CHAR
  // + RebalancerConfigHolder.Fields.REBALANCER_CONFIG);
  // }

  public boolean hasRebalancerConfiguration() {
    return rebalancerConfiguration != null;
  }

  public void setRebalancerConfiguration(RebalancerConfiguration config) {
    rebalancerConfiguration = config;
  }

  public RebalancerConfiguration getRebalancerConfiguration() {
    return rebalancerConfiguration;
  }
}
