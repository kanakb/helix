package org.apache.helix.controller.rebalancer.config;

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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.helix.api.model.id.ParticipantId;
import org.apache.helix.api.model.id.PartitionId;
import org.apache.helix.api.model.strategy.RebalancerConfiguration;
import org.apache.helix.controller.rebalancer.SemiAutoRebalancer;
import org.apache.helix.model.IdealState;
import org.apache.helix.model.IdealState.RebalanceMode;

import com.google.common.collect.Maps;

/**
 * RebalancerConfig for SEMI_AUTO rebalancer mode. It indicates the preferred locations of each
 * partition replica. By default, it corresponds to {@link SemiAutoRebalancer}
 */
public final class SemiAutoRebalancerConfig extends AbstractAutoRebalancerConfig {
  /**
   * Instantiate a SemiAutoRebalancerConfig
   */
  protected SemiAutoRebalancerConfig(IdealState idealState) {
    super(idealState);
  }

  /**
   * Get the preference list of a partition
   * @param partitionId the partition to look up
   * @return list of participant IDs
   */
  public List<ParticipantId> getPreferenceList(PartitionId partitionId) {
    List<ParticipantId> preferenceList = getIdealState().getPreferenceList(partitionId);
    if (preferenceList != null) {
      return preferenceList;
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Builder for a SEMI_AUTO RebalancerConfig
   */
  public static class Builder extends AbstractBuilder<Builder> {
    // TODO: need a way to set this in a default way
    private Map<PartitionId, List<ParticipantId>> _preferenceLists = Maps.newHashMap();

    @Override
    public Builder withExistingConfig(RebalancerConfiguration config) {
      super.withExistingConfig(config);
      SemiAutoRebalancerConfig semiAutoConfig =
          BasicRebalancerConfig.convert(config, SemiAutoRebalancerConfig.class);
      if (semiAutoConfig != null) {
        for (PartitionId partitionId : config.getPartitionSet()) {
          withPreferenceList(partitionId, semiAutoConfig.getPreferenceList(partitionId));
        }
      }
      return getThis();
    }

    /**
     * Set the ordered list of participants that can serve a partition
     * @param partitionId the partition to assign
     * @param preferenceList list of participant IDs ordered by state preference
     * @return Builder
     */
    public Builder withPreferenceList(PartitionId partitionId, List<ParticipantId> preferenceList) {
      _preferenceLists.put(partitionId, preferenceList);
      return getThis();
    }

    /**
     * Set the ordered lists of participants that can serve partitions
     * @param preferenceLists map of partition ID to list of participant ID ordered by state
     *          preference
     * @return Builder
     */
    public Builder withPreferenceLists(Map<PartitionId, List<ParticipantId>> preferenceLists) {
      _preferenceLists.clear();
      _preferenceLists.putAll(preferenceLists);
      return getThis();
    }

    @Override
    protected IdealState toIdealState() {
      IdealState idealState = super.toIdealState();
      idealState.setRebalanceMode(RebalanceMode.SEMI_AUTO);
      idealState.getRecord().setListFields(
          IdealState.stringListsFromPreferenceLists(_preferenceLists));
      return idealState;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    /**
     * Return an instantiated SemiAutoRebalancerConfig
     */
    @Override
    public SemiAutoRebalancerConfig build() {
      return new SemiAutoRebalancerConfig(toIdealState());
    }
  }
}
