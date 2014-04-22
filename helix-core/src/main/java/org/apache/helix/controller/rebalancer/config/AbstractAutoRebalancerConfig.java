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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.helix.HelixConstants.StateModelToken;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.rebalancer.RebalancerConfiguration;
import org.apache.helix.model.IdealState;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A config with properties common to SEMI_AUTO and FULL_AUTO rebalance modes.
 */
public abstract class AbstractAutoRebalancerConfig extends BasicRebalancerConfig {
  protected AbstractAutoRebalancerConfig(IdealState idealState) {
    super(idealState);
  }

  /**
   * Check if this partition should be assigned to any live participant
   * @return true if any live participant expected, false otherwise
   */
  public boolean getAnyLiveParticipant(PartitionId partitionId) {
    String replicas = getIdealState().getReplicas();
    if (replicas.equals(StateModelToken.ANY_LIVEINSTANCE)) {
      return true;
    }
    List<ParticipantId> preferenceList = getIdealState().getPreferenceList(partitionId);
    if (preferenceList != null && preferenceList.size() == 1) {
      ParticipantId participantId = preferenceList.get(0);
      if (participantId.toString().equals(StateModelToken.ANY_LIVEINSTANCE.toString())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Abstract builder for an AbstractAutoRebalancerConfig. This should never be used directly except
   * in subclass builders.
   */
  protected static abstract class AbstractBuilder<T extends BasicRebalancerConfig.AbstractBuilder<T>>
      extends BasicRebalancerConfig.AbstractBuilder<T> {
    private Set<PartitionId> _partitionsWithAnyLive = Sets.newHashSet();

    @Override
    public T withExistingConfig(RebalancerConfiguration config) {
      super.withExistingConfig(config);
      AbstractAutoRebalancerConfig abstractConfig =
          BasicRebalancerConfig.convert(config, AbstractAutoRebalancerConfig.class);
      if (abstractConfig != null) {
        for (PartitionId partitionId : config.getPartitionSet()) {
          if (abstractConfig.getAnyLiveParticipant(partitionId)) {
            _partitionsWithAnyLive.add(partitionId);
          }
        }
      }
      return getThis();
    }

    /**
     * Indicate that a partition can be assigned to any live participant.
     * @param partitionId the partition to assign
     * @return Builder
     */
    public T withPartitionAcceptingAnyLiveParticipant(PartitionId partitionId) {
      _partitionsWithAnyLive.add(partitionId);
      return getThis();
    }

    /**
     * Indicate a set of partitions that can be assigned to any live participant
     * @param partitionIds the partition to assign
     * @return Builder
     */
    public T withPartitionsAcceptingAnyLiveParticipant(Collection<PartitionId> partitionIds) {
      for (PartitionId partitionId : partitionIds) {
        withPartitionAcceptingAnyLiveParticipant(partitionId);
      }
      return getThis();
    }

    @Override
    protected IdealState toIdealState() {
      IdealState idealState = super.toIdealState();
      if (_partitionsWithAnyLive.equals(idealState.getPartitionIdSet())) {
        idealState.setReplicas(StateModelToken.ANY_LIVEINSTANCE.toString());
      }
      for (PartitionId partitionId : _partitionsWithAnyLive) {
        List<ParticipantId> preferenceList =
            Lists.newArrayList(ParticipantId.from(StateModelToken.ANY_LIVEINSTANCE.toString()));
        idealState.setPreferenceList(partitionId, preferenceList);
      }
      return idealState;
    }
  }
}
