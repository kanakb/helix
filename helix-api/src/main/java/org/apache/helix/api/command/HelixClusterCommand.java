package org.apache.helix.api.command;

import java.util.Collections;
import java.util.HashSet;
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
  private HelixStateModelCommand stateModelDefinition;
  private final Set<String> alerts;
  private final Set<String> stats;
  private final Set<HelixResourceCommand> resources;
  private HelixClusterConstraintCommand constraints;
  private final Set<HelixParticipantCommand> participants;
  private ClusterId clusterId;

  protected HelixClusterCommand() {
    alerts = new HashSet<String>();
    stats = new HashSet<String>();
    resources = new HashSet<HelixResourceCommand>();
    participants = new HashSet<HelixParticipantCommand>();
  }

  /**
   * Registers a state model definition for the cluster, the statemodel
   * definition is applied to the cluster as well as resources added to the
   * cluster
   * @param stateModelDefinition
   */
  public void setStateModel(HelixStateModelCommand stateModelCommand) {
    this.stateModelDefinition = stateModelCommand;
  }

  public void setAlert(Set<String> alerts) {
    alerts.addAll(alerts);
  }

  public void addAlert(String alert) {
    alerts.add(alert);
  }

  public boolean removeAlert(String alert) {
    return alerts.remove(alert);
  }

  public Set<String> getAlerts() {
    return Collections.unmodifiableSet(this.alerts);
  }

  public void setStats(Set<String> stats) {
    this.stats.addAll(stats);
  }

  public boolean removeStat(String stat) {
    return stats.remove(stat);
  }

  public Set<String> getStats() {
    return Collections.unmodifiableSet(this.stats);
  }

  public void addResources(Set<HelixResourceCommand> resources) {
    this.resources.addAll(resources);
  }

  public boolean removeResource(HelixResourceCommand resource) {
    return this.resources.remove(resource);
  }

  public void addParticipants(Set<HelixParticipantCommand> participants) {
    this.participants.addAll(participants);
  }

  public void setClusterId(ClusterId id) {
    this.clusterId = id;
  }
  
  public void setClusterConstraints(HelixClusterConstraintCommand constraint){
    this.constraints = constraint;
  }
}
