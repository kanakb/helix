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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.id.StateModelFactoryId;
import org.apache.helix.api.model.configuration.ResourceConfiguration;
import org.apache.helix.api.model.rebalancer.RebalancerConfiguration;
import org.apache.helix.api.model.statemachine.State;
import org.apache.helix.controller.rebalancer.HelixRebalancer;
import org.apache.helix.controller.rebalancer.RebalancerRef;
import org.apache.helix.controller.serializer.DefaultStringSerializer;
import org.apache.helix.controller.serializer.StringSerializer;
import org.apache.helix.model.IdealState;
import org.apache.helix.model.IdealState.RebalanceMode;

/**
 * Raw RebalancerConfig that functions for generic resources. This class is backed by an IdealState.
 */
public class BasicRebalancerConfig extends AbstractRebalancerConfig implements
    RebalancerConfiguration {
  private final IdealState _idealState;
  private final Class<? extends StringSerializer> _serializer;

  protected BasicRebalancerConfig(IdealState idealState) {
    _idealState = idealState;
    _serializer = DefaultStringSerializer.class;
  }

  @Override
  public ResourceId getResourceId() {
    return _idealState.getResourceId();
  }

  @Override
  public StateModelDefinitionId getStateModelDefId() {
    return _idealState.getStateModelDefId();
  }

  @Override
  public StateModelFactoryId getStateModelFactoryId() {
    return _idealState.getStateModelFactoryId();
  }

  @Override
  public String getParticipantGroupTag() {
    return _idealState.getInstanceGroupTag();
  }

  @Override
  public Class<? extends StringSerializer> getSerializerClass() {
    return _serializer;
  }

  @Override
  public Set<PartitionId> getPartitionSet() {
    return _idealState.getPartitionIdSet();
  }

  @Override
  public Class<? extends HelixRebalancer> getRebalancerClass() {
    return _idealState.getRebalancerRef().getRebalancerClass();
  }

  @Override
  public int getReplicaCount() {
    String replicas = _idealState.getReplicas();
    try {
      return Integer.parseInt(replicas);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  @Override
  public RebalanceMode getRebalanceMode() {
    return _idealState.getRebalanceMode();
  }

  /**
   * Safely cast a RebalancerConfig into a subtype
   * @param config RebalancerConfig object
   * @param clazz the target class
   * @return An instance of clazz, or null if the conversion is not possible
   */
  public static <T extends RebalancerConfiguration> T convert(RebalancerConfiguration config,
      Class<T> clazz) {
    try {
      return clazz.cast(config);
    } catch (ClassCastException e) {
      return null;
    }
  }

  /**
   * Get the IdealState backing this configuration
   */
  protected IdealState getIdealState() {
    return _idealState;
  }

  /**
   * Convert an ideal state into a typed BasicRebalancerConfig
   * @param idealState populated ideal state
   * @return BasicRebalancerConfig or appropriate subclass
   */
  public static BasicRebalancerConfig from(IdealState idealState) {
    switch (idealState.getRebalanceMode()) {
    case FULL_AUTO:
      return new FullAutoRebalancerConfig(idealState);
    case SEMI_AUTO:
      return new SemiAutoRebalancerConfig(idealState);
    case CUSTOMIZED:
      return new CustomRebalancerConfig(idealState);
    default:
      return new BasicRebalancerConfig(idealState);
    }
  }

  public static BasicRebalancerConfig from(ResourceConfiguration config) {
    RebalancerConfigHolder holder = new RebalancerConfigHolder(config);
    return holder.getRebalancerConfig(BasicRebalancerConfig.class);
  }

  /**
   * Convert a RebalancerConfig into an IdealState
   * @param config instantiated RebalancerConfig
   * @return IdealState corresponding to the RebalancerConfig
   */
  public static IdealState toIdealState(RebalancerConfiguration config) {
    BasicRebalancerConfig basicConfig =
        BasicRebalancerConfig.convert(config, BasicRebalancerConfig.class);
    if (basicConfig != null) {
    } else {
      AbstractRebalancerConfig abstractConfig = (AbstractRebalancerConfig) config;
      basicConfig =
          new BasicRebalancerConfig.Builder().withResourceId(config.getResourceId())
              .withMode(abstractConfig.getRebalanceMode())
              .withPartitionSet(config.getPartitionSet())
              .withReplicaCount(config.getReplicaCount())
              .withStateModelDefId(config.getStateModelDefId())
              .withStateModelFactoryId(config.getStateModelFactoryId())
              .withRebalancerClass(abstractConfig.getRebalancerClass()).build();
    }
    return basicConfig.getIdealState();
  }

  /**
   * Basic builder for a RebalancerConfig with generic configuration. For mode-specific
   * configuration, see {@link FullAutoRebalancerConfig.Builder},
   * {@link SemiAutoRebalancerConfig.Builder}, and {@link CustomRebalancerConfig.Builder}
   */
  public static class Builder extends AbstractBuilder<Builder> {
    private RebalanceMode _mode = RebalanceMode.USER_DEFINED;

    @Override
    public Builder withExistingConfig(RebalancerConfiguration config) {
      super.withExistingConfig(config);
      AbstractRebalancerConfig abstractConfig =
          BasicRebalancerConfig.convert(config, AbstractRebalancerConfig.class);
      if (abstractConfig != null) {
        _mode = abstractConfig.getRebalanceMode();
      }
      return getThis();
    }

    /**
     * Set the mode of this rebalancer
     * @param mode RebalanceMode
     * @return Builder
     */
    public Builder withMode(RebalanceMode mode) {
      _mode = mode;
      return getThis();
    }

    @Override
    protected IdealState toIdealState() {
      IdealState idealState = super.toIdealState();
      idealState.setRebalanceMode(_mode);
      return idealState;
    }

    @Override
    protected Builder getThis() {
      return this;
    }
  }

  /**
   * An abstract builder for IdealState-backed RebalancerConfig classes. Use {@link Builder},
   * {@link FullAutoRebalancerConfig.Builder}, {@link SemiAutoRebalancerConfig.Builder}, and
   * {@link CustomRebalancerConfig.Builder} for concrete builders.
   */
  public static abstract class AbstractBuilder<T extends AbstractBuilder<T>> {
    private ResourceId _resourceId;
    private StateModelDefinitionId _stateModelDefId;
    private StateModelFactoryId _stateModelFactoryId;
    private String _participantGroupTag;
    private Class<? extends HelixRebalancer> _rebalancerClass;
    private String _replicaCount;
    private Set<PartitionId> _partitionSet = new HashSet<PartitionId>();

    /**
     * Create this RebalancerConfig from an existing one
     * @param config instantiated config
     * @return Builder
     */
    public T withExistingConfig(RebalancerConfiguration config) {
      AbstractRebalancerConfig abstractConfig =
          BasicRebalancerConfig.convert(config, AbstractRebalancerConfig.class);
      if (abstractConfig != null) {
        _rebalancerClass = abstractConfig.getRebalancerClass();
      }
      _resourceId = config.getResourceId();
      _stateModelDefId = config.getStateModelDefId();
      _participantGroupTag = config.getParticipantGroupTag();
      _replicaCount = String.valueOf(config.getReplicaCount());
      _partitionSet.clear();
      _partitionSet.addAll(config.getPartitionSet());
      return getThis();
    }

    /**
     * Associate this config with a resource. Required.
     * @param resourceId the resource Id
     * @return Builder
     */
    public T withResourceId(ResourceId resourceId) {
      // TODO: enforce that this is called
      _resourceId = resourceId;
      return getThis();
    }

    /**
     * Associate this config with a state model. Required.
     * @param stateModelDefId the state model definition ID
     * @return Builder
     */
    public T withStateModelDefId(StateModelDefinitionId stateModelDefId) {
      _stateModelDefId = stateModelDefId;
      return getThis();
    }

    /**
     * Associate this config with a state model factory. Optional.
     * @param stateModelFactoryId the state model factory ID
     * @return Builder
     */
    public T withStateModelFactoryId(StateModelFactoryId stateModelFactoryId) {
      _stateModelFactoryId = stateModelFactoryId;
      return getThis();
    }

    /**
     * Associate this config with a user-defined rebalancer class. Required for user-defined
     * rebalancing mode.
     * @param rebalancerClass a class that extends {@link HelixRebalancer}
     * @return Builder
     */
    public T withRebalancerClass(Class<? extends HelixRebalancer> rebalancerClass) {
      _rebalancerClass = rebalancerClass;
      return getThis();
    }

    /**
     * Set the number of replicas per partition for this resource. This is only honored for state
     * models with the "R" dynamic upper bound. The default is 1.
     * @param replicaCount maximum number of replicas
     * @return Builder
     */
    public T withReplicaCount(int replicaCount) {
      _replicaCount = String.valueOf(replicaCount);
      return getThis();
    }

    /**
     * Set a tag for this resource. If set, only participants that have the same tag can serve this
     * resource.
     * @param participantGroupTag the tag
     * @return Builder
     */
    public T withParticipantGroupTag(String participantGroupTag) {
      _participantGroupTag = participantGroupTag;
      return getThis();
    }

    /**
     * Specify partitions to add by count. This assumes partitions are of the form
     * resourceId_partitionNum.
     * @param partitionCount the number of partitions
     * @return Builder
     */
    public T withPartitionCount(int partitionCount) {
      _partitionSet.clear();
      for (int i = 0; i < partitionCount; i++) {
        withPartition(PartitionId.from(_resourceId, Integer.toString(i)));
      }
      return getThis();
    }

    /**
     * Specify partitions by unique IDs.
     * @param partitionIds set of partition IDs
     * @return Builder
     */
    public T withPartitionSet(Collection<PartitionId> partitionIds) {
      _partitionSet.clear();
      _partitionSet.addAll(partitionIds);
      return getThis();
    }

    /**
     * Specify a single partition
     * @param partitionId partition ID
     * @return
     */
    public T withPartition(PartitionId partitionId) {
      _partitionSet.add(partitionId);
      return getThis();
    }

    /**
     * Populate an IdealState Subclasses should override this method if the backing ideal state is
     * affected by the subclassed builder.
     * @return IdealState
     */
    protected IdealState toIdealState() {
      IdealState idealState = new IdealState(_resourceId);
      if (_stateModelDefId != null) {
        idealState.setStateModelDefId(_stateModelDefId);
      }
      if (_stateModelFactoryId != null) {
        idealState.setStateModelFactoryId(_stateModelFactoryId);
      }
      idealState.setRebalancerRef(RebalancerRef.from(_rebalancerClass));
      if (_replicaCount != null) {
        idealState.setReplicas(_replicaCount);
      }
      idealState.setRebalanceMode(RebalanceMode.USER_DEFINED);
      if (_participantGroupTag != null) {
        idealState.setInstanceGroupTag(_participantGroupTag);
      }
      if (_partitionSet.isEmpty()) {
        withPartitionCount(1);
      }
      idealState.setNumPartitions(_partitionSet.size());
      for (PartitionId partitionId : _partitionSet) {
        List<ParticipantId> emptyList = Collections.emptyList();
        idealState.setPreferenceList(partitionId, emptyList);
        Map<ParticipantId, State> emptyMap = Collections.emptyMap();
        idealState.setParticipantStateMap(partitionId, emptyMap);
      }
      return idealState;
    }

    /**
     * Get a typed instance to allow switching between base and derived methods.
     * @return specific Builder instance
     */
    protected abstract T getThis();

    /**
     * Build the BasicRebalancerConfig based on invoked previous methods
     * @return BasicRebalancerConfig or subclass instance
     */
    public BasicRebalancerConfig build() {
      return BasicRebalancerConfig.from(toIdealState());
    }
  }
}
