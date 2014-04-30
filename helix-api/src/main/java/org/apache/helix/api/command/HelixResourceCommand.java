package org.apache.helix.api.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
  private final Map<String, Serializable> properties;

  /**
   * Creates a resource command for a given cluster
   * @param resourceId the resource identifier
   */
  public HelixResourceCommand(ResourceId resourceId) {
    this.resourceId = resourceId;
    this.properties = new HashMap<String, Serializable>();
  }
  
  /**
   * Returns the resource id
   * @return the resource id
   */
  public ResourceId getId(){
    return this.resourceId;
  }
  
  /**
   * Sets the partitions for the resource
   * @param partitions the total partitions for the resource
   */
  public void setPartitions(int partitions){
    this.partitions = partitions;
  }
  
  /**
   * Retrieves the partitions configured for the resource
   * @return the number of partitions
   */
  public int getPartitions(){
    return partitions;
  }
  
  /**
   * Identifies the rebalancer to use for the partitions. Custom Rebalancers
   * are first added to the rebalancer registry and then referenced here.
   * @param rebalancerId
   */
  public void setRebalancerId(RebalancerId rebalancerId){
    this.rebalancerId = rebalancerId;
  }
  
  /**
   * Retrieves the rebalancer id
   * @return the rebalancer id
   */
  public RebalancerId getRebalancerId(){
    return rebalancerId;
  }
  
  /**
   * Identifies the state model definition for this resource
   * @param stateModelDefinitionId 
   */
  public void setStateModelDefinitionId(StateModelDefinitionId stateModelDefinitionId){
    this.stateModelDefinitionId = stateModelDefinitionId;
  }
  
  /**
   * Retrieves the state model definition identifier
   * @return the state model definition id
   */
  public StateModelDefinitionId getStateModelDefinitionId(){
    return stateModelDefinitionId;
  }
  
  /**
   * A set of name-value pairs that the user can pass in
   * @param properties the data user wants to track
   */
  public void setUserProperties(Map<String, Serializable> properties){
    this.properties.putAll(properties);
  }
  
  /**
   * A set of name-value pairs that the user can pass in
   * @param key the user property name
   * @param value the user property value
   */
  public void addUserProperty(String key, Serializable value){
    this.properties.put(key, value);
  }
}
