package org.apache.helix.api.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;

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
 * Command allows creating partitions for a given resource
 */
public class HelixPartitionCommand {
  private final ResourceId resourceId;
  private final PartitionId partitionId;
  private final Map<String, Serializable> properties;
  private boolean enabled;

  /**
   * Creates a partition for a resource
   * @param resourceId the resource for which the partition is created
   * @param partitionId the partition 
   */
  public HelixPartitionCommand(ResourceId resourceId, PartitionId partitionId) {
    this.resourceId = resourceId;
    this.partitionId = partitionId;
    this.properties = new HashMap<String, Serializable>();
  }
  
  /**
   * Identifier for the partition
   * @return the partition id
   */
  public PartitionId getId(){
    return this.partitionId;
  }
  
  /**
   * The resource identifier
   * @return the resource id
   */
  public ResourceId getResourceId(){
    return this.resourceId;
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
  
  public void enable(){
    this.enabled = true;
  }
  
  public void disable(){
    this.enabled = false;
  }
}
