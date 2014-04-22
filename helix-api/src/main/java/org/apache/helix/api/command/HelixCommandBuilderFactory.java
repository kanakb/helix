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

import java.util.Set;

import org.apache.helix.api.command.HelixMemberCommand.MemberType;
import org.apache.helix.api.id.AdministratorId;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.ControllerId;
import org.apache.helix.api.id.MemberId;
import org.apache.helix.api.id.ParticipantId;
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
  public static final HelixClusterCommandBuilder createClusterBuilder(ClusterId id) {
    return new HelixClusterCommandBuilder(id);
  }

  /**
   * Creates a command builder for State Model Commands
   * @param id the state model id to create the command for
   * @return HelixStateModelCommandBuilder a command builder for cluster
   */
  public static final HelixStateModelDefinitionCommandBuilder createStateModelDefinitionBuilder(
      StateModelDefinitionId id) {
    return new HelixStateModelDefinitionCommandBuilder(id);
  }

  /**
   * Creates a command builder for resource Commands
   * @param id the resource id to create the command for
   * @return HelixStateModelCommandBuilder a command builder for resource
   */
  public static final HelixResourceCommandBuilder createResourceBuilder(ResourceId id) {
    return new HelixResourceCommandBuilder(id);
  }

  /**
   * Creates a member builder of type participant
   * @param participantId the participant id
   * @return HelixMemberCommandBuilder which allows creating a participant member
   */
  public static final <T extends MemberId> HelixMemberCommandBuilder<T> createParticipantMemberBuilder(
      T participantId) {
    return new HelixMemberCommandBuilder<T>(participantId, MemberType.PARTICIPANT);
  }

  /**
   * Creates a member builder of type administrator
   * @param administratorId the administrator id
   * @return HelixMemberCommandBuilder which allows creating a administrator member
   */
  public static final <T extends MemberId> HelixMemberCommandBuilder<T> createAdministratorMemberBuilder(
      T administratorId) {
    throw new UnsupportedOperationException(
        "Currently members of type administrator cannot be created");
  }

  /**
   * Creates a member builder of type controller
   * @param controllerId the controller id
   * @return HelixMemberCommandBuilder which allows creating a controller member
   */
  public static final <T extends MemberId> HelixMemberCommandBuilder<T> createControllerMemberBuilder(
      T controllerId) {
    throw new UnsupportedOperationException(
        "Currently members of type controller cannot be created");
  }

  /**
   * Creates a member builder of type spectator
   * @param spectatorId the spectator id
   * @return HelixMemberCommandBuilder which allows creating a spectator member
   */
  public static final <T extends MemberId> HelixMemberCommandBuilder<T> createSpectatorMemberBuilder(
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
     * Indicates if the cluster can be auto-started
     * @param autoStart <b>True</b>if the cluster can be auto-started, <b>False</b> if it cannot be
     * @return HelixClusterCommandBuilder an instance of a cluster command builder
     */
    public HelixClusterCommandBuilder withAutoStart(boolean autoStart) {
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
   * A command builder for the member command
   * @param <T> a derived type of MemberId
   */
  public static class HelixMemberCommandBuilder<T extends MemberId> {
    HelixMemberCommand command;

    /**
     * Creates a member command builder with a given member id
     * @param memberId
     */
    HelixMemberCommandBuilder(T memberId, MemberType type) {
      switch (type) {
      case ADMINISTRATOR:
        command = new HelixAdministratorCommand((AdministratorId) memberId);
        break;
      case SPECTATOR:
        command = new HelixSpectatorCommand((SpectatorId) memberId);
        break;
      case CONTROLLER:
        command = new HelixControllerCommand((ControllerId) memberId);
        break;
      default:
        command = new HelixParticipantCommand((ParticipantId) memberId);
        break;
      }
    }

    /**
     * A command builder for Helix members 
     * @param hostName the host name to start the member on
     * @return HelixMemberCommandBuilder
     */
    public HelixMemberCommandBuilder<T> forHost(String hostName) {
      command.setHostName(hostName);
      return this;
    }

    /**
     * Sets the port where the member should run
     * @param port the port number
     * @return HelixMemberCommandBuilder
     */
    public HelixMemberCommandBuilder<T> forPort(int port) {
      command.setPort(port);
      return this;
    }

    /**
     * Enables the member
     * @return <b>True</b> to enable the member, <b>False</b> to disable
     */
    public HelixMemberCommandBuilder<T> enable() {
      command.setEnabled(true);
      return this;
    }

    /**
     * Disables the member
     * @return <b>True</b> to disable the member, <b>False</b> to enable
     */
    public HelixMemberCommandBuilder<T> disable() {
      command.setEnabled(false);
      return this;
    }

    /**
     * Builds a member command 
     * @return M the member command
     */
    public <M extends HelixMemberCommand> M build() {
      return null;
    }
  }
}
