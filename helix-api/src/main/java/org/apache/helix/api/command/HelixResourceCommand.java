package org.apache.helix.api.command;

import org.apache.helix.api.id.RebalancerId;
import org.apache.helix.api.id.ResourceId;
import org.apache.helix.api.id.StateModelDefinitionId;

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
 * Allows creating resources for the cluster
 */
public class HelixResourceCommand {
  private final ResourceId resourceId;
  private int partitions;
  private StateModelDefinitionId stateModelDefinitionId;
  private RebalancerId rebalancerId;

  /**
   * Creates a resource command for a given cluster
   * @param clusterId the cluster to which the resource is added
   */
  public HelixResourceCommand(ResourceId resourceId) {
    this.resourceId = resourceId;
  }
  
  public void setPartitions(int partitions){
    this.partitions = partitions;
  }
  
  public int getPartitions(){
    return partitions;
  }
  
  public void setRebalancerId(RebalancerId rebalancerId){
    this.rebalancerId = rebalancerId;
  }
  
  public RebalancerId getRebalancerId(){
    return rebalancerId;
  }
  
  public void setStateModelDefinitionId(StateModelDefinitionId stateModelDefinitionId){
    this.stateModelDefinitionId = stateModelDefinitionId;
  }
  
  public StateModelDefinitionId getStateModelDefinitionId(){
    return stateModelDefinitionId;
  }
}
