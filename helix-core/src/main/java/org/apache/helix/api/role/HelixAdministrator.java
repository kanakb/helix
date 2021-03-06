package org.apache.helix.api.role;

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

import java.util.Collection;
import java.util.List;

import org.apache.helix.api.command.ClusterCommand;
import org.apache.helix.api.command.ResourceCommand;
import org.apache.helix.api.config.ClusterConfig;
import org.apache.helix.api.config.ResourceConfig;
import org.apache.helix.api.id.ClusterId;
import org.apache.helix.api.id.Id;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.model.Scope;
import org.apache.helix.api.model.UserConfig;
import org.apache.helix.api.model.constraint.ClusterConstraints;
import org.apache.helix.api.model.constraint.ClusterConstraints.ConstraintType;
import org.apache.helix.api.model.statemachine.Transition;
import org.apache.helix.api.snapshot.Cluster;
import org.apache.helix.api.snapshot.Resource;
import org.apache.helix.api.snapshot.Participant;
import org.apache.helix.api.snapshot.RunningInstance;
import org.apache.helix.core.StateModelDefinition;

/**
 * Starting point for all cluster administration tasks. TODO: define failure behavior
 */
public interface HelixAdministrator extends HelixStartable {
  List<ClusterId> listClusters();

  void manageClusterWithControllerCluster(ClusterId clusterId, ClusterId controllerClusterId);

  void addCluster(ClusterConfig clusterConfig);

  void updateCluster(ClusterId clusterId, ClusterCommand command);

  Cluster readCluster(ClusterId clusterId);

  void dropCluster(ClusterId clusterId);

  List<ResourceId> listResources(ClusterId clusterId);

  List<ResourceId> listTaggedResources(ClusterId clusterId, String tag);

  void addResource(ClusterId clusterId, ResourceConfig resourceConfig);

  void updateResource(ClusterId clusterId, ResourceId resourceId, ResourceCommand command);

  Resource readResource(ClusterId clusterId, ResourceId resourceId);

  void dropResource(ClusterId clusterId, ResourceId resourceId);

  List<ParticipantId> listParticipants(ClusterId clusterId);

  List<ParticipantId> listTaggedParticipants(ClusterId clusterId, String tag);

  void addParticipant(ClusterId clusterId, ParticipantId participantId);

  void updateParticipant(ClusterId clusterId, ParticipantId participantId);

  Participant readParticipant(ClusterId clusterId, ParticipantId participantId);

  void dropParticipant(ClusterId clusterId, ParticipantId participantId);

  List<StateModelDefinitionId> listStateModelDefinitions(ClusterId clusterId);

  void addStateModelDef(ClusterId clusterId, StateModelDefinition stateModelDef);

  StateModelDefinition readStateModelDef(ClusterId clusterId, StateModelDefinitionId stateModelDefId);

  void dropStateModelDef(ClusterId clusterId, StateModelDefinitionId stateModelDefId);

  <T extends Id> void addUserConfig(ClusterId clusterId, Scope<T> scope, UserConfig userConfig);

  <T extends Id> void replaceUserConfig(ClusterId clusterId, Scope<T> scope, UserConfig userConfig);

  <T extends Id> void removeUserConfig(ClusterId clusterId, Scope<T> scope,
      Collection<String> configKeys);

  <T extends Id> UserConfig getUserConfig(ClusterId clusterId, Scope<T> scope);

  // TODO: is this how we should do constraints?
  // we probably need something more general, but easier to understand than ClusterConstraints
  <T extends Id> void setTransitionConstraint(ClusterId clusterId, Scope<T> scope,
      Transition transition, int maxInParallel);

  <T extends Id> void removeTransitionConstraint(ClusterId clusterId, Scope<T> scope,
      Transition transition);

  <T extends Id> ClusterConstraints getConstraints(ClusterId clusterId, ConstraintType type);
}
