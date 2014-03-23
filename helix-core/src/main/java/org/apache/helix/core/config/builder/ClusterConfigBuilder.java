package org.apache.helix.core.config.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.helix.api.config.ClusterConfig;
import org.apache.helix.api.config.ResourceConfig;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.ConstraintId;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.model.Scope;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.api.model.configuration.ParticipantConfiguration;
import org.apache.helix.api.model.constraint.ClusterConstraints;
import org.apache.helix.api.model.constraint.ConstraintItem;
import org.apache.helix.api.model.constraint.ConstraintItemBuilder;
import org.apache.helix.api.model.constraint.ClusterConstraints.ConstraintAttribute;
import org.apache.helix.api.model.constraint.ClusterConstraints.ConstraintType;
import org.apache.helix.api.model.ipc.Message.MessageType;
import org.apache.helix.api.model.statemachine.StateModelDefinition;
import org.apache.helix.api.model.statemachine.Transition;
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

public class ClusterConfigBuilder {
  private static final Logger LOG = Logger.getLogger(ClusterConfigBuilder.class);
  /**
   * Assembles a cluster configuration
   */
  private ClusterId _id;
  private Map<ResourceId, ResourceConfig> _resourceMap;
  private Map<ParticipantId, ParticipantConfiguration> _participantMap;
  private Map<ConstraintType, ClusterConstraints> _constraintMap;
  private Map<StateModelDefinitionId, StateModelDefinition> _stateModelMap;
  private UserConfig _userConfig;
  private boolean _isPaused;
  private boolean _autoJoin;

  /**
   * Initialize builder for a cluster
   * @param id cluster id
   */
  public ClusterConfigBuilder() {

  }

  /**
   * Creates the builder with a given cluster id
   * @param id
   * @return
   */
  public ClusterConfigBuilder withClusterId(ClusterId id) {
    _id = id;
    _resourceMap = new HashMap<ResourceId, ResourceConfig>();
    _participantMap = new HashMap<ParticipantId, ParticipantConfiguration>();
    _constraintMap = new HashMap<ConstraintType, ClusterConstraints>();
    _stateModelMap = new HashMap<StateModelDefinitionId, StateModelDefinition>();
    _isPaused = false;
    _autoJoin = false;
    _userConfig = new UserConfig(Scope.cluster(id));
    return this;
  }

  /**
   * Add a resource to the cluster
   * @param resource resource configuration
   * @return Builder
   */
  public ClusterConfigBuilder addResource(ResourceConfig resource) {
    _resourceMap.put(resource.getId(), resource);
    return this;
  }

  /**
   * Add multiple resources to the cluster
   * @param resources resource configurations
   * @return Builder
   */
  public ClusterConfigBuilder addResources(Collection<ResourceConfig> resources) {
    for (ResourceConfig resource : resources) {
      addResource(resource);
    }
    return this;
  }

  /**
   * Add a participant to the cluster
   * @param participant participant configuration
   * @return Builder
   */
  public ClusterConfigBuilder addParticipant(ParticipantConfiguration participant) {
    _participantMap.put(participant.getId(), participant);
    return this;
  }

  /**
   * Add multiple participants to the cluster
   * @param participants participant configurations
   * @return Builder
   */
  public ClusterConfigBuilder addParticipants(Collection<ParticipantConfiguration> participants) {
    for (ParticipantConfiguration participant : participants) {
      addParticipant(participant);
    }
    return this;
  }

  /**
   * Add a constraint to the cluster
   * @param constraint cluster constraint of a specific type
   * @return ClusterConfigBuilder
   */
  public ClusterConfigBuilder addConstraint(ClusterConstraints constraint) {
    ClusterConstraints existConstraints = getConstraintsInstance(constraint.getType());
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
   * @return ClusterConfigBuilder
   */
  public ClusterConfigBuilder addConstraint(ConstraintType type, ConstraintId constraintId,
      ConstraintItem item) {
    ClusterConstraints existConstraints = getConstraintsInstance(type);
    existConstraints.addConstraintItem(constraintId, item);
    return this;
  }

  /**
   * Add multiple constraints to the cluster
   * @param constraints cluster constraints of multiple distinct types
   * @return ClusterConfigBuilder
   */
  public <T extends ClusterConstraints> ClusterConfigBuilder addConstraints(
      Collection<T> constraints) {
    for (T constraint : constraints) {
      addConstraint(constraint);
    }
    return this;
  }

  /**
   * Add a constraint on the maximum number of in-flight transitions of a certain type
   * @param <T>
   * @param scope scope of the constraint
   * @param stateModelDefId identifies the state model containing the transition
   * @param transition the transition to constrain
   * @param maxInFlightTransitions number of allowed in-flight transitions in the scope
   * @return Builder
   */
  public ClusterConfigBuilder addTransitionConstraint(Scope<?> scope,
      StateModelDefinitionId stateModelDefId, Transition transition, int maxInFlightTransitions) {
    Map<String, String> attributes = Maps.newHashMap();
    attributes.put(ConstraintAttribute.MESSAGE_TYPE.toString(),
        MessageType.STATE_TRANSITION.toString());
    attributes.put(ConstraintAttribute.CONSTRAINT_VALUE.toString(),
        Integer.toString(maxInFlightTransitions));
    attributes.put(ConstraintAttribute.TRANSITION.toString(), transition.toString());
    attributes.put(ConstraintAttribute.STATE_MODEL.toString(), stateModelDefId.toString());
    switch (scope.getType()) {
    case CLUSTER:
      // cluster is implicit
      break;
    case RESOURCE:
      attributes.put(ConstraintAttribute.RESOURCE.toString(), scope.getScopedId().toString());
      break;
    case PARTICIPANT:
      attributes.put(ConstraintAttribute.INSTANCE.toString(), scope.getScopedId().toString());
      break;
    default:
      LOG.error("Unsupported scope for adding a transition constraint: " + scope);
      return this;
    }
    ConstraintItem item = new ConstraintItemBuilder().addConstraintAttributes(attributes).build();
    ClusterConstraints constraints = getConstraintsInstance(ConstraintType.MESSAGE_CONSTRAINT);
    constraints.addConstraintItem(ConstraintId.from(scope, stateModelDefId, transition), item);
    return this;
  }

  /**
   * Add a state model definition to the cluster
   * @param stateModelDef state model definition of the cluster
   * @return Builder
   */
  public ClusterConfigBuilder addStateModelDefinition(StateModelDefinition stateModelDef) {
    _stateModelMap.put(stateModelDef.getStateModelDefId(), stateModelDef);
    return this;
  }

  /**
   * Add multiple state model definitions
   * @param stateModelDefs collection of state model definitions for the cluster
   * @return Builder
   */
  public <T extends StateModelDefinition> ClusterConfigBuilder addStateModelDefinitions(
      Collection<T> stateModelDefs) {
    for (T stateModelDef : stateModelDefs) {
      addStateModelDefinition(stateModelDef);
    }
    return this;
  }

  /**
   * Set the paused status of the cluster
   * @param isPaused true if paused, false otherwise
   * @return Builder
   */
  public ClusterConfigBuilder pausedStatus(boolean isPaused) {
    _isPaused = isPaused;
    return this;
  }

  /**
   * Allow or disallow participants from automatically being able to join the cluster
   * @param autoJoin true if allowed, false if disallowed
   * @return Builder
   */
  public ClusterConfigBuilder autoJoin(boolean autoJoin) {
    _autoJoin = autoJoin;
    return this;
  }

  /**
   * Set the user configuration
   * @param userConfig user-specified properties
   * @return Builder
   */
  public ClusterConfigBuilder userConfig(UserConfig userConfig) {
    _userConfig = userConfig;
    return this;
  }

  /**
   * Create the cluster configuration
   * @return ClusterConfig
   */
  public ClusterConfig build() {
    return new ClusterConfig(_id, _resourceMap, _participantMap, _constraintMap, _stateModelMap,
        _userConfig, _isPaused, _autoJoin);
  }

  /**
   * Get a valid instance of ClusterConstraints for a type
   * @param type the type
   * @return ClusterConstraints
   */
  private ClusterConstraints getConstraintsInstance(ConstraintType type) {
    ClusterConstraints constraints = _constraintMap.get(type);
    if (constraints == null) {
      constraints = new ClusterConstraints(type);
      _constraintMap.put(type, constraints);
    }
    return constraints;
  }
}
