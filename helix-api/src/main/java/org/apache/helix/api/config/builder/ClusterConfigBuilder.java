package org.apache.helix.api.config.builder;

import java.util.Collection;

import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.helix.api.config.ClusterConfig;
import org.apache.helix.api.config.ParticipantConfig;
import org.apache.helix.api.config.ResourceConfig;
import org.apache.helix.api.config.Scope;
import org.apache.helix.api.config.UserConfig;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.ConstraintId;
import org.apache.helix.api.id.StateModelDefId;
import org.apache.helix.api.model.IClusterConstraints;
import org.apache.helix.api.model.IClusterConstraints.ConstraintType;
import org.apache.helix.api.model.IConstraintItem;
import org.apache.helix.api.model.IStateModelDefinition;
import org.apache.helix.api.model.ITransition;

public abstract class ClusterConfigBuilder {
  /**
   * Add a resource to the cluster
   * @param resource resource configuration
   * @return Builder
   */
  public abstract ClusterConfigBuilder addResource(ResourceConfig resource);

  /**
   * Add multiple resources to the cluster
   * @param resources resource configurations
   * @return Builder
   */
  public abstract ClusterConfigBuilder addResources(Collection<ResourceConfig> resources);

  /**
   * Add a participant to the cluster
   * @param participant participant configuration
   * @return Builder
   */
  public abstract ClusterConfigBuilder addParticipant(ParticipantConfig participant);

  /**
   * Add multiple participants to the cluster
   * @param participants participant configurations
   * @return Builder
   */
  public abstract ClusterConfigBuilder addParticipants(Collection<ParticipantConfig> participants);

  /**
   * Add a constraint to the cluster
   * @param constraint cluster constraint of a specific type
   * @return ClusterConfigBuilder
   */
  public abstract <T extends IClusterConstraints> ClusterConfigBuilder addConstraint(T constraint);

  /**
   * Add a single constraint item
   * @param type type of the constraint
   * @param constraintId unique constraint identifier
   * @param item instantiated ConstraintItem
   * @return ClusterConfigBuilder
   */
  public abstract <T extends IConstraintItem> ClusterConfigBuilder addConstraint(ConstraintType type,
      ConstraintId constraintId, T item);

  /**
   * Add multiple constraints to the cluster
   * @param constraints cluster constraints of multiple distinct types
   * @return ClusterConfigBuilder
   */
  public abstract <T extends IClusterConstraints> ClusterConfigBuilder addConstraints(
      Collection<T> constraints);

  /**
   * Add a constraint on the maximum number of in-flight transitions of a certain type
   * @param scope scope of the constraint
   * @param stateModelDefId identifies the state model containing the transition
   * @param transition the transition to constrain
   * @param maxInFlightTransitions number of allowed in-flight transitions in the scope
   * @return Builder
   */
  public abstract ClusterConfigBuilder addTransitionConstraint(Scope<?> scope,
      StateModelDefId stateModelDefId, ITransition transition, int maxInFlightTransitions);

  /**
   * Add a state model definition to the cluster
   * @param stateModelDef state model definition of the cluster
   * @return Builder
   */
  public abstract <T extends IStateModelDefinition> ClusterConfigBuilder addStateModelDefinition(
      T stateModelDef);

  /**
   * Add multiple state model definitions
   * @param stateModelDefs collection of state model definitions for the cluster
   * @return Builder
   */
  public abstract <T extends IStateModelDefinition> ClusterConfigBuilder addStateModelDefinitions(
      Collection<T> stateModelDefs);

  /**
   * Set the paused status of the cluster
   * @param isPaused true if paused, false otherwise
   * @return Builder
   */
  public abstract ClusterConfigBuilder pausedStatus(boolean isPaused);

  /**
   * Allow or disallow participants from automatically being able to join the cluster
   * @param autoJoin true if allowed, false if disallowed
   * @return Builder
   */
  public abstract ClusterConfigBuilder autoJoin(boolean autoJoin);

  /**
   * Set the user configuration
   * @param userConfig user-specified properties
   * @return Builder
   */
  public abstract ClusterConfigBuilder userConfig(UserConfig userConfig);

  /**
   * Creates the builder with a given cluster id
   * @param id
   * @return
   */
  public abstract ClusterConfigBuilder withClusterId(ClusterId id);

  /**
   * Create the cluster configuration
   * @return ClusterConfig
   */
  public abstract ClusterConfig build();

  /**
   * Returns a new instance of the cluster configuration builder
   * 
   * @return ClusterConfigBuilder
   */
  public static ClusterConfigBuilder newInstance() {
    try {
      return new DiscoverClass().newInstance(ClusterConfigBuilder.class, 
          "org.apache.helix.core.config.builder.ClusterConfigBuilderImpl");
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }
}
