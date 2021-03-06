package org.apache.helix.api.client;

import java.util.List;

import org.apache.helix.api.command.HelixAdministratorCommand;
import org.apache.helix.api.command.HelixClusterCommand;
import org.apache.helix.api.command.HelixParticipantCommand;
import org.apache.helix.api.command.HelixPartitionCommand;
import org.apache.helix.api.command.HelixResourceCommand;
import org.apache.helix.api.command.HelixSpectatorCommand;
import org.apache.helix.api.id.AdministratorId;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.MemberId;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.SpectatorId;
import org.apache.helix.api.model.Administrator;
import org.apache.helix.api.model.Cluster;
import org.apache.helix.api.model.Controller;
import org.apache.helix.api.model.Member;
import org.apache.helix.api.model.Participant;
import org.apache.helix.api.model.Partition;
import org.apache.helix.api.model.Resource;
import org.apache.helix.api.model.Spectator;
import org.apache.helix.api.query.HelixQuery;

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
 * The client provides ability to discover various entities in the
 * helix ecosystem
 */
public abstract class HelixClient {

  /**
   * Locates a cluster with a given id
   * @param clusterId the cluster id
   * @return HelixCluster the cluster with the given id
   */
  public abstract Cluster findClusterById(ClusterId clusterId);

  /**
   * Locates a member with the given id. A member can be of different types
   * @param participantId the participant id
   * @return HelixMember the member with the given id
   */
  public abstract Participant findParticipantById(ParticipantId participantId);

  /**
   * Locates a resource with a given id.
   * @param resourceId the resource id
   * @return HelixResource the resource with the given id
   */
  public abstract Resource findResourceById(ResourceId resourceId);

  /**
   * Retrieves all participants in a given cluster
   * @param query the participant query
   * @return List<HelixParticipant> the list of helix members assigned to the cluster
   */
  public abstract <Q extends HelixQuery> List<Participant> findParticipants(Q query);

  /**
   * Retrieves the controller for a given cluster
   * @param query the controller query
   * @return List<HelixController> the controllers for the cluster
   */
  public abstract <Q extends HelixQuery> List<Controller> findControllers(Q query);

  /**
   * Retrieves the leader controller for a given cluster
   * @param clusterId the cluster id
   * @return HelixController the leader controller for the cluster, there can only be one
   */
  public abstract Controller findLeader(ClusterId clusterId);

  /**
   * Retrieves all resources in the cluster
   * @param query the resource query
   * @return List<HelixResource> the list of resources in the cluster
   */
  public abstract <Q extends HelixQuery> List<Resource> findResources(Q query);
  
  /**
   * Finds all members which meet the criteria specified in the query
   * @param query
   * @return List<Member<T>>
   */
  public abstract <T extends MemberId, Q extends HelixQuery> List<Member<T>> findMembers(Q query);

  /**
   * Retrieves all partitions for a given resource
   * @param query the resource query
   * @return List<HelixPartition> the list of partitions for the resource
   */
  public abstract <Q extends HelixQuery> List<Partition> findPartitions(Q query);

  /**
   * Creates a cluster based on the command passed
   * @param command
   * @return Cluster
   */
  public abstract Cluster addCluster(HelixClusterCommand command);

  /**
   * Update the cluster based on the command passed
   * @param command
   * @return Cluster
   */
  public abstract Cluster updateCluster(HelixClusterCommand command);

  /**
   * Removes a cluster from the helix environment, a cluster can only be removed if all its
   * members, resources and partitions are removed
   * @param clusterId the id of the cluster to remove
   * @return boolean <b>True</b>if the remove succeeds, <b>False</b> if not
   */
  public abstract boolean removeCluster(ClusterId clusterId);

  /**
   * Adds a cluster participant based on the command to the cluster
   * @param command
   * @return HelixParticipant
   */
  public abstract Participant addParticipant(HelixParticipantCommand command);

  /**
   * Updates a cluster participant based on the command to the cluster
   * @param command
   * @return HelixParticipant
   */
  public abstract Participant updateParticipant(HelixParticipantCommand command);

  /**
   * Adds participants to the cluster
   * @param commands the commands for the participants
   * @return List<HelixParticipant>
   */
  public abstract List<Participant> addParticipants(List<HelixParticipantCommand> commands);

  /**
   * Removes a participant from the cluster
   * @param id the participant id to remove
   * @return boolean <b>True</b> if the participant is removed, <b>False</b> if the participant
   *         cannot be removed
   */
  public abstract boolean removeParticipant(ParticipantId id);

  /**
   * Adds a cluster spectator based on the command to the cluster
   * @param command
   * @return HelixSpectator
   */
  public final Spectator addSpectator(HelixSpectatorCommand command) {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds spectators to the cluster
   * @param commands the commands for the spectators
   * @return List<HelixSpectator>
   */
  public final List<Spectator> addSpectators(List<HelixSpectatorCommand> commands) {
    throw new UnsupportedOperationException();
  }

  /**
   * Removes a spectator from the cluster
   * @param id the spectator to remove
   * @return boolean <b>True</b> if the spectator is removed, <b>False</b> if the removal fails
   */
  public final boolean removeSpectator(SpectatorId id) {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds a cluster administrator based on the command to the cluster.
   * @param command
   * @return HelixAdministrator
   */
  public final Administrator addAdministrator(HelixAdministratorCommand command) {
    throw new UnsupportedOperationException();
  }

  /**
   * Removes a controller from the cluster
   * @param id the controller to remove
   * @return boolean <b>True</b>if the controller is removed, <b>False</b> if removal fails
   */
  public final boolean removeAdministrator(AdministratorId id) {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds a resource to the cluster
   * @param command the resource command
   * @return HelixResource
   */
  public abstract Resource addResource(HelixResourceCommand command);

  /**
   * Removes a resource from the cluster
   * @param resourceId the resource to remove
   * @return boolean <b>True</b>if removal succeeds, <b>False</b> if removal fails
   */
  public abstract boolean removeResource(ResourceId resourceId);

  /**
   * Adds a list of resource to the cluster
   * @param commands the resource commands
   * @return List<Resource>
   */
  public abstract List<Resource> addResources(List<HelixResourceCommand> commands);

  /**
   * Adds a partition to the cluster, a partition is always added for a resource. The command
   * identifies the resource that is partitioned
   * @param command
   * @return HelixPartition
   */
  public abstract Partition addPartition(HelixPartitionCommand command);

  /**
   * Adds partitions to the cluster, a partition is always added for a resource. The command
   * identifies the resource that is partitioned
   * @param command
   * @return List<HelixPartition>
   */
  public abstract List<Partition> addPartitions(List<HelixPartitionCommand> command);

  /**
   * Removes a partition from the resource
   * @param id the parition to remove
   * @return boolean <b>True</b> if the partition is removed, <b>False</b> if it is not removed
   */
  public abstract boolean removePartition(PartitionId id);
}
