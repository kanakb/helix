package org.apache.helix.api.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.helix.api.id.ClusterId;

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
 * A command used to create Helix cluster
 */
public class HelixClusterCommand {
  private HelixStateModelDefinitionCommand stateModelDefinition;
  private final Set<HelixResourceCommand> resources;
  private final Set<HelixParticipantCommand> participants;
  private ClusterId clusterId;
  private boolean allowAutoJoin;
  private Set<HelixConstraintCommand> constraints;
  private boolean recreate;
  private Map<String, Serializable> properties;
  private Status status;
  
  private enum Status{
    PAUSED,
    
    RUNNING
  }
  
  /**
   * Creates a cluster mutation command for a cluster identified by its id
   * @param clusterId the cluster id
   */
  public HelixClusterCommand(ClusterId clusterId) {
    this.clusterId = clusterId;
    resources = new HashSet<HelixResourceCommand>();
    participants = new HashSet<HelixParticipantCommand>();
    constraints = new HashSet<HelixConstraintCommand>();
    properties = new HashMap<String, Serializable>();
  }

  /**
   * Registers a state model definition for the cluster, the statemodel
   * definition is applied to the cluster as well as resources added to the
   * cluster
   * @param stateModelCommand a command which identifies the state model to
   *          create for the cluster
   */
  public void setStateModel(HelixStateModelDefinitionCommand stateModelCommand) {
    this.stateModelDefinition = stateModelCommand;
  }

  /**
   * Adds a constraint command to the cluster, the command identifies the
   * constraint applied at the appropriate level
   * @param constraint
   */
  public <T extends HelixConstraintCommand> void addConstraint(T constraint) {
    constraints.add(constraint);
  }

  /**
   * Returns the cluster id for this cluster
   * @return ClusterId
   */
  public ClusterId getClusterId() {
    return this.clusterId;
  }

  /**
   * Adds resources to the cluster
   * @param resources the set of resources
   */
  public void addResources(Set<HelixResourceCommand> resources) {
    this.resources.addAll(resources);
  }

  /**
   * Removes a resource from the cluster
   * @param resource the resource to remove from the cluster
   * @return <b>True</b> if the resource was removed, <b>False</b> if the removal failed
   */
  public boolean removeResource(HelixResourceCommand resource) {
    return this.resources.remove(resource);
  }

  /**
   * Adds participants to the cluster
   * 
   * @param participants the set of participants to add
   */
  public void addParticipants(Set<HelixParticipantCommand> participants) {
    this.participants.addAll(participants);
  }

  /**
   * Indicates if the cluster allows auto-join of members
   * @return <b>True</b> if the members can auto-join <b>False</b> if they cannot
   */
  public boolean isAllowAutoJoin() {
    return allowAutoJoin;
  }

  /**
   * Configures the cluster for auto-join of members
   * @param allowAutoJoin <b>True</b> if the members can auto-join <b>False</b> if they cannot
   */
  public void setAllowAutoJoin(boolean allowAutoJoin) {
    this.allowAutoJoin = allowAutoJoin;
  }
  
  /**
   * Dictates if the cluster should be recreated even if it exists
   * @param recreate <b>True</b>to recreate the cluster, <b>False</b> to prevent recreate
   */
  public void recreateIfExists(boolean recreate){
    this.recreate = recreate;
  }
  
  /**
   * Indicates if the cluster command is set to recreate the cluster if it exists
   * @return <b>True</b> if recreation is necessary, <b>False</b> if not
   */
  public boolean doRecreateIfExists(){
    return this.recreate;
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
  
  /**
   * Pauses the cluster
   */
  public void pause(){
    this.status = Status.PAUSED;
  }
  
  /**
   * Checks if the cluster is paused
   * 
   * @return <b>True</b> if the cluster is paused, <b>False</b> if it is not
   */
  public boolean isPaused(){
    return status.equals(Status.PAUSED);
  }
  
  /**
   * Checks if the cluster is running
   * 
   * @return <b>True</b> if the cluster is running, <b>False</b> if it is not
   */
  public boolean isRunning(){
    return status.equals(Status.RUNNING);
  }
  
  /**
   * Resumes the cluster
   */
  public void resume(){
    this.status = Status.RUNNING;
  }
}
