package org.apache.helix.api.command;

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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.apache.helix.api.command.HelixMemberCommand.MemberType;
import org.apache.helix.api.id.AdministratorId;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.ControllerId;
import org.apache.helix.api.id.MemberId;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.RebalancerId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.SpectatorId;
import org.apache.helix.api.id.StateModelDefinitionId;

/**
 * A builder factory for commands, this factory is the starting point for creating commands which
 * can then be fed to the HelixAdministrator client
 */
public class HelixCommandBuilderFactory {

  /**
   * Creates a command builder for Cluster Commands
   * @param id the cluster id to create the command for
   * @return HelixClusterCommandBuilder a command builder for cluster
   */
  public static final HelixClusterCommandBuilder createClusterBuilder(String id) {
    return new HelixClusterCommandBuilder(ClusterId.from(id));
  }

  /**
   * Creates a command builder for State Model Commands
   * @param id the state model id to create the command for
   * @return HelixStateModelCommandBuilder a command builder for cluster
   */
  public static final HelixStateModelDefinitionCommandBuilder createStateModelDefinitionBuilder(
      String id) {
    return new HelixStateModelDefinitionCommandBuilder(StateModelDefinitionId.from(id));
  }

  /**
   * Creates a command builder for resource commands
   * @param id the resource id to create the command for
   * @return HelixResourceCommandBuilder a command builder for resource
   */
  public static final HelixResourceCommandBuilder createResourceBuilder(String id) {
    return new HelixResourceCommandBuilder(ResourceId.from(id));
  }

  /**
   * Creates a command builder for partition commands
   * @param resourceId the resource id to create the partition for
   * @param partitionId the partition id to create the command for
   * @return HelixPartitionCommandBuilder a command builder for partition
   */
  public static final HelixPartitionCommandBuilder createResourceBuilder(String resourceId,
      String partitionId) {
    return new HelixPartitionCommandBuilder(ResourceId.from(resourceId),
        PartitionId.from(partitionId));
  }

  /**
   * Creates a member builder of type participant
   * @param participantId the participant id
   * @return HelixMemberCommandBuilder which allows creating a participant member
   */
  public static final HelixParticipantCommandBuilder createParticipantMemberBuilder(
      String participantId) {
    return new HelixParticipantCommandBuilder(ParticipantId.from(participantId));
  }

  /**
   * Creates a member builder of type administrator
   * @param administratorId the administrator id
   * @return HelixMemberCommandBuilder which allows creating a administrator member
   */
  public static final <T extends MemberId, M extends HelixMemberCommand> HelixMemberCommandBuilder<T, M> createAdministratorMemberBuilder(
      String administratorId) {
    throw new UnsupportedOperationException(
        "Currently members of type administrator cannot be created");
  }

  /**
   * Creates a member builder of type controller
   * @param controllerId the controller id
   * @return HelixMemberCommandBuilder which allows creating a controller member
   */
  public static final <T extends MemberId, M extends HelixMemberCommand> HelixMemberCommandBuilder<T, M> createControllerMemberBuilder(
      String controllerId) {
    throw new UnsupportedOperationException(
        "Currently members of type controller cannot be created");
  }

  /**
   * Creates a member builder of type spectator
   * @param spectatorId the spectator id
   * @return HelixMemberCommandBuilder which allows creating a spectator member
   */
  public static final <T extends MemberId, M extends HelixMemberCommand> HelixMemberCommandBuilder<T, M> createSpectatorMemberBuilder(
      T spectatorId) {
    throw new UnsupportedOperationException("Currently members of type spectator cannot be created");
  }

  /**
   * Creates a command builder for resource command
   */
  public static class HelixResourceCommandBuilder {

    HelixResourceCommandBuilder(ResourceId id) {

    }

    /**
     * Partitions for the resource
     * @param partitions the total partitions
     * @return HelixResourceCommandBuilder
     */
    public HelixResourceCommandBuilder withPartitions(int partitions) {
      return null;
    }

    /**
     * The state model to use for the resource
     * @param id the state model id
     * @return HelixResourceCommandBuilder
     */
    public HelixResourceCommandBuilder withStateModelDefinitionId(StateModelDefinitionId id) {
      return null;
    }

    /**
     * The rebalancer to use to rebalance the resource replicas across the cluster
     * @param rebalancerId the rebalancer id
     * @return HelixResourceCommandBuilder
     */
    public HelixResourceCommandBuilder withRebalancerId(RebalancerId rebalancerId) {
      return null;
    }

    /**
     * Tracks custom user properties against the cluster, the properties are serialized as json
     * unless the values are Externalizable in which case the property serialization is left to the
     * invoker
     * @param properties the properties to track
     * @return HelixResourceCommandBuilder
     */
    public HelixResourceCommandBuilder withUserProperties(Map<String, Serializable> properties) {
      return null;
    }

    /**
     * Tracks custom user property against the cluster, the property value is serialized as json
     * using Javabean property descriptors unless the value is Externalizable in which case the
     * property value is serialized as chosen by the invoker
     * @param propertyName the property name
     * @param propertyValue the value to store
     * @return HelixResourceCommandBuilder
     */
    public HelixResourceCommandBuilder withUserProperty(String propertyName,
        Serializable propertyValue) {
      return null;
    }

    /**
     * Builds the command
     * @return HelixResourceCommand
     */
    public HelixResourceCommand build() {
      return null;
    }

  }

  /**
   * The command builder class which implements all the interfaces
   */
  public static class HelixClusterCommandBuilder {

    /**
     * A cluster command must always have a cluster id
     * @param id
     */
    HelixClusterCommandBuilder(ClusterId id) {
    }

    /**
     * Adds a set of participants to the cluster command
     * @param participants
     * @return HelixClusterCommandBuilder an instance of a cluster command builder
     */
    public HelixClusterCommandBuilder withParticipants(Set<HelixParticipantCommand> participants) {
      return null;
    }

    /**
     * Adds a set of resources to the command
     * @param resources
     * @return HelixClusterCommandBuilder an instance of a cluster command builder
     */
    public HelixClusterCommandBuilder withResources(Set<HelixResourceCommand> resources) {
      return null;
    }

    /**
     * Defines the state model definition for the command
     * @param command
     * @return HelixClusterCommandBuilder an instance of a cluster command builder
     */
    public HelixClusterCommandBuilder withStateModelDefinition(
        HelixStateModelDefinitionCommand command) {
      return null;
    }

    /**
     * Indicates if the cluster allows auto-join
     * @param autoJoin <b>True</b>if the cluster allows auto-join, <b>False</b> if not
     * @return HelixClusterCommandBuilder an instance of a cluster command builder
     */
    public HelixClusterCommandBuilder withAllowAutoJoin(boolean autoJoin) {
      return null;
    }

    /**
     * Adds the specified cluster constraint to the cluster command
     * @param command the constraint command
     * @return HelixClusterCommandBuilder an instance of a cluster command builder
     */
    public HelixClusterCommandBuilder withConstraint(HelixConstraintCommand command) {
      return null;
    }

    /**
     * Re-Creates the cluster if its already created
     * @param create <b>True</b>if the cluster should be recreated, <b>False</b> if not
     * @return HelixClusterCommandBuilder an instance of a cluster command builder
     */
    public HelixClusterCommandBuilder recreateIfExists(boolean create) {
      return null;
    }

    /**
     * Tracks custom user properties against the cluster, the properties are serialized as json
     * unless the values are Externalizable in which case the property serialization is left to the
     * invoker
     * @param properties the properties to track
     * @return HelixClusterCommandBuilder
     */
    public HelixClusterCommandBuilder withUserProperties(Map<String, Serializable> properties) {
      return null;
    }

    /**
     * Tracks custom user property against the cluster, the property value is serialized as json
     * using Javabean property descriptors unless the value is Externalizable in which case the
     * property value is serialized as chosen by the invoker
     * @param propertyName the property name
     * @param propertyValue the value to store
     * @return HelixClusterCommandBuilder
     */
    public HelixClusterCommandBuilder withUserProperty(String propertyName,
        Serializable propertyValue) {
      return null;
    }

    /**
     * Builds and returns the command
     * @return HelixClusterCommand
     */
    public HelixClusterCommand build() {
      return null;
    }
  }

  /**
   * A command builder to create state model definitions
   */
  public static class HelixStateModelDefinitionCommandBuilder {

    HelixStateModelDefinitionCommandBuilder(StateModelDefinitionId id) {
    }

    /**
     * Adds states to the state model
     * @param states the states the state model should manage transitions across
     * @return Set<String> the states
     */
    public HelixStateModelDefinitionCommandBuilder addStates(Set<String> states) {
      return null;
    }

    /**
     * Adds a transition from a given fromState to the toState
     * @param fromState the state to start transition from
     * @param toState the state to end transition to
     * @param transitionConstraint the constraint on the transition
     * @return HelixStateModelDefinitionCommandBuilder
     */
    public HelixStateModelDefinitionCommandBuilder addTransition(String fromState, String toState,
        HelixTransitionConstraintCommand transitionConstraint) {
      return null;
    }

    /**
     * Adds a transition from a given state to the toState
     * @param fromState the state to start transition from
     * @param toStates the states to end transitions into
     * @param transitionConstraint the constraints on the transition
     * @return HelixStateModelDefinitionCommandBuilder
     */
    public HelixStateModelDefinitionCommandBuilder addTransition(String fromState,
        Set<String> toStates, HelixTransitionConstraintCommand transitionConstraint) {
      return null;
    }

    /**
     * Adds a constraint to a given state
     * @param state the state to add constraint to
     * @param stateConstraint the constraint for the state
     * @return HelixStateModelDefinitionCommandBuilder
     */
    public HelixStateModelDefinitionCommandBuilder addStateConstraint(String state,
        HelixStateConstraintCommand stateConstraint) {
      return null;
    }

    /**
     * Builds the state model definition command
     * @return HelixStateModelDefinitionCommand
     */
    public HelixStateModelDefinitionCommand build() {
      return null;
    }
  }

  /**
   * Parition command builder
   */
  public static class HelixPartitionCommandBuilder {
    private HelixPartitionCommand command;

    HelixPartitionCommandBuilder(ResourceId resourceId, PartitionId partitionId) {

    }

    /**
     * Tracks custom user properties against the cluster, the properties are serialized as json
     * unless the values are Externalizable in which case the property serialization is left to the
     * invoker
     * @param properties the properties to track
     * @return HelixPartitionCommandBuilder
     */
    public HelixPartitionCommandBuilder withUserProperties(Map<String, Serializable> properties) {
      return null;
    }

    /**
     * Tracks custom user property against the cluster, the property value is serialized as json
     * using Javabean property descriptors unless the value is Externalizable in which case the
     * property value is serialized as chosen by the invoker
     * @param propertyName the property name
     * @param propertyValue the value to store
     * @return HelixPartitionCommandBuilder
     */
    public HelixPartitionCommandBuilder withUserProperty(String propertyName,
        Serializable propertyValue) {
      return null;
    }

    /**
     * Returns the command for derived classes
     * @return HelixPartitionCommand an instance of HelixPartitionCommand
     */
    protected HelixPartitionCommand build() {
      return this.command;
    }
  }

  /**
   * A command builder for the member command
   * @param <T> a derived type of MemberId
   * @param <M>
   */
  public static class HelixMemberCommandBuilder<T extends MemberId, M extends HelixMemberCommand> {
    private M command;

    /**
     * Creates a member command builder with a given member id
     * @param memberId
     */
    @SuppressWarnings("unchecked")
    HelixMemberCommandBuilder(T memberId, MemberType type) {
      switch (type) {
      case ADMINISTRATOR:
        command = (M) new HelixAdministratorCommand((AdministratorId) memberId);
        break;
      case SPECTATOR:
        command = (M) new HelixSpectatorCommand((SpectatorId) memberId);
        break;
      case CONTROLLER:
        command = (M) new HelixControllerCommand((ControllerId) memberId);
        break;
      default:
        command = (M) new HelixParticipantCommand((ParticipantId) memberId);
        break;
      }
    }

    /**
     * A command builder for Helix members
     * @param hostName the host name to start the member on
     * @return HelixMemberCommandBuilder
     */
    public HelixMemberCommandBuilder<T, M> forHost(String hostName) {
      command.setHostName(hostName);
      return this;
    }

    /**
     * Enables the member
     * @return <b>True</b> to enable the member, <b>False</b> to disable
     */
    public HelixMemberCommandBuilder<T, M> enable() {
      command.setEnabled(true);
      return this;
    }

    /**
     * Disables the member
     * @return <b>True</b> to disable the member, <b>False</b> to enable
     */
    public HelixMemberCommandBuilder<T, M> disable() {
      command.setEnabled(false);
      return this;
    }

    /**
     * Sets the port where the member should run
     * @param port the port number
     * @return HelixMemberCommandBuilder
     */
    protected HelixMemberCommandBuilder<T, M> forPort(int port) {
      command.setPort(port);
      return this;
    }

    /**
     * Builds a member command
     * @return M the member command
     */
    public M build() {
      return command;
    }

    /**
     * Tracks custom user properties against the cluster, the properties are serialized as json
     * unless the values are Externalizable in which case the property serialization is left to the
     * invoker
     * @param properties the properties to track
     * @return HelixMemberCommandBuilder
     */
    public HelixMemberCommandBuilder<T, M> withUserProperties(Map<String, Serializable> properties) {
      return null;
    }

    /**
     * Tracks custom user property against the cluster, the property value is serialized as json
     * using Javabean property descriptors unless the value is Externalizable in which case the
     * property value is serialized as chosen by the invoker
     * @param propertyName the property name
     * @param propertyValue the value to store
     * @return HelixClusterCommandBuilder
     */
    public HelixMemberCommandBuilder<T, M> withUserProperty(String propertyName,
        Serializable propertyValue) {
      return null;
    }

    /**
     * Returns the command for derived classes
     * @return <M extends HelixMemberCommand> an instance of HelixMemberCommand
     */
    protected M getCommand() {
      return this.command;
    }
  }

  /**
   * A command builder for the member command
   */
  public static class HelixParticipantCommandBuilder extends
      HelixMemberCommandBuilder<ParticipantId, HelixParticipantCommand> {

    /**
     * Creates a member command builder with a given member id
     * @param memberId
     */
    HelixParticipantCommandBuilder(ParticipantId memberId) {
      super(memberId, MemberType.PARTICIPANT);
    }

    /**
     * Sets the port where the member should run
     * @param port the port number
     * @return HelixMemberCommandBuilder
     */
    public HelixParticipantCommandBuilder forPort(int port) {
      super.forPort(port);
      return (HelixParticipantCommandBuilder) this;
    }

  }
}
