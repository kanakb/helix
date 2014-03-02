package org.apache.helix.core.config.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.helix.api.config.ClusterConfig;
import org.apache.helix.api.model.IClusterConstraints;
import org.apache.helix.api.model.IConstraintItem;
import org.apache.helix.model.ClusterConstraints;
import org.apache.helix.model.builder.ConstraintItemBuilder;
import org.apache.helix.api.config.ParticipantConfig;
import org.apache.helix.api.config.ResourceConfig;
import org.apache.helix.api.config.Scope;
import org.apache.helix.api.config.UserConfig;
import org.apache.helix.api.config.builder.ClusterConfigBuilder;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.ConstraintId;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefId;
import org.apache.helix.api.model.IStateModelDefinition;
import org.apache.helix.api.model.ITransition;
import org.apache.helix.api.model.IClusterConstraints.ConstraintAttribute;
import org.apache.helix.api.model.IClusterConstraints.ConstraintType;
import org.apache.helix.api.model.IMessage.MessageType;
import org.apache.log4j.Logger;

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

public class ClusterConfigBuilderImpl extends ClusterConfigBuilder {
  private static final Logger LOG = Logger.getLogger(ClusterConfigBuilderImpl.class);
  /**
   * Assembles a cluster configuration
   */
  private ClusterId _id;
  private Map<ResourceId, ResourceConfig> _resourceMap;
  private Map<ParticipantId, ParticipantConfig> _participantMap;
  private Map<ConstraintType, IClusterConstraints> _constraintMap;
  private Map<StateModelDefId, IStateModelDefinition> _stateModelMap;
  private UserConfig _userConfig;
  private boolean _isPaused;
  private boolean _autoJoin;

  /**
   * Initialize builder for a cluster
   * @param id cluster id
   */
  public ClusterConfigBuilderImpl() {

  }

  public ClusterConfigBuilder withClusterId(ClusterId id) {
    _id = id;
    _resourceMap = new HashMap<ResourceId, ResourceConfig>();
    _participantMap = new HashMap<ParticipantId, ParticipantConfig>();
    _constraintMap = new HashMap<ConstraintType, IClusterConstraints>();
    _stateModelMap = new HashMap<StateModelDefId, IStateModelDefinition>();
    _isPaused = false;
    _autoJoin = false;
    _userConfig = new UserConfig(Scope.cluster(id));
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.api.config.ClusterConfigBuilder#addResource(org.apache.helix.api.config.
   * ResourceConfig)
   */
  @Override
  public ClusterConfigBuilder addResource(ResourceConfig resource) {
    _resourceMap.put(resource.getId(), resource);
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.api.config.ClusterConfigBuilder#addResources(java.util.Collection)
   */
  @Override
  public ClusterConfigBuilder addResources(Collection<ResourceConfig> resources) {
    for (ResourceConfig resource : resources) {
      addResource(resource);
    }
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.api.config.ClusterConfigBuilder#addParticipant(org.apache.helix.api.config
   * .ParticipantConfig)
   */
  @Override
  public ClusterConfigBuilder addParticipant(ParticipantConfig participant) {
    _participantMap.put(participant.getId(), participant);
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.api.config.ClusterConfigBuilder#addParticipants(java.util.Collection)
   */
  @Override
  public ClusterConfigBuilder addParticipants(Collection<ParticipantConfig> participants) {
    for (ParticipantConfig participant : participants) {
      addParticipant(participant);
    }
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.api.config.ClusterConfigBuilder#addConstraint(org.apache.helix.api.model.
   * IClusterConstraints)
   */
  @Override
  public ClusterConfigBuilder addConstraint(IClusterConstraints constraint) {
    IClusterConstraints existConstraints = getConstraintsInstance(constraint.getType());
    for (ConstraintId constraintId : constraint.getConstraintItems().keySet()) {
      existConstraints.addConstraintItem(constraintId, constraint.getConstraintItem(constraintId));
    }
    return this;
  }

  /**
   * Add a single constraint item
   * @param type type of the constraint
   * @param constraintId unique constraint identifier
   * @param item instantiated ConstraintItem
   * @return Builder
   */
  @Override
  public ClusterConfigBuilder addConstraint(ConstraintType type, ConstraintId constraintId,
      IConstraintItem item) {
    IClusterConstraints existConstraints = getConstraintsInstance(type);
    existConstraints.addConstraintItem(constraintId, item);
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.api.config.ClusterConfigBuilder#addConstraints(java.util.Collection)
   */
  @Override
  public <T extends IClusterConstraints> ClusterConfigBuilder addConstraints(Collection<T> constraints) {
    for (T constraint : constraints) {
      addConstraint(constraint);
    }
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.api.config.ClusterConfigBuilder#addTransitionConstraint(org.apache.helix.api
   * .config.Scope, org.apache.helix.api.id.StateModelDefId, org.apache.helix.api.model.ITransition,
   * int)
   */
  @Override
  public ClusterConfigBuilder addTransitionConstraint(Scope<?> scope,
      StateModelDefId stateModelDefId, ITransition transition, int maxInFlightTransitions) {
    Map<String, String> attributes = Maps.newHashMap();
    attributes.put(ConstraintAttribute.MESSAGE_TYPE.toString(),
        MessageType.STATE_TRANSITION.toString());
    attributes.put(ConstraintAttribute.CONSTRAINT_VALUE.toString(),
        Integer.toString(maxInFlightTransitions));
    attributes.put(ConstraintAttribute.TRANSITION.toString(), transition.toString());
    attributes.put(ConstraintAttribute.STATE_MODEL.toString(), stateModelDefId.stringify());
    switch (scope.getType()) {
    case CLUSTER:
      // cluster is implicit
      break;
    case RESOURCE:
      attributes.put(ConstraintAttribute.RESOURCE.toString(), scope.getScopedId().stringify());
      break;
    case PARTICIPANT:
      attributes.put(ConstraintAttribute.INSTANCE.toString(), scope.getScopedId().stringify());
      break;
    default:
      LOG.error("Unsupported scope for adding a transition constraint: " + scope);
      return this;
    }
    IConstraintItem item = new ConstraintItemBuilder().addConstraintAttributes(attributes).build();
    IClusterConstraints constraints = getConstraintsInstance(ConstraintType.MESSAGE_CONSTRAINT);
    constraints.addConstraintItem(ConstraintId.from(scope, stateModelDefId, transition), item);
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.api.config.ClusterConfigBuilder#addStateModelDefinition(org.apache.helix.api
   * .model.IStateModelDefinition)
   */
  @Override
  public ClusterConfigBuilder addStateModelDefinition(IStateModelDefinition stateModelDef) {
    _stateModelMap.put(stateModelDef.getStateModelDefId(), stateModelDef);
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.api.config.ClusterConfigBuilder#addStateModelDefinitions(java.util.Collection)
   */
  @Override
  public <T extends IStateModelDefinition> ClusterConfigBuilder addStateModelDefinitions(
      Collection<T> stateModelDefs) {
    for (T stateModelDef : stateModelDefs) {
      addStateModelDefinition(stateModelDef);
    }
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.api.config.ClusterConfigBuilder#pausedStatus(boolean)
   */
  @Override
  public ClusterConfigBuilder pausedStatus(boolean isPaused) {
    _isPaused = isPaused;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.api.config.ClusterConfigBuilder#autoJoin(boolean)
   */
  @Override
  public ClusterConfigBuilder autoJoin(boolean autoJoin) {
    _autoJoin = autoJoin;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.api.config.ClusterConfigBuilder#userConfig(org.apache.helix.api.config.UserConfig
   * )
   */
  @Override
  public ClusterConfigBuilder userConfig(UserConfig userConfig) {
    _userConfig = userConfig;
    return this;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.api.config.ClusterConfigBuilder#build()
   */
  @Override
  public ClusterConfig build() {
    return new ClusterConfig(_id, _resourceMap, _participantMap, _constraintMap, _stateModelMap,
        _userConfig, _isPaused, _autoJoin);
  }

  /**
   * Get a valid instance of ClusterConstraints for a type
   * @param type the type
   * @return ClusterConstraints
   */
  private IClusterConstraints getConstraintsInstance(ConstraintType type) {
    IClusterConstraints constraints = _constraintMap.get(type);
    if (constraints == null) {
      constraints = new ClusterConstraints(type);
      _constraintMap.put(type, constraints);
    }
    return constraints;
  }
}
