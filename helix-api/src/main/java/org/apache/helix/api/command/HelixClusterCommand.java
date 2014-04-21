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
  private final Set<HelixParticipantCommand> participants;
  private ClusterId clusterId;
  private boolean autoStart;
  private boolean allowAutoJoin;
  private Set<HelixConstraintCommand> constraints;

  /**
   * Creates a cluster mutation command for a cluster identified by its id
   * @param clusterId the cluster id
   */
  public HelixClusterCommand(ClusterId clusterId) {
    this.clusterId = clusterId;
    alerts = new HashSet<String>();
    stats = new HashSet<String>();
    resources = new HashSet<HelixResourceCommand>();
    participants = new HashSet<HelixParticipantCommand>();
    constraints = new HashSet<HelixConstraintCommand>();
  }

  /**
   * Registers a state model definition for the cluster, the statemodel
   * definition is applied to the cluster as well as resources added to the
   * cluster
   * @param stateModelCommand a command which identifies the state model to
   *          create for the cluster
   */
  public void setStateModel(HelixStateModelCommand stateModelCommand) {
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
   * Sets all alerts on the cluster
   * @param alerts
   */
  public void setAlert(Set<String> alerts) {
    alerts.addAll(alerts);
  }

  /**
   * Returns the cluster id for this cluster
   * @return ClusterId
   */
  public ClusterId getClusterId() {
    return this.clusterId;
  }

  /**
   * Adds an alert to the set of alerts
   * @param alert
   */
  public void addAlert(String alert) {
    alerts.add(alert);
  }

  /**
   * Removes an alert from the set of alerts
   * @param alert
   * @return <b>True</b> if the alert was removed, <b>False</b> if the alert
   *         could not be removed
   */
  public boolean removeAlert(String alert) {
    return alerts.remove(alert);
  }

  /**
   * Returns a copy of all the alerts
   * @return Set<String> returns all the alerts configured for the cluster
   */
  public Set<String> getAlerts() {
    return Collections.unmodifiableSet(this.alerts);
  }

  /**
   * Sets the stats on the command
   * @param stats
   */
  public void setStats(Set<String> stats) {
    this.stats.addAll(stats);
  }

  /**
   * Removes a stat from the set of stats
   * @param stat the stat to remove
   * @return <b>True</b>if the stat was removed, <b>False</b>if removal failed
   */
  public boolean removeStat(String stat) {
    return stats.remove(stat);
  }

  /**
   * Retrieves a copy of all stats
   * @return Set<String> the stats configured for the cluster
   */
  public Set<String> getStats() {
    return Collections.unmodifiableSet(this.stats);
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
   * Checks if the cluster is configured to be autostart
   * @return <b>True</b> if the cluster is auto-started, <b>False</b> if not
   */
  public boolean isAutoStart() {
    return autoStart;
  }

  /**
   * Sets the auto-start configuration for the cluster
   * @param autoStart <b>True</b> if the cluster should be autostarted,
   * <b>False</b> if it is not to be autostarted
   */
  public void setAutoStart(boolean autoStart) {
    this.autoStart = autoStart;
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
}
