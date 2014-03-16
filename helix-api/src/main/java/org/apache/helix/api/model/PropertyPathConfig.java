package org.apache.helix.api.model;

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

import static org.apache.helix.api.model.PropertyType.ALERTS;
import static org.apache.helix.api.model.PropertyType.ALERT_STATUS;
import static org.apache.helix.api.model.PropertyType.CONFIGS;
import static org.apache.helix.api.model.PropertyType.CONTEXT;
import static org.apache.helix.api.model.PropertyType.CURRENTSTATES;
import static org.apache.helix.api.model.PropertyType.EXTERNALVIEW;
import static org.apache.helix.api.model.PropertyType.HEALTHREPORT;
import static org.apache.helix.api.model.PropertyType.HISTORY;
import static org.apache.helix.api.model.PropertyType.IDEALSTATES;
import static org.apache.helix.api.model.PropertyType.LIVEINSTANCES;
import static org.apache.helix.api.model.PropertyType.MESSAGES;
import static org.apache.helix.api.model.PropertyType.PAUSE;
import static org.apache.helix.api.model.PropertyType.RESOURCEASSIGNMENTS;
import static org.apache.helix.api.model.PropertyType.STATEMODELDEFS;
import static org.apache.helix.api.model.PropertyType.STATUSUPDATES;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Utility mapping properties to their Zookeeper locations
 */
public class PropertyPathConfig {
  private static Logger logger = Logger.getLogger(PropertyPathConfig.class);

  static final Map<PropertyType, Map<Integer, String>> templateMap =
      new HashMap<PropertyType, Map<Integer, String>>();
  static final Map<PropertyType, Class<? extends HelixProperty>> typeToClassMapping =
      new HashMap<PropertyType, Class<? extends HelixProperty>>();
  static final ClassLoader clazzLoader = PropertyPathConfig.class.getClassLoader();
  static {
    try {
      typeToClassMapping.put(LIVEINSTANCES, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.LiveInstance"));
      typeToClassMapping.put(IDEALSTATES, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.IdealState"));
      typeToClassMapping.put(CONFIGS, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.InstanceConfig"));
      typeToClassMapping.put(EXTERNALVIEW, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.ExternalView"));
      typeToClassMapping.put(STATEMODELDEFS, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.api.model.statemachine.StateModelDefinition"));
      typeToClassMapping.put(MESSAGES, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.api.model.ipc.Message"));
      typeToClassMapping.put(CURRENTSTATES, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.CurrentState"));
      typeToClassMapping.put(STATUSUPDATES, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.api.model.statemachine.StatusUpdate"));
      typeToClassMapping.put(HISTORY, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.LeaderHistory"));
      typeToClassMapping.put(HEALTHREPORT, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.HealthStat"));
      typeToClassMapping.put(ALERTS,
          (Class<? extends HelixProperty>) clazzLoader.loadClass("org.apache.helix.model.Alerts"));
      typeToClassMapping.put(ALERT_STATUS, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.AlertStatus"));
      typeToClassMapping.put(PAUSE, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.PauseSignal"));
      typeToClassMapping.put(RESOURCEASSIGNMENTS, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.model.ResourceAssignment"));
      typeToClassMapping.put(CONTEXT, (Class<? extends HelixProperty>) clazzLoader
          .loadClass("org.apache.helix.controller.context.ControllerContextHolder"));
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }

    // @formatter:off
    addEntry(PropertyType.CLUSTER, 1, "/{clusterName}");
    addEntry(PropertyType.CONFIGS, 1, "/{clusterName}/CONFIGS");
    addEntry(PropertyType.CONFIGS, 2, "/{clusterName}/CONFIGS/{scope}");
    addEntry(PropertyType.CONFIGS, 3, "/{clusterName}/CONFIGS/{scope}/{scopeKey}");
    addEntry(PropertyType.CONFIGS, 4, "/{clusterName}/CONFIGS/{scope}/{scopeKey}/{subScopeKey}");
    // addEntry(PropertyType.CONFIGS,2,"/{clusterName}/CONFIGS/{instanceName}");
    addEntry(PropertyType.LIVEINSTANCES, 1, "/{clusterName}/LIVEINSTANCES");
    addEntry(PropertyType.LIVEINSTANCES, 2, "/{clusterName}/LIVEINSTANCES/{instanceName}");
    addEntry(PropertyType.INSTANCES, 1, "/{clusterName}/INSTANCES");
    addEntry(PropertyType.INSTANCES, 2, "/{clusterName}/INSTANCES/{instanceName}");
    addEntry(PropertyType.IDEALSTATES, 1, "/{clusterName}/IDEALSTATES");
    addEntry(PropertyType.IDEALSTATES, 2, "/{clusterName}/IDEALSTATES/{resourceName}");
    addEntry(PropertyType.RESOURCEASSIGNMENTS, 1, "/{clusterName}/RESOURCEASSIGNMENTS");
    addEntry(PropertyType.RESOURCEASSIGNMENTS, 2,
        "/{clusterName}/RESOURCEASSIGNMENTS/{resourceName}");
    addEntry(PropertyType.EXTERNALVIEW, 1, "/{clusterName}/EXTERNALVIEW");
    addEntry(PropertyType.EXTERNALVIEW, 2, "/{clusterName}/EXTERNALVIEW/{resourceName}");
    addEntry(PropertyType.STATEMODELDEFS, 1, "/{clusterName}/STATEMODELDEFS");
    addEntry(PropertyType.STATEMODELDEFS, 2, "/{clusterName}/STATEMODELDEFS/{stateModelName}");
    addEntry(PropertyType.CONTROLLER, 1, "/{clusterName}/CONTROLLER");
    addEntry(PropertyType.PROPERTYSTORE, 1, "/{clusterName}/PROPERTYSTORE");

    // INSTANCE
    addEntry(PropertyType.MESSAGES, 2, "/{clusterName}/INSTANCES/{instanceName}/MESSAGES");
    addEntry(PropertyType.MESSAGES, 3, "/{clusterName}/INSTANCES/{instanceName}/MESSAGES/{msgId}");
    addEntry(PropertyType.CURRENTSTATES, 2, "/{clusterName}/INSTANCES/{instanceName}/CURRENTSTATES");
    addEntry(PropertyType.CURRENTSTATES, 3,
        "/{clusterName}/INSTANCES/{instanceName}/CURRENTSTATES/{sessionId}");
    addEntry(PropertyType.CURRENTSTATES, 4,
        "/{clusterName}/INSTANCES/{instanceName}/CURRENTSTATES/{sessionId}/{resourceName}");
    addEntry(PropertyType.CURRENTSTATES, 5,
        "/{clusterName}/INSTANCES/{instanceName}/CURRENTSTATES/{sessionId}/{resourceName}/{bucketName}");
    addEntry(PropertyType.STATUSUPDATES, 2, "/{clusterName}/INSTANCES/{instanceName}/STATUSUPDATES");
    addEntry(PropertyType.STATUSUPDATES, 3,
        "/{clusterName}/INSTANCES/{instanceName}/STATUSUPDATES/{sessionId}");
    addEntry(PropertyType.STATUSUPDATES, 4,
        "/{clusterName}/INSTANCES/{instanceName}/STATUSUPDATES/{sessionId}/{subPath}");
    addEntry(PropertyType.STATUSUPDATES, 5,
        "/{clusterName}/INSTANCES/{instanceName}/STATUSUPDATES/{sessionId}/{subPath}/{recordName}");
    addEntry(PropertyType.ERRORS, 2, "/{clusterName}/INSTANCES/{instanceName}/ERRORS");
    addEntry(PropertyType.ERRORS, 3, "/{clusterName}/INSTANCES/{instanceName}/ERRORS/{sessionId}");
    addEntry(PropertyType.ERRORS, 4,
        "/{clusterName}/INSTANCES/{instanceName}/ERRORS/{sessionId}/{subPath}");
    addEntry(PropertyType.ERRORS, 5,
        "/{clusterName}/INSTANCES/{instanceName}/ERRORS/{sessionId}/{subPath}/{recordName}");
    addEntry(PropertyType.HEALTHREPORT, 2, "/{clusterName}/INSTANCES/{instanceName}/HEALTHREPORT");
    addEntry(PropertyType.HEALTHREPORT, 3,
        "/{clusterName}/INSTANCES/{instanceName}/HEALTHREPORT/{reportName}");
    // CONTROLLER
    addEntry(PropertyType.MESSAGES_CONTROLLER, 1, "/{clusterName}/CONTROLLER/MESSAGES");
    addEntry(PropertyType.MESSAGES_CONTROLLER, 2, "/{clusterName}/CONTROLLER/MESSAGES/{msgId}");
    addEntry(PropertyType.ERRORS_CONTROLLER, 1, "/{clusterName}/CONTROLLER/ERRORS");
    addEntry(PropertyType.ERRORS_CONTROLLER, 2, "/{clusterName}/CONTROLLER/ERRORS/{errorId}");
    addEntry(PropertyType.STATUSUPDATES_CONTROLLER, 1, "/{clusterName}/CONTROLLER/STATUSUPDATES");
    addEntry(PropertyType.STATUSUPDATES_CONTROLLER, 2,
        "/{clusterName}/CONTROLLER/STATUSUPDATES/{subPath}");
    addEntry(PropertyType.STATUSUPDATES_CONTROLLER, 3,
        "/{clusterName}/CONTROLLER/STATUSUPDATES/{subPath}/{recordName}");
    addEntry(PropertyType.LEADER, 1, "/{clusterName}/CONTROLLER/LEADER");
    addEntry(PropertyType.HISTORY, 1, "/{clusterName}/CONTROLLER/HISTORY");
    addEntry(PropertyType.PAUSE, 1, "/{clusterName}/CONTROLLER/PAUSE");
    addEntry(PropertyType.PERSISTENTSTATS, 1, "/{clusterName}/CONTROLLER/PERSISTENTSTATS");
    addEntry(PropertyType.ALERTS, 1, "/{clusterName}/CONTROLLER/ALERTS");
    addEntry(PropertyType.ALERT_STATUS, 1, "/{clusterName}/CONTROLLER/ALERT_STATUS");
    addEntry(PropertyType.ALERT_HISTORY, 1, "/{clusterName}/CONTROLLER/ALERT_HISTORY");
    addEntry(PropertyType.CONTEXT, 1, "/{clusterName}/CONTROLLER/CONTEXT");
    addEntry(PropertyType.CONTEXT, 2, "/{clusterName}/CONTROLLER/CONTEXT/{contextId}");
    // @formatter:on

  }
  static Pattern pattern = Pattern.compile("(\\{.+?\\})");

  private static void addEntry(PropertyType type, int numKeys, String template) {
    if (!templateMap.containsKey(type)) {
      templateMap.put(type, new HashMap<Integer, String>());
    }
    logger.trace("Adding template for type:" + type.getType() + " arguments:" + numKeys
        + " template:" + template);
    templateMap.get(type).put(numKeys, template);
  }

  /**
   * Get the Zookeeper path given the property type, cluster, and parameters
   * @param type
   * @param clusterName
   * @param keys
   * @return a valid path, or null if none exists
   */
  public static String getPath(PropertyType type, String clusterName, String... keys) {
    if (clusterName == null) {
      logger.warn("ClusterName can't be null for type:" + type);
      return null;
    }
    if (keys == null) {
      keys = new String[] {};
    }
    String template = null;
    if (templateMap.containsKey(type)) {
      // keys.length+1 since we add clusterName
      template = templateMap.get(type).get(keys.length + 1);
    }

    String result = null;

    if (template != null) {
      result = template;
      Matcher matcher = pattern.matcher(template);
      int count = 0;
      while (matcher.find()) {
        count = count + 1;
        String var = matcher.group();
        if (count == 1) {
          result = result.replace(var, clusterName);
        } else {
          result = result.replace(var, keys[count - 2]);
        }
      }
    }
    if (result == null || result.indexOf('{') > -1 || result.indexOf('}') > -1) {
      logger.warn("Unable to instantiate template:" + template + " using clusterName:"
          + clusterName + " and keys:" + Arrays.toString(keys));
    }
    return result;
  }

  /**
   * Given a path, find the name of an instance at that path
   * @param path
   * @return a valid instance name, or null if none exists
   */
  public static String getInstanceNameFromPath(String path) {
    // path structure
    // /<cluster_name>/instances/<instance_name>/[currentStates/messages]
    if (path.contains("/" + PropertyType.INSTANCES + "/")) {
      String[] split = path.split("\\/");
      if (split.length > 3) {
        return split[3];
      }
    }
    return null;
  }
}
