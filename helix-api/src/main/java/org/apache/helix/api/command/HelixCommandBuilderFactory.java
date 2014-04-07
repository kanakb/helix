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
 * A builder factory for commands, this factory is the starting point for creating commands which
 * can then be fed to the HelixAdministrator client
 */
public class HelixCommandBuilderFactory {

  /**
   * Creates a command builder for Cluster Commands
   */
  public static final HelixClusterCommandBuilder createClusterBuilder(ClusterId id) {
    return new HelixClusterCommandBuilder(id);
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
     * Adds cluster constraints to the cluster command
     * @param constraints
     * @return
     */
    public HelixClusterCommandBuilder withConstraints(HelixClusterConstraintCommand constraints) {
      return null;
    }

    /**
     * Adds a set of participants to the cluster command
     * @param participants
     * @return
     */
    public HelixClusterCommandBuilder withParticipants(Set<HelixParticipantCommand> participants) {
      return null;
    }

    /**
     * Adds a set of resources to the command
     * @param resources
     * @return
     */
    public HelixClusterCommandBuilder withResources(Set<HelixResourceCommand> resources) {
      return null;
    }

    /**
     * Defines the state model definition for the command
     * @param command
     * @return
     */
    public HelixClusterCommandBuilder withStateModelDefinition(HelixStateModelCommand command) {
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
   * This is just for illustration purposes
   * @param args
   */
  public static final void main(String args[]) {
    ClusterId id = null;
    HelixClusterConstraintCommand constraint = null;
    Set<HelixParticipantCommand> participants = null;
    Set<HelixResourceCommand> resources = null;
    HelixStateModelCommand stateModelDefinition = null;
    HelixCommandBuilderFactory.createClusterBuilder(id).withConstraints(constraint)
        .withStateModelDefinition(stateModelDefinition).withParticipants(participants)
        .withResources(resources).build();
  }
}
