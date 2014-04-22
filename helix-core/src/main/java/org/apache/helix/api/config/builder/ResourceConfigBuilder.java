package org.apache.helix.api.config.builder;

import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.helix.api.config.ResourceConfig;
import org.apache.helix.api.config.SchedulerTaskConfig;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.api.rebalancer.RebalancerConfiguration;

public abstract class ResourceConfigBuilder {

  public abstract ResourceConfigBuilder with(ResourceId id);

  /**
   * Set the rebalancer configuration
   * @param rebalancerConfig properties of interest for rebalancing
   * @return Builder
   */
  public abstract ResourceConfigBuilder rebalancerConfig(RebalancerConfiguration rebalancerConfig);

  /**
   * Set the user configuration
   * @param userConfig user-specified properties
   * @return Builder
   */
  public abstract ResourceConfigBuilder userConfig(UserConfig userConfig);

  /**
   * @param schedulerTaskConfig
   * @return
   */
  public abstract ResourceConfigBuilder schedulerTaskConfig(SchedulerTaskConfig schedulerTaskConfig);

  /**
   * Set the bucket size
   * @param bucketSize the size to use
   * @return Builder
   */
  public abstract ResourceConfigBuilder bucketSize(int bucketSize);

  /**
   * Set the batch message mode
   * @param batchMessageMode true to enable, false to disable
   * @return Builder
   */
  public abstract ResourceConfigBuilder batchMessageMode(boolean batchMessageMode);

  /**
   * Create a Resource object
   * @return instantiated Resource
   */
  public abstract ResourceConfig build();

  /**
   * Returns a new instance of the resource config builder
   * @return ResourceConfigBuilder
   */
  public static ResourceConfigBuilder newInstance() {
    try {
      return new DiscoverClass().newInstance(ResourceConfigBuilder.class,
          "org.apache.helix.core.config.builder.ResourceConfigBuilderImpl");
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }
}
