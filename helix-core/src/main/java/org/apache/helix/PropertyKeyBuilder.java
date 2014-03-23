package org.apache.helix;

import static org.apache.helix.api.model.PropertyType.ALERTS;
import static org.apache.helix.api.model.PropertyType.ALERT_HISTORY;
import static org.apache.helix.api.model.PropertyType.ALERT_STATUS;
import static org.apache.helix.api.model.PropertyType.CLUSTER;
import static org.apache.helix.api.model.PropertyType.CONFIGS;
import static org.apache.helix.api.model.PropertyType.CONTEXT;
import static org.apache.helix.api.model.PropertyType.CONTROLLER;
import static org.apache.helix.api.model.PropertyType.CURRENTSTATES;
import static org.apache.helix.api.model.PropertyType.ERRORS;
import static org.apache.helix.api.model.PropertyType.ERRORS_CONTROLLER;
import static org.apache.helix.api.model.PropertyType.EXTERNALVIEW;
import static org.apache.helix.api.model.PropertyType.HEALTHREPORT;
import static org.apache.helix.api.model.PropertyType.HISTORY;
import static org.apache.helix.api.model.PropertyType.IDEALSTATES;
import static org.apache.helix.api.model.PropertyType.LEADER;
import static org.apache.helix.api.model.PropertyType.LIVEINSTANCES;
import static org.apache.helix.api.model.PropertyType.MESSAGES;
import static org.apache.helix.api.model.PropertyType.MESSAGES_CONTROLLER;
import static org.apache.helix.api.model.PropertyType.PAUSE;
import static org.apache.helix.api.model.PropertyType.PERSISTENTSTATS;
import static org.apache.helix.api.model.PropertyType.PROPERTYSTORE;
import static org.apache.helix.api.model.PropertyType.RESOURCEASSIGNMENTS;
import static org.apache.helix.api.model.PropertyType.STATEMODELDEFS;
import static org.apache.helix.api.model.PropertyType.STATUSUPDATES;
import static org.apache.helix.api.model.PropertyType.STATUSUPDATES_CONTROLLER;

import org.apache.helix.api.model.PropertyKey;
import org.apache.helix.api.model.PropertyType;
import org.apache.helix.api.model.HelixConfigScope.ConfigScopeProperty;
import org.apache.helix.controller.context.ControllerContextHolder;
import org.apache.helix.api.model.statemachine.StateModelDefinition;
import org.apache.helix.api.model.statemachine.StatusUpdate;
import org.apache.helix.api.model.configuration.ClusterConfiguration;
import org.apache.helix.api.model.configuration.PartitionConfiguration;
import org.apache.helix.api.model.configuration.ResourceConfiguration;
import org.apache.helix.api.model.constraint.ClusterConstraints;
import org.apache.helix.api.model.ipc.Message;
import org.apache.helix.model.AlertHistory;
import org.apache.helix.model.AlertStatus;
import org.apache.helix.model.Alerts;
import org.apache.helix.model.CurrentState;
import org.apache.helix.model.Error;
import org.apache.helix.model.HealthStat;
import org.apache.helix.model.IdealState;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.model.LeaderHistory;
import org.apache.helix.model.LiveInstance;
import org.apache.helix.model.PauseSignal;
import org.apache.helix.model.PersistentStats;
import org.apache.helix.model.ResourceAssignment;
import org.apache.helix.model.composite.ExternalView;

/**
 * PropertyKey builder for a cluster
 */
public class PropertyKeyBuilder {
  private final String _clusterName;

  /**
   * Instantiate with a cluster name
   * @param clusterName
   */
  public PropertyKeyBuilder(String clusterName) {
    _clusterName = clusterName;
  }

  /**
   * Get the key for the root node
   * @return cluster node
   */
  public PropertyKey cluster() {
    return new PropertyKey(CLUSTER, null, _clusterName);
  }

  /**
   * Get a property key associated with {@link IdealState}
   * @return {@link PropertyKey}
   */
  public PropertyKey idealStates() {
    return new PropertyKey(IDEALSTATES, IdealState.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link IdealState} and a resource
   * @param resourceName
   * @return {@link PropertyKey}
   */
  public PropertyKey idealStates(String resourceName) {
    return new PropertyKey(IDEALSTATES, IdealState.class, _clusterName, resourceName);
  }

  /**
   * Get a property key associated with all {@link ResourceAssignment}s on the cluster
   * @return {@link PropertyKey}
   */
  public PropertyKey resourceAssignments() {
    return new PropertyKey(RESOURCEASSIGNMENTS, ResourceAssignment.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link ResourceAssignment} representing the most recent
   * assignment
   * @param resourceName name of the resource
   * @return {@link PropertyKey}
   */
  public PropertyKey resourceAssignment(String resourceName) {
    return new PropertyKey(RESOURCEASSIGNMENTS, ResourceAssignment.class, _clusterName,
        resourceName);
  }

  /**
   * Get a property key associated with {@link StateModelDefinition}
   * @return {@link PropertyKey}
   */
  public PropertyKey stateModelDefs() {
    return new PropertyKey(STATEMODELDEFS, StateModelDefinition.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link StateModelDefinition} for a given state model name
   * @param stateModelName
   * @return {@link PropertyKey}
   */
  public PropertyKey stateModelDef(String stateModelName) {
    return new PropertyKey(STATEMODELDEFS, StateModelDefinition.class, _clusterName, stateModelName);
  }

  /**
   * Get a property key associated with all cluster configurations
   * @return {@link PropertyKey}
   */

  public PropertyKey clusterConfigs() {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.CLUSTER, ClusterConfiguration.class,
        _clusterName, ConfigScopeProperty.CLUSTER.toString());
  }

  /**
   * Get a property key associated with this cluster configuration
   * @return {@link PropertyKey}
   */
  public PropertyKey clusterConfig() {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.CLUSTER, ClusterConfiguration.class,
        _clusterName, ConfigScopeProperty.CLUSTER.toString(), _clusterName);
  }

  /**
   * Get a property key associated with {@link InstanceConfig}
   * @return {@link PropertyKey}
   */
  public PropertyKey instanceConfigs() {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.PARTICIPANT, InstanceConfig.class,
        _clusterName, ConfigScopeProperty.PARTICIPANT.toString());
  }

  /**
   * Get a property key associated with {@link InstanceConfig} for a specific instance
   * @param instanceName
   * @return {@link PropertyKey}
   */
  public PropertyKey instanceConfig(String instanceName) {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.PARTICIPANT, InstanceConfig.class,
        _clusterName, ConfigScopeProperty.PARTICIPANT.toString(), instanceName);
  }

  /**
   * Get a property key associated with resource configurations
   * @return {@link PropertyKey}
   */
  public PropertyKey resourceConfigs() {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.RESOURCE, ResourceConfiguration.class,
        _clusterName, ConfigScopeProperty.RESOURCE.toString());
  }

  /**
   * Get a property key associated with a specific resource configuration
   * @param resourceName
   * @return {@link PropertyKey}
   */
  public PropertyKey resourceConfig(String resourceName) {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.RESOURCE, ResourceConfiguration.class,
        _clusterName, ConfigScopeProperty.RESOURCE.toString(), resourceName);
  }

  /**
   * Get a property key associated with all partition configurations
   * @param resourceName
   * @return {@link PropertyKey}
   */
  public PropertyKey partitionConfigs(String resourceName) {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.RESOURCE, PartitionConfiguration.class,
        _clusterName, ConfigScopeProperty.RESOURCE.toString(), resourceName);
  }

  /**
   * Get a property key associated with a partition configuration
   * @param resourceName
   * @param partitionName
   * @return {@link PropertyKey}
   */
  public PropertyKey partitionConfig(String resourceName, String partitionName) {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.RESOURCE, PartitionConfiguration.class,
        _clusterName, ConfigScopeProperty.RESOURCE.toString(), resourceName, partitionName);
  }

  /**
   * Get a property key associated with a partition configuration
   * @param instanceName
   * @param resourceName
   * @param partitionName
   * @return {@link PropertyKey}
   */
  public PropertyKey partitionConfig(String instanceName, String resourceName, String partitionName) {
    return new PropertyKey(CONFIGS, ConfigScopeProperty.RESOURCE, PartitionConfiguration.class,
        _clusterName, ConfigScopeProperty.RESOURCE.toString(), resourceName, partitionName);
  }

  /**
   * Get a property key associated with {@link ClusterConstraints}
   * @return {@link PropertyKey}
   */
  public PropertyKey constraints() {
    return new PropertyKey(CONFIGS, ClusterConstraints.class, _clusterName,
        ConfigScopeProperty.CONSTRAINT.toString());
  }

  /**
   * Get a property key associated with a specific {@link ClusterConstraints}
   * @param constraintType
   * @return {@link PropertyKey}
   */

  public PropertyKey constraint(String constraintType) {
    return new PropertyKey(CONFIGS, ClusterConstraints.class, _clusterName,
        ConfigScopeProperty.CONSTRAINT.toString(), constraintType);
  }

  /**
   * Get a property key associated with {@link LiveInstance}
   * @return {@link PropertyKey}
   */
  public PropertyKey liveInstances() {
    return new PropertyKey(LIVEINSTANCES, LiveInstance.class, _clusterName);
  }

  /**
   * Get a property key associated with a specific {@link LiveInstance}
   * @param instanceName
   * @return {@link PropertyKey}
   */
  public PropertyKey liveInstance(String instanceName) {
    return new PropertyKey(LIVEINSTANCES, LiveInstance.class, _clusterName, instanceName);
  }

  /**
   * Get a property key associated with all instances
   * @return {@link PropertyKey}
   */
  public PropertyKey instances() {
    return new PropertyKey(PropertyType.INSTANCES, null, _clusterName);
  }

  /**
   * Get a property key associated with all instances
   * @return {@link PropertyKey}
   */
  public PropertyKey instance(String instanceName) {
    return new PropertyKey(PropertyType.INSTANCES, null, _clusterName, instanceName);
  }

  /**
   * Get a property key associated with {@link Message} for an instance
   * @param instanceName
   * @return {@link PropertyKey}
   */
  public PropertyKey messages(String instanceName) {
    return new PropertyKey(MESSAGES, Message.class, _clusterName, instanceName);
  }

  /**
   * Get a property key associated with a specific {@link Message} on an instance
   * @param instanceName
   * @param messageId
   * @return {@link PropertyKey}
   */
  public PropertyKey message(String instanceName, String messageId) {
    return new PropertyKey(MESSAGES, Message.class, _clusterName, instanceName, messageId);
  }

  /**
   * Get a property key associated with {@link CurrentState} of an instance
   * @param instanceName
   * @return {@link PropertyKey}
   */
  public PropertyKey sessions(String instanceName) {
    return new PropertyKey(CURRENTSTATES, CurrentState.class, _clusterName, instanceName);
  }

  /**
   * Get a property key associated with {@link CurrentState} of an instance and session
   * @param instanceName
   * @param sessionId
   * @return {@link PropertyKey}
   */
  public PropertyKey currentStates(String instanceName) {
    return new PropertyKey(CURRENTSTATES, CurrentState.class, _clusterName, instanceName);
  }

  /**
   * Get a property key associated with {@link CurrentState} of an instance and session
   * @param instanceName
   * @param sessionId
   * @return {@link PropertyKey}
   */
  public PropertyKey currentStates(String instanceName, String sessionId) {
    return new PropertyKey(CURRENTSTATES, CurrentState.class, _clusterName, instanceName, sessionId);
  }

  /**
   * Get a property key associated with {@link CurrentState} of an instance, session, and
   * resource
   * @param instanceName
   * @param sessionId
   * @param resourceName
   * @return {@link PropertyKey}
   */
  public PropertyKey currentState(String instanceName, String sessionId, String resourceName) {
    return new PropertyKey(CURRENTSTATES, CurrentState.class, _clusterName, instanceName,
        sessionId, resourceName);
  }

  /**
   * Get a property key associated with {@link CurrentState} of an instance, session, resource,
   * and bucket name
   * @param instanceName
   * @param sessionId
   * @param resourceName
   * @param bucketName
   * @return {@link PropertyKey}
   */
  public PropertyKey currentState(String instanceName, String sessionId, String resourceName,
      String bucketName) {
    if (bucketName == null) {
      return new PropertyKey(CURRENTSTATES, CurrentState.class, _clusterName, instanceName,
          sessionId, resourceName);

    } else {
      return new PropertyKey(CURRENTSTATES, CurrentState.class, _clusterName, instanceName,
          sessionId, resourceName, bucketName);
    }
  }

  /**
   * Get a property key representing the root of all status updates
   * @param instanceName the participant the status updates belong to
   * @return {@link PropertyKey}
   */
  public PropertyKey statusUpdates(String instanceName) {
    return new PropertyKey(STATUSUPDATES, null, _clusterName, instanceName);
  }

  /**
   * Get a property key associated with {@link StatusUpdate} of an instance, session, resource,
   * and partition
   * @param instanceName
   * @param sessionId
   * @param resourceName
   * @param partitionName
   * @return {@link PropertyKey}
   */
  public PropertyKey stateTransitionStatus(String instanceName, String sessionId,
      String resourceName, String partitionName) {
    return new PropertyKey(STATUSUPDATES, StatusUpdate.class, _clusterName, instanceName,
        sessionId, resourceName, partitionName);
  }

  /**
   * Get a property key associated with {@link StatusUpdate} of an instance, session, and
   * resource
   * @param instanceName
   * @param sessionId
   * @param resourceName
   * @return {@link PropertyKey}
   */
  public PropertyKey stateTransitionStatus(String instanceName, String sessionId,
      String resourceName) {
    return new PropertyKey(STATUSUPDATES, StatusUpdate.class, _clusterName, instanceName,
        sessionId, resourceName);
  }

  /**
   * Get a property key associated with {@link StatusUpdate} of an instance and session
   * @param instanceName
   * @param sessionId
   * @return {@link PropertyKey}
   */
  public PropertyKey stateTransitionStatus(String instanceName, String sessionId) {
    return new PropertyKey(STATUSUPDATES, StatusUpdate.class, _clusterName, instanceName, sessionId);
  }

  /**
   * Used to get status update for a NON STATE TRANSITION type
   * @param instanceName
   * @param sessionId
   * @param msgType
   * @param msgId
   * @return {@link PropertyKey}
   */
  public PropertyKey taskStatus(String instanceName, String sessionId, String msgType, String msgId) {
    return new PropertyKey(STATUSUPDATES, StatusUpdate.class, _clusterName, instanceName,
        sessionId, msgType, msgId);
  }

  /**
   * Get a property key representing the root of all persisted participant errors
   * @param instanceName the participant of interest
   * @return {@link PropertyKey}
   */
  public PropertyKey participantErrors(String instanceName) {
    return new PropertyKey(ERRORS, null, _clusterName, instanceName);
  }

  /**
   * Get a property key associated with {@link Error} of an instance, session, resource,
   * and partition
   * @param instanceName
   * @param sessionId
   * @param resourceName
   * @param partitionName
   * @return {@link PropertyKey}
   */
  public PropertyKey stateTransitionError(String instanceName, String sessionId,
      String resourceName, String partitionName) {
    return new PropertyKey(ERRORS, Error.class, _clusterName, instanceName, sessionId,
        resourceName, partitionName);
  }

  /**
   * Get a property key associated with {@link Error} of an instance, session, and
   * resource
   * @param instanceName
   * @param sessionId
   * @param resourceName
   * @return {@link PropertyKey}
   */
  public PropertyKey stateTransitionErrors(String instanceName, String sessionId,
      String resourceName) {
    return new PropertyKey(ERRORS, Error.class, _clusterName, instanceName, sessionId, resourceName);
  }

  /**
   * Used to get status update for a NON STATE TRANSITION type
   * @param instanceName
   * @param sessionId
   * @param msgType
   * @param msgId
   * @return {@link PropertyKey}
   */
  public PropertyKey taskError(String instanceName, String sessionId, String msgType, String msgId) {
    return new PropertyKey(ERRORS, null, _clusterName, instanceName, sessionId, msgType, msgId);
  }

  /**
   * Get a property key associated with all {@link ExternalView}
   * @return {@link PropertyKey}
   */
  public PropertyKey externalViews() {
    return new PropertyKey(EXTERNALVIEW, ExternalView.class, _clusterName);
  }

  /**
   * Get a property key associated with an {@link ExternalView} of a resource
   * @param resourceName
   * @return {@link PropertyKey}
   */
  public PropertyKey externalView(String resourceName) {
    return new PropertyKey(EXTERNALVIEW, ExternalView.class, _clusterName, resourceName);
  }

  /**
   * Get a property key associated with a controller
   * @return {@link PropertyKey}
   */
  public PropertyKey controller() {
    return new PropertyKey(CONTROLLER, null, _clusterName);
  }

  /**
   * Get a property key associated with {@link StatusUpdate} of controller errors
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerTaskErrors() {
    return new PropertyKey(ERRORS_CONTROLLER, StatusUpdate.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link StatusUpdate} of a controller error
   * @param errorId
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerTaskError(String errorId) {
    return new PropertyKey(ERRORS_CONTROLLER, Error.class, _clusterName, errorId);
  }

  /**
   * Get the root of all controller status updates
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerTaskStatuses() {
    return new PropertyKey(STATUSUPDATES_CONTROLLER, StatusUpdate.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link StatusUpdate} of controller status updates
   * @param subPath
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerTaskStatuses(String subPath) {
    return new PropertyKey(STATUSUPDATES_CONTROLLER, StatusUpdate.class, _clusterName, subPath);
  }

  /**
   * Get a property key associated with {@link StatusUpdate} of a controller status update
   * @param subPath
   * @param recordName
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerTaskStatus(String subPath, String recordName) {
    return new PropertyKey(STATUSUPDATES_CONTROLLER, StatusUpdate.class, _clusterName, subPath,
        recordName);
  }

  /**
   * Get a property key associated with all {@link Message}s for the controller
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerMessages() {
    return new PropertyKey(MESSAGES_CONTROLLER, Message.class, _clusterName);
  }

  /**
   * Get a property key associated with a {@link Message} for the controller
   * @param msgId
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerMessage(String msgId) {
    return new PropertyKey(MESSAGES_CONTROLLER, Message.class, _clusterName, msgId);
  }

  /**
   * Get a property key associated with {@link LeaderHistory}
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerLeaderHistory() {
    return new PropertyKey(HISTORY, LeaderHistory.class, _clusterName);
  }

  /**
   * Get a property key associated with a {@link LiveInstance} leader
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerLeader() {
    return new PropertyKey(LEADER, LiveInstance.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link PauseSignal}
   * @return {@link PropertyKey}
   */
  public PropertyKey pause() {
    return new PropertyKey(PAUSE, PauseSignal.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link PersistentStats}
   * @return {@link PropertyKey}
   */
  public PropertyKey persistantStat() {
    return new PropertyKey(PERSISTENTSTATS, PersistentStats.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link Alerts}
   * @return {@link PropertyKey}
   */
  public PropertyKey alerts() {
    return new PropertyKey(ALERTS, Alerts.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link AlertStatus}
   * @return {@link PropertyKey}
   */
  public PropertyKey alertStatus() {
    return new PropertyKey(ALERT_STATUS, AlertStatus.class, _clusterName);
  }

  /**
   * Get a property key associated with {@link AlertHistory}
   * @return {@link PropertyKey}
   */
  public PropertyKey alertHistory() {
    return new PropertyKey(ALERT_HISTORY, AlertHistory.class, _clusterName);
  }

  /**
   * Get a property key associated with a {@link HealthStat} for an instance
   * @param instanceName
   * @param id identifies the statistics
   * @return {@link PropertyKey}
   */
  public PropertyKey healthReport(String instanceName, String id) {
    return new PropertyKey(HEALTHREPORT, HealthStat.class, _clusterName, instanceName, id);
  }

  /**
   * Get a property key associated with {@link HealthStat}s for an instance
   * @param instanceName
   * @return {@link PropertyKey}
   */
  public PropertyKey healthReports(String instanceName) {
    return new PropertyKey(HEALTHREPORT, HealthStat.class, _clusterName, instanceName);
  }

  /**
   * Get a propertykey associated with the root of the Helix property store
   * @return {@link PropertyStore}
   */
  public PropertyKey propertyStore() {
    return new PropertyKey(PROPERTYSTORE, null, _clusterName);
  }

  /**
   * Get a propertykey associated with the root of the Helix contexts
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerContexts() {
    return new PropertyKey(CONTEXT, ControllerContextHolder.class, _clusterName);
  }

  /**
   * Get a propertykey associated with the root of the Helix contexts
   * @return {@link PropertyKey}
   */
  public PropertyKey controllerContext(String contextId) {
    return new PropertyKey(CONTEXT, ControllerContextHolder.class, _clusterName, contextId);
  }

  /**
   * Get the {@link PropertyKey} for this message
   * @param keyBuilder PropertyKey Builder
   * @param instanceName target instance
   * @return message PropertyKey
   */
  public PropertyKey getKey(String id, boolean isControllerMsg, String instanceName) {
    if (isControllerMsg) {
      return controllerMessage(id);
    } else {
      return message(instanceName, id);
    }
  }
}
