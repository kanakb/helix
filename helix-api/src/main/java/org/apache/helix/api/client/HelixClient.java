package org.apache.helix.api.client;

import java.util.List;

import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.MemberId;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.model.Cluster;
import org.apache.helix.api.model.Controller;
import org.apache.helix.api.model.Participant;
import org.apache.helix.api.model.Partition;
import org.apache.helix.api.model.Resource;

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
  abstract Cluster findCluster(ClusterId clusterId);

  /**
   * Locates a member with the given id. A member can be of different types
   * @see HelixMemberCommand.MemberType for different types
   * @param memberId the member id
   * @return HelixMember the member with the given id
   */
  abstract Participant findParticipant(ParticipantId participantId);

  /**
   * Locates a resource with a given id.
   * @param resourceId the resource id
   * @return HelixResource the resource with the given id
   */
  abstract Resource findResource(ResourceId resourceId);

  /**
   * Retrieves all participants in a given cluster
   * @param clusterId the cluster id
   * @return List<HelixParticipant> the list of helix members assigned to the cluster
   */
  abstract List<Participant> getParticipants(ClusterId clusterId);
  
  /**
   * Retrieves the controller for a given cluster
   * @param clusterId the cluster id
   * @return List<HelixController> the controllers for the cluster
   */
  abstract List<Controller> getControllers(ClusterId clusterId);
  
  /**
   * Retrieves the leader controller for a given cluster
   * @param clusterId the cluster id
   * @return HelixController the leader controller for the cluster, there can only be one
   */
  abstract Controller getLeader(ClusterId clusterId);

  /**
   * Retrieves all resources in the cluster
   * @param clusterId the cluster id
   * @return List<HelixResource> the list of resources in the cluster
   */
  abstract List<Resource> getResources(ClusterId clusterId);

  /**
   * Retrieves all resources assigned to a given HelixMember
   * @param memberId the member id
   * @return List<HelixResource> the list of resources assigned to the member
   */
  abstract List<Resource> getResources(MemberId memberId);
  
  /**
   * Retrieves all partitions for a given resource
   * @param resourceId the resource id
   * @return List<HelixPartition> the list of partitions for the resource
   */
  abstract List<Partition> getPartitions(ResourceId resourceId);
}
