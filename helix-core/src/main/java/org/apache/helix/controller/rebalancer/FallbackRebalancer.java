package org.apache.helix.controller.rebalancer;

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

import java.util.Map;
import java.util.Set;

import org.apache.helix.HelixDataAccessor;
import org.apache.helix.HelixManager;
import org.apache.helix.PropertyKeyBuilder;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.model.PropertyKey;
import org.apache.helix.api.model.statemachine.State;
import org.apache.helix.api.model.statemachine.StateModelDefinition;
import org.apache.helix.api.model.strategy.RebalancerConfiguration;
import org.apache.helix.api.snapshot.Cluster;
import org.apache.helix.controller.context.ControllerContextProvider;
import org.apache.helix.controller.rebalancer.config.BasicRebalancerConfig;
import org.apache.helix.controller.rebalancer.util.ConstraintBasedAssignment;
import org.apache.helix.controller.stages.ClusterDataCache;
import org.apache.helix.controller.stages.CurrentStateOutput;
import org.apache.helix.controller.stages.ResourceCurrentState;
import org.apache.helix.model.IdealState;
import org.apache.helix.model.IdealState.RebalanceMode;
import org.apache.helix.model.Partition;
import org.apache.helix.model.ResourceAssignment;
import org.apache.helix.util.HelixUtil;
import org.apache.log4j.Logger;

/**
 * This class is intented for use to wrap usages of {@link Rebalancer}. It is subject to removal
 * once that class is removed.
 */
@SuppressWarnings("deprecation")
public class FallbackRebalancer implements HelixRebalancer {
  private static final Logger LOG = Logger.getLogger(FallbackRebalancer.class);
  private HelixManager _helixManager;

  @Override
  public void init(HelixManager helixManager, ControllerContextProvider contextProvider) {
    _helixManager = helixManager;
  }

  @Override
  public ResourceAssignment computeResourceMapping(RebalancerConfiguration rebalancerConfig,
      ResourceAssignment prevAssignment, Cluster cluster, ResourceCurrentState currentState) {
    // make sure the manager is not null
    if (_helixManager == null) {
      LOG.info("HelixManager is null!");
      return null;
    }

    // get the config
    BasicRebalancerConfig config =
        BasicRebalancerConfig.convert(rebalancerConfig, BasicRebalancerConfig.class);
    if (config == null) {
      LOG.info("Resource is not partitioned");
      return null;
    }

    // get the ideal state and rebalancer class
    ResourceId resourceId = config.getResourceId();
    StateModelDefinition stateModelDef =
        cluster.getStateModelMap().get(config.getStateModelDefId());
    if (stateModelDef == null) {
      LOG.info("StateModelDefinition unavailable for " + resourceId);
      return null;
    }
    HelixDataAccessor accessor = _helixManager.getHelixDataAccessor();
    PropertyKeyBuilder keyBuilder = accessor.keyBuilder();
    IdealState idealState = accessor.getProperty(keyBuilder.idealStates(resourceId.toString()));
    if (idealState == null) {
      LOG.info("No IdealState available for " + resourceId);
      return null;
    }
    String rebalancerClassName = idealState.getRebalancerClassName();
    if (rebalancerClassName == null) {
      LOG.info("No Rebalancer class available for " + resourceId);
      return null;
    }

    // try to instantiate the rebalancer class
    Rebalancer rebalancer = null;
    try {
      rebalancer =
          (Rebalancer) (HelixUtil.loadClass(getClass(), rebalancerClassName).newInstance());
    } catch (Exception e) {
      LOG.warn("rebalancer " + rebalancerClassName + " not available", e);
    }
    if (rebalancer == null) {
      LOG.warn("Rebalancer class " + rebalancerClassName + " could not be instantiated for "
          + resourceId);
      return null;
    }

    // get the cluster data cache (unfortunately involves a second read of the cluster)
    ClusterDataCache cache = new ClusterDataCache();
    cache.refresh(accessor);

    // adapt ResourceCurrentState to CurrentStateOutput
    CurrentStateOutput currentStateOutput = new CurrentStateOutput();
    for (ResourceId resource : currentState.getResourceIds()) {
      currentStateOutput.setBucketSize(resource.toString(), currentState.getBucketSize(resource));
      currentStateOutput.setResourceStateModelDef(resource.toString(), currentState
          .getResourceStateModelDef(resource).toString());
      Set<PartitionId> partitions = currentState.getCurrentStateMappedPartitions(resource);
      for (PartitionId partitionId : partitions) {
        // set current state
        Map<ParticipantId, State> currentStateMap =
            currentState.getCurrentStateMap(resource, partitionId);
        for (ParticipantId participantId : currentStateMap.keySet()) {
          currentStateOutput.setCurrentState(resource.toString(),
              new Partition(partitionId.toString()), participantId.toString(), currentStateMap
                  .get(participantId).toString());
        }

        // set pending current state
        Map<ParticipantId, State> pendingStateMap =
            currentState.getPendingStateMap(resource, partitionId);
        for (ParticipantId participantId : pendingStateMap.keySet()) {
          currentStateOutput.setPendingState(resource.toString(),
              new Partition(partitionId.toString()), participantId.toString(), pendingStateMap
                  .get(participantId).toString());
        }
      }
    }

    // call the rebalancer
    rebalancer.init(_helixManager);
    IdealState newIdealState =
        rebalancer.computeResourceMapping(resourceId.toString(), idealState, currentStateOutput,
            cache);

    // do the resource assignments
    ResourceAssignment assignment = new ResourceAssignment(resourceId);
    if (idealState.getRebalanceMode() == RebalanceMode.CUSTOMIZED) {
      // customized ideal state uses a map
      for (PartitionId partitionId : newIdealState.getPartitionIdSet()) {
        Set<ParticipantId> disabledParticipants =
            ConstraintBasedAssignment.getDisabledParticipants(cluster.getParticipantMap(),
                partitionId);
        Map<ParticipantId, State> replicaMap =
            ConstraintBasedAssignment.computeCustomizedBestStateForPartition(cluster
                .getLiveParticipantMap().keySet(), stateModelDef, newIdealState
                .getParticipantStateMap(partitionId), currentState.getCurrentStateMap(resourceId,
                partitionId), disabledParticipants);
        assignment.addReplicaMap(partitionId, replicaMap);
      }
    } else {
      // other modes use auto assignment
      Map<State, String> upperBounds =
          ConstraintBasedAssignment
              .stateConstraints(stateModelDef, resourceId, cluster.getConfig());
      for (PartitionId partitionId : newIdealState.getPartitionIdSet()) {
        Set<ParticipantId> disabledParticipants =
            ConstraintBasedAssignment.getDisabledParticipants(cluster.getParticipantMap(),
                partitionId);
        Map<ParticipantId, State> replicaMap =
            ConstraintBasedAssignment.computeAutoBestStateForPartition(upperBounds, cluster
                .getLiveParticipantMap().keySet(), stateModelDef, newIdealState
                .getPreferenceList(partitionId), currentState.getCurrentStateMap(resourceId,
                partitionId), disabledParticipants);
        assignment.addReplicaMap(partitionId, replicaMap);
      }
    }
    return assignment;
  }
}
