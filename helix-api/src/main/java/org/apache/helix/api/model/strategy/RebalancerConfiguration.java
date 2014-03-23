package org.apache.helix.api.model.strategy;

import java.util.Set;

import org.apache.helix.api.model.id.PartitionId;
import org.apache.helix.api.model.id.ResourceId;
import org.apache.helix.api.model.statemachine.id.StateModelDefinitionId;
import org.apache.helix.api.model.statemachine.id.StateModelFactoryId;

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
 * Defines the state available to a rebalancer. The most common use case is to use a
 * {@link PartitionedRebalancerConfig} or a subclass and set up a resource with it. A rebalancer
 * configuration, at a minimum, is aware of subunits of a resource, the state model to follow, and
 * how the configuration should be serialized.
 */
public interface RebalancerConfiguration {

  /**
   * Get the partitions of the resource
   * @return set of subunit ids
   */
  public Set<PartitionId> getPartitionSet();

  /**
   * Get the resource to rebalance
   * @return resource id
   */
  public ResourceId getResourceId();

  /**
   * Get the state model definition that the resource follows
   * @return state model definition id
   */
  public StateModelDefinitionId getStateModelDefId();

  /**
   * Get the state model factory of this resource
   * @return state model factory id
   */
  public StateModelFactoryId getStateModelFactoryId();

  /**
   * Get the tag, if any, that participants must have in order to serve this resource
   * @return participant group tag, or null
   */
  public String getParticipantGroupTag();

  /**
   * Get the maximum number of replicas of a state with dynamic upper bound "R" that each resource
   * partition can have.
   * @return replica count
   */
  public int getReplicaCount();
  // TODO: getRebalancerRef needs to be at this level
  // TODO: getRebalanceMode needs to be at this level

}
