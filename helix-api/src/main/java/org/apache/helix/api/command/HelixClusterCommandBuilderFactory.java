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

import org.apache.helix.api.id.ClusterId;

/**
 * A builder for cluster commands, the builder is intended to be fluent so that the client
 * will build the command in a manner that enforces order of adding the correct commands
 * to create the
 */
public class HelixClusterCommandBuilderFactory {

  /**
   * Adds participants to the command and progresses the command towards adding resources
   */
  interface AddParticipants {
    public AddResources withParticipants(Set<HelixParticipantCommand> participants);
  }

  /**
   * Adds the cluster constraints to the command, it then chains to the state model definition
   */
  interface AddClusterConstraint {
    public AddStateModelDefinition withConstraints(HelixClusterConstraintCommand constraints);
  }

  /**
   * The cluster identifier is added through this interface. The interface then leads to the
   * next step in the chain which is to add the cluster constraints.
   */
  interface SetClusterIdentifier {
    public AddClusterConstraint withId(ClusterId id);
  }

  /**
   * Adds resources to the command and allows finalizations to create the command
   */
  interface AddResources {
    public Finalize withResources(Set<HelixResourceCommand> resources);
  }

  /**
   * Adds the state model definition to the command allow to add participants next
   */
  interface AddStateModelDefinition {
    public AddParticipants withStateModelDefinition(HelixStateModelCommand command);
  }

  /**
   * Finalizes the command chain and returns the completely built command
   */
  interface Finalize {
    public HelixClusterCommand build();
  }

  /**
   * Creates a command builder and initiates the segment in the chain
   * @return the first step to the command creation
   */
  public static final SetClusterIdentifier createBuilder() {
    return new HelixClusterCommandBuilder();
  }

  /**
   * The command builder class which implements all the interfaces
   */
  public static class HelixClusterCommandBuilder implements SetClusterIdentifier, AddParticipants,
      AddClusterConstraint, AddResources, AddStateModelDefinition, Finalize {

    HelixClusterCommandBuilder() {
    }

    @Override
    public AddStateModelDefinition withConstraints(HelixClusterConstraintCommand constraints) {
      return null;
    }

    @Override
    public AddResources withParticipants(Set<HelixParticipantCommand> participants) {
      return null;
    }

    @Override
    public AddClusterConstraint withId(ClusterId id) {
      return null;
    }

    @Override
    public Finalize withResources(Set<HelixResourceCommand> resources) {
      return null;
    }

    @Override
    public AddParticipants withStateModelDefinition(HelixStateModelCommand command) {
      return null;
    }

    @Override
    public HelixClusterCommand build() {
      // TODO Auto-generated method stub
      return null;
    }
  }

  /**
   * This is just for illustration purposes
   * @param args
   */
  public static final void main(String args[]) {
    ClusterId id = null;
    HelixClusterConstraintCommand constraint = null;
    Set<HelixParticipantCommand> participants = null;
    Set<HelixResourceCommand> resources = null;
    HelixStateModelCommand stateModelDefinition = null;
    HelixClusterCommandBuilderFactory.createBuilder().withId(id).withConstraints(constraint)
        .withStateModelDefinition(stateModelDefinition).withParticipants(participants)
        .withResources(resources).build();
  }
}
