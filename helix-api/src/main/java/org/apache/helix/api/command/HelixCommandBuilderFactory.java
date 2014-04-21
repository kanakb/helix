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
   * @param id the cluster id to create the command for
   * @return HelixClusterCommandBuilder a command builder for cluster
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
    public HelixClusterCommandBuilder withStateModelDefinition(HelixStateModelCommand command) {
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
    Set<HelixParticipantCommand> participants = null;
    Set<HelixResourceCommand> resources = null;
    HelixStateModelCommand stateModelDefinition = null;
    HelixCommandBuilderFactory.createClusterBuilder(id)
        .withStateModelDefinition(stateModelDefinition).withParticipants(participants)
        .withResources(resources).build();
  }
}
