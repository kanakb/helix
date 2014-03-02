package org.apache.helix.api.model;

import java.util.Map;

import org.apache.helix.api.model.IClusterConstraints.ConstraintAttribute;

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
 * @author osgigeek
 *
 */
public interface IConstraintItem {

  /**
   * Check if this constraint follows these attributes. Note that it is possible that this
   * constraint could consist of attributes in addition to those that are specified.
   * @param attributes attributes to check
   * @return true if the constraint follows every attribute, false otherwise
   */
  public abstract boolean match(Map<ConstraintAttribute, String> attributes);

  /**
   * filter out attributes that are not specified by this constraint
   * @param attributes attributes to filter
   * @return attributes of this constraint that are in the provided attributes
   */
  public abstract Map<ConstraintAttribute, String> filter(
      Map<ConstraintAttribute, String> attributes);

  /**
   * Get the actual entities that the constraint operates on
   * @return the constraint value
   */
  public abstract String getConstraintValue();

  /**
   * Get all the attributes of the constraint
   * @return scope-value pairs of attributes
   */
  public abstract Map<ConstraintAttribute, String> getAttributes();

  /**
   * Get the value of a specific attribute in this cluster
   * @param attr the attribute to look up
   * @return the attribute value, or null if the attribute is not present
   */
  public abstract String getAttributeValue(ConstraintAttribute attr);

  public abstract String toString();

}
