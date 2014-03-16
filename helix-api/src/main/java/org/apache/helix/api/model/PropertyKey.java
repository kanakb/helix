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

import java.util.Arrays;

import org.apache.helix.api.model.HelixConfigScope.ConfigScopeProperty;
import org.apache.log4j.Logger;

/**
 * Key allowing for type-safe lookups of and conversions to {@link HelixProperty} objects.
 */
public class PropertyKey {
  private static Logger LOG = Logger.getLogger(PropertyKey.class);
  public PropertyType _type;
  private final String[] _params;
  Class<? extends HelixProperty> _typeClazz;

  // if type is CONFIGS, set configScope; otherwise null
  ConfigScopeProperty _configScope;

  /**
   * Instantiate with a type, associated class, and parameters
   * @param type
   * @param typeClazz
   * @param params parameters associated with the key, the first of which is the cluster name
   */
  public PropertyKey(PropertyType type, Class<? extends HelixProperty> typeClazz, String... params) {
    this(type, null, typeClazz, params);
  }

  /**
   * Instantiate with a type, scope, associated class, and parameters
   * @param type
   * @param configScope
   * @param typeClazz
   * @param params parameters associated with the key, the first of which is the cluster name
   */
  public PropertyKey(PropertyType type, ConfigScopeProperty configScope,
      Class<? extends HelixProperty> typeClazz, String... params) {
    _type = type;
    if (params == null || params.length == 0 || Arrays.asList(params).contains(null)) {
      throw new IllegalArgumentException("params cannot be null");
    }

    _params = params;
    _typeClazz = typeClazz;

    _configScope = configScope;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return getPath();
  }

  /**
   * Get the path associated with this property
   * @return absolute path to the property
   */
  public String getPath() {
    String clusterName = _params[0];
    String[] subKeys = Arrays.copyOfRange(_params, 1, _params.length);
    String path = PropertyPathConfig.getPath(_type, clusterName, subKeys);
    if (path == null) {
      LOG.error("Invalid property key with type:" + _type + "subKeys:" + Arrays.toString(_params));
    }
    return path;
  }

  /**
   * Get the associated property type
   * @return {@link PropertyType}
   */
  public PropertyType getType() {
    return _type;
  }

  /**
   * Get parameters associated with the key
   * @return the parameters in the same order they were provided
   */
  public String[] getParams() {
    return _params;
  }

  /**
   * Get the associated class of this property
   * @return subclass of {@link HelixProperty}
   */
  public Class<? extends HelixProperty> getTypeClass() {
    return _typeClazz;
  }

  /**
   * Get the scope of this property
   * @return {@link ConfigScopeProperty}
   */
  public ConfigScopeProperty getConfigScope() {
    return _configScope;
  }

}
