package org.apache.helix.controller.rebalancer.config;

import org.apache.helix.api.model.configuration.RebalancerConfiguration;
import org.apache.helix.controller.rebalancer.FullAutoRebalancer;
import org.apache.helix.model.IdealState;

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
 * RebalancerConfig for FULL_AUTO rebalancing mode. By default, it corresponds to
 * {@link FullAutoRebalancer}
 */
public final class FullAutoRebalancerConfig extends AbstractAutoRebalancerConfig {

  protected FullAutoRebalancerConfig(IdealState idealState) {
    super(idealState);
  }

  /**
   * Get the maximum number of partitions that a participant can serve
   * @return maximum number of partitions per participant
   */
  public int getMaxPartitionsPerParticipant() {
    return getIdealState().getMaxPartitionsPerInstance();
  }

  /**
   * Builder for a FULL_AUTO RebalancerConfig
   */
  public static class Builder extends AbstractBuilder<Builder> {
    private Integer _maxPartitionsPerParticipant;

    @Override
    public Builder withExistingConfig(RebalancerConfiguration config) {
      super.withExistingConfig(config);
      FullAutoRebalancerConfig fullAutoConfig =
          BasicRebalancerConfig.convert(config, FullAutoRebalancerConfig.class);
      if (fullAutoConfig != null) {
        withMaxPartitionsPerParticipant(fullAutoConfig.getMaxPartitionsPerParticipant());
      }
      return getThis();
    }

    /**
     * Set the number of partitions that a participant can serve at a maximum
     * @param maxPartitionsPerParticipant the maximum as an integer
     * @return Builder
     */
    public Builder withMaxPartitionsPerParticipant(int maxPartitionsPerParticipant) {
      _maxPartitionsPerParticipant = maxPartitionsPerParticipant;
      return getThis();
    }

    @Override
    protected IdealState toIdealState() {
      IdealState idealState = super.toIdealState();
      idealState.setRebalanceMode(RebalanceMode.FULL_AUTO);
      if (_maxPartitionsPerParticipant != null) {
        idealState.setMaxPartitionsPerInstance(_maxPartitionsPerParticipant);
      }
      return idealState;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    /**
     * Return an instantiated FullAutoRebalancerConfig
     */
    @Override
    public FullAutoRebalancerConfig build() {
      return new FullAutoRebalancerConfig(toIdealState());
    }
  }
}
