package org.apache.helix.controller.rebalancer.config;

import java.util.Collections;
import java.util.Map;

import org.apache.helix.api.model.id.ParticipantId;
import org.apache.helix.api.model.id.PartitionId;
import org.apache.helix.api.model.statemachine.State;
import org.apache.helix.api.model.strategy.RebalancerConfiguration;
import org.apache.helix.controller.rebalancer.CustomRebalancer;
import org.apache.helix.model.IdealState;
import org.apache.helix.model.IdealState.RebalanceMode;

import com.google.common.collect.Maps;

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
 * RebalancerConfig for a resource that should be rebalanced in CUSTOMIZED mode. By default, it
 * corresponds to {@link CustomRebalancer}
 */
public final class CustomRebalancerConfig extends BasicRebalancerConfig {
  /**
   * Instantiate a CustomRebalancerConfig
   */
  protected CustomRebalancerConfig(IdealState idealState) {
    super(idealState);
  }

  /**
   * Get the preference map of a partition
   * @param partitionId the partition to look up
   * @return map of participant to state
   */
  public Map<ParticipantId, State> getPreferenceMap(PartitionId partitionId) {
    Map<ParticipantId, State> preferenceMap = getIdealState().getParticipantStateMap(partitionId);
    if (preferenceMap != null) {
      return preferenceMap;
    } else {
      return Collections.emptyMap();
    }
  }

  /**
   * Builder for a CUSTOMIZED RebalancerConfig
   */
  public static class Builder extends AbstractBuilder<Builder> {
    // TODO: need a way to set this in a default way
    private Map<PartitionId, Map<ParticipantId, State>> _preferenceMaps = Maps.newHashMap();

    @Override
    public Builder withExistingConfig(RebalancerConfiguration config) {
      super.withExistingConfig(config);
      CustomRebalancerConfig customConfig =
          BasicRebalancerConfig.convert(config, CustomRebalancerConfig.class);
      if (customConfig != null) {
        for (PartitionId partitionId : config.getPartitionSet()) {
          withPreferenceMap(partitionId, customConfig.getPreferenceMap(partitionId));
        }
      }
      return getThis();
    }

    /**
     * Add a preference map for a partition. These are the participants and states that Helix will
     * assign for this partition.
     * @param partitionId the partition to assign
     * @param preferenceMap the participants that serve the partition and the state at each
     *          participant
     * @return Builder
     */
    public Builder withPreferenceMap(PartitionId partitionId,
        Map<ParticipantId, State> preferenceMap) {
      _preferenceMaps.put(partitionId, preferenceMap);
      return getThis();
    }

    /**
     * Add a collection of preference maps for a partition. These are the participants and states
     * that Helix will assign for each partition.
     * @param preferenceMaps map of partition ID to participant ID to state
     * @return Builder
     */
    public Builder withPreferenceMaps(Map<PartitionId, Map<ParticipantId, State>> preferenceMaps) {
      _preferenceMaps.clear();
      _preferenceMaps.putAll(preferenceMaps);
      return getThis();
    }

    @Override
    protected IdealState toIdealState() {
      IdealState idealState = super.toIdealState();
      idealState.setRebalanceMode(RebalanceMode.CUSTOMIZED);
      idealState.getRecord().setMapFields(
          IdealState.stringMapsFromParticipantStateMaps(_preferenceMaps));
      return idealState;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    /**
     * Return an instantiated CustomRebalancerConfig
     */
    @Override
    public CustomRebalancerConfig build() {
      return new CustomRebalancerConfig(toIdealState());
    }
  }
}
