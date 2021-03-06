package org.apache.helix.api.model.constraint;

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

import java.util.HashMap;
import java.util.Map;

import org.apache.helix.api.model.constraint.ClusterConstraints.ConstraintAttribute;
import org.apache.log4j.Logger;

/**
 * A single constraint and its associated attributes
 */
public class ConstraintItem {
  private static Logger LOG = Logger.getLogger(ConstraintItem.class);

  // attributes e.g. {STATE:MASTER, RESOURCE:TestDB, INSTANCE:localhost_12918}
  final Map<ConstraintAttribute, String> _attributes;
  final String _constraintValue;

  /**
   * Initialize a constraint with attributes
   * @param attributes the attributes that define the constraint, including the constraint value
   */
  public ConstraintItem(Map<String, String> attributes) {
    ConstraintItemBuilder builder = new ConstraintItemBuilder();
    builder.addConstraintAttributes(attributes);
    _attributes = builder.getAttributes();
    _constraintValue = builder.getConstraintValue();
  }

  /**
   * Initialize a constraint with {@link ConstraintAttribute}s and a custom constraint value
   * @param attributes constraint attribute scope-value pairs
   * @param constraintValue the specific entity(ies) affected by the constraint
   */
  public ConstraintItem(Map<ConstraintAttribute, String> attributes, String constraintValue) {
    _attributes = attributes;
    _constraintValue = constraintValue;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IConstrantItem#match(java.util.Map)
   */
  public boolean match(Map<ConstraintAttribute, String> attributes) {
    for (ConstraintAttribute key : _attributes.keySet()) {
      if (!attributes.containsKey(key)) {
        return false;
      }

      if (!attributes.get(key).matches(_attributes.get(key))) {
        return false;
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IConstrantItem#filter(java.util.Map)
   */
  public Map<ConstraintAttribute, String> filter(Map<ConstraintAttribute, String> attributes) {
    Map<ConstraintAttribute, String> ret = new HashMap<ConstraintAttribute, String>();
    for (ConstraintAttribute key : _attributes.keySet()) {
      // TODO: what if attributes.get(key)==null? might need match function at constrait level
      ret.put(key, attributes.get(key));
    }

    return ret;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IConstrantItem#getConstraintValue()
   */
  public String getConstraintValue() {
    return _constraintValue;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IConstrantItem#getAttributes()
   */
  public Map<ConstraintAttribute, String> getAttributes() {
    return _attributes;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IConstrantItem#getAttributeValue(org.apache.helix.api.model.
   * IClusterConstraints.ConstraintAttribute)
   */
  public String getAttributeValue(ConstraintAttribute attr) {
    return _attributes.get(attr);
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IConstrantItem#toString()
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(_attributes + ":" + _constraintValue);
    return sb.toString();
  }

}
