package org.apache.helix.api.config;

import java.util.Map;
import java.util.Set;

import org.apache.helix.core.config.builder.ClusterConfigBuilder;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.ConstraintId;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.model.Scope;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.model.builder.ClusterConstraintsBuilder;
import org.apache.helix.api.model.configuration.ParticipantConfiguration;
import org.apache.helix.api.model.constraint.ClusterConstraints;
import org.apache.helix.api.model.constraint.ConstraintItem;
import org.apache.helix.api.model.constraint.ClusterConstraints.ConstraintAttribute;
import org.apache.helix.api.model.constraint.ClusterConstraints.ConstraintType;
import org.apache.helix.api.model.ipc.Message.MessageType;
import org.apache.helix.api.model.statemachine.StateModelDefinition;
import org.apache.helix.api.model.statemachine.Transition;
import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
 * Configuration properties of a cluster
 */
public class ClusterConfig {
  private static final Logger LOG = Logger.getLogger(ClusterConfig.class);

  private final ClusterId _id;
  private final Map<ResourceId, ResourceConfig> _resourceMap;
  private final Map<ParticipantId, ParticipantConfiguration> _participantMap;
  private final Map<ConstraintType, ClusterConstraints> _constraintMap;
  private final Map<StateModelDefinitionId, StateModelDefinition> _stateModelMap;
  private final UserConfig _userConfig;
  private final boolean _isPaused;
  private final boolean _autoJoin;

  /**
   * Initialize a cluster configuration. Also see ClusterConfig.Builder
   * @param id cluster id
   * @param resourceMap map of resource id to resource config
   * @param participantMap map of participant id to participant config
   * @param constraintMap map of constraint type to all constraints of that type
   * @param stateModelMap map of state model id to state model definition
   * @param userConfig user-defined cluster properties
   * @param isPaused true if paused, false if active
   * @param allowAutoJoin true if participants can join automatically, false otherwise
   */
  public ClusterConfig(ClusterId id, Map<ResourceId, ResourceConfig> resourceMap,
      Map<ParticipantId, ParticipantConfiguration> participantMap,
      Map<ConstraintType, ClusterConstraints> constraintMap,
      Map<StateModelDefinitionId, StateModelDefinition> stateModelMap, UserConfig userConfig,
      boolean isPaused, boolean allowAutoJoin) {
    _id = id;
    _resourceMap = ImmutableMap.copyOf(resourceMap);
    _participantMap = ImmutableMap.copyOf(participantMap);
    _constraintMap = ImmutableMap.copyOf(constraintMap);
    _stateModelMap = ImmutableMap.copyOf(stateModelMap);
    _userConfig = userConfig;
    _isPaused = isPaused;
    _autoJoin = allowAutoJoin;
  }

  /**
   * Get cluster id
   * @return cluster id
   */
  public ClusterId getId() {
    return _id;
  }

  /**
   * Get resources in the cluster
   * @return a map of resource id to resource, or empty map if none
   */
  public Map<ResourceId, ResourceConfig> getResourceMap() {
    return _resourceMap;
  }

  /**
   * Get all the constraints on the cluster
   * @return map of constraint type to constraints
   */
  public Map<ConstraintType, ClusterConstraints> getConstraintMap() {
    return _constraintMap;
  }

  /**
   * Get the limit of simultaneous execution of a transition
   * @param scope the scope under which the transition is constrained
   * @param stateModelDefId the state model of which the transition is a part
   * @param transition the constrained transition
   * @return the limit, or Integer.MAX_VALUE if there is no limit
   */
  public int getTransitionConstraint(Scope<?> scope, StateModelDefinitionId stateModelDefId,
      Transition transition) {
    // set up attributes to match based on the scope
    ClusterConstraints transitionConstraints =
        getConstraintMap().get(ConstraintType.MESSAGE_CONSTRAINT);
    Map<ConstraintAttribute, String> matchAttributes = Maps.newHashMap();
    matchAttributes.put(ConstraintAttribute.STATE_MODEL, stateModelDefId.toString());
    matchAttributes.put(ConstraintAttribute.MESSAGE_TYPE, MessageType.STATE_TRANSITION.toString());
    matchAttributes.put(ConstraintAttribute.TRANSITION, transition.toString());
    switch (scope.getType()) {
    case CLUSTER:
      // cluster is implicit
      break;
    case RESOURCE:
      matchAttributes.put(ConstraintAttribute.RESOURCE, scope.getScopedId().toString());
      break;
    case PARTICIPANT:
      matchAttributes.put(ConstraintAttribute.INSTANCE, scope.getScopedId().toString());
      break;
    default:
      LOG.error("Unsupported scope for transition constraints: " + scope);
      return Integer.MAX_VALUE;
    }
    Set<ConstraintItem> matches = transitionConstraints.match(matchAttributes);
    int value = Integer.MAX_VALUE;
    for (ConstraintItem item : matches) {
      String constraintValue = item.getConstraintValue();
      if (constraintValue != null) {
        try {
          int current = Integer.parseInt(constraintValue);
          if (current < value) {
            value = current;
          }
        } catch (NumberFormatException e) {
          LOG.error("Invalid in-flight transition cap: " + constraintValue);
        }
      }
    }
    return value;
  }

  /**
   * Get participants of the cluster
   * @return a map of participant id to participant, or empty map if none
   */
  public Map<ParticipantId, ParticipantConfiguration> getParticipantMap() {
    return _participantMap;
  }

  /**
   * Get all the state model definitions on the cluster
   * @return map of state model definition id to state model definition
   */
  public Map<StateModelDefinitionId, StateModelDefinition> getStateModelMap() {
    return _stateModelMap;
  }

  /**
   * Get user-specified configuration properties of this cluster
   * @return UserConfig properties
   */
  public UserConfig getUserConfig() {
    return _userConfig;
  }

  /**
   * Check the paused status of the cluster
   * @return true if paused, false otherwise
   */
  public boolean isPaused() {
    return _isPaused;
  }

  /**
   * Check if this cluster allows participants to join automatically
   * @return true if allowed, false if disallowed
   */
  public boolean autoJoinAllowed() {
    return _autoJoin;
  }

  /**
   * Update context for a ClusterConfig
   */
  public static class Delta {
    private enum Fields {
      USER_CONFIG,
      AUTO_JOIN
    }

    private Set<Fields> _updateFields;
    private Map<ConstraintType, Set<ConstraintId>> _removedConstraints;
    private ClusterConfigBuilder _builder = new ClusterConfigBuilder();

    /**
     * Instantiate the delta for a cluster config
     * @param clusterId the cluster to update
     */
    public Delta(ClusterId clusterId) {
      _updateFields = Sets.newHashSet();
      _removedConstraints = Maps.newHashMap();
      for (ConstraintType type : ConstraintType.values()) {
        Set<ConstraintId> constraints = Sets.newHashSet();
        _removedConstraints.put(type, constraints);
      }
      _builder = _builder.withClusterId(clusterId);
    }

    /**
     * Add a constraint on the maximum number of in-flight transitions of a certain type
     * @param scope scope of the constraint
     * @param stateModelDefId identifies the state model containing the transition
     * @param transition the transition to constrain
     * @param maxInFlightTransitions number of allowed in-flight transitions in the scope
     * @return Delta
     */
    public Delta addTransitionConstraint(Scope<?> scope, StateModelDefinitionId stateModelDefId,
        Transition transition, int maxInFlightTransitions) {
      _builder.addTransitionConstraint(scope, stateModelDefId, transition, maxInFlightTransitions);
      return this;
    }

    /**
     * Remove a constraint on the maximum number of in-flight transitions of a certain type
     * @param scope scope of the constraint
     * @param stateModelDefId identifies the state model containing the transition
     * @param transition the transition to constrain
     * @return Delta
     */
    public Delta removeTransitionConstraint(Scope<?> scope, StateModelDefinitionId stateModelDefId,
        Transition transition) {
      _removedConstraints.get(ConstraintType.MESSAGE_CONSTRAINT).add(
          ConstraintId.from(scope, stateModelDefId, transition));
      return this;
    }

    /**
     * Add a single constraint item
     * @param type type of the constraint item
     * @param constraintId unique constraint id
     * @param item instantiated ConstraintItem
     * @return Delta
     */
    public Delta addConstraintItem(ConstraintType type, ConstraintId constraintId,
        ConstraintItem item) {
      _builder.addConstraint(type, constraintId, item);
      return this;
    }

    /**
     * Remove a single constraint item
     * @param type type of the constraint item
     * @param constraintId unique constraint id
     * @return Delta
     */
    public Delta removeConstraintItem(ConstraintType type, ConstraintId constraintId) {
      _removedConstraints.get(type).add(constraintId);
      return this;
    }

    /*
     * Set the user configuration
     * @param userConfig user-specified properties
     * @return Delta
     */
    public Delta setUserConfig(UserConfig userConfig) {
      _builder.userConfig(userConfig);
      _updateFields.add(Fields.USER_CONFIG);
      return this;
    }

    /**
     * Allow or disallow participants from automatically being able to join the cluster
     * @param autoJoin true if allowed, false if disallowed
     * @return Delta
     */
    public Delta setAutoJoin(boolean autoJoin) {
      _builder.autoJoin(autoJoin);
      _updateFields.add(Fields.AUTO_JOIN);
      return this;
    }

    /**
     * Create a ClusterConfig that is the combination of an existing ClusterConfig and this delta
     * @param orig the original ClusterConfig
     * @return updated ClusterConfig
     */
    public ClusterConfig mergeInto(ClusterConfig orig) {
      // copy in original and updated fields
      ClusterConfig deltaConfig = _builder.build();
      ClusterConfigBuilder builder =
          new ClusterConfigBuilder().withClusterId(orig.getId())
              .addResources(orig.getResourceMap().values())
              .addParticipants(orig.getParticipantMap().values())
              .addStateModelDefinitions(orig.getStateModelMap().values())
              .userConfig(orig.getUserConfig()).pausedStatus(orig.isPaused())
              .autoJoin(orig.autoJoinAllowed());
      for (Fields field : _updateFields) {
        switch (field) {
        case USER_CONFIG:
          builder.userConfig(deltaConfig.getUserConfig());
          break;
        case AUTO_JOIN:
          builder.autoJoin(deltaConfig.autoJoinAllowed());
          break;
        }
      }
      // add constraint deltas
      for (ConstraintType type : ConstraintType.values()) {
        ClusterConstraints constraints;
        if (orig.getConstraintMap().containsKey(type)) {
          constraints = orig.getConstraintMap().get(type);
        } else {
          constraints = new ClusterConstraintsBuilder().withType(type).build();
        }
        // add new constraints
        if (deltaConfig.getConstraintMap().containsKey(type)) {
          ClusterConstraints deltaConstraints = deltaConfig.getConstraintMap().get(type);
          for (ConstraintId constraintId : deltaConstraints.getConstraintItems().keySet()) {
            ConstraintItem constraintItem = deltaConstraints.getConstraintItem(constraintId);
            constraints.addConstraintItem(constraintId, constraintItem);
          }
        }
        // remove constraints
        for (ConstraintId constraintId : _removedConstraints.get(type)) {
          constraints.removeConstraintItem(constraintId);
        }
        builder.addConstraint(constraints);
      }

      // get the result
      return builder.build();
    }
  }

}
