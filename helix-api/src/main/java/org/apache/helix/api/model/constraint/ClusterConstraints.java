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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.helix.api.id.ConstraintId;
import org.apache.helix.api.model.HelixProperty;
import org.apache.helix.api.model.ZNRecord;
import org.apache.helix.api.model.ipc.Message;
import org.apache.helix.api.model.ipc.Message.MessageType;
import org.apache.helix.api.model.statemachine.Transition;
import org.apache.log4j.Logger;

/**
 * All of the constraints on a given cluster and its subcomponents, both physical and logical.
 */
public class ClusterConstraints extends HelixProperty {
  private static Logger LOG = Logger.getLogger(ClusterConstraints.class);

  public enum ConstraintAttribute {
    STATE,
    STATE_MODEL,
    MESSAGE_TYPE,
    TRANSITION,
    RESOURCE,
    PARTITION,
    INSTANCE,
    CONSTRAINT_VALUE
  }

  /**
   * Possible special values that constraint attributes can take
   */
  public enum ConstraintValue {
    ANY,
    N,
    R
  }

  /**
   * What is being constrained
   */
  public enum ConstraintType {
    STATE_CONSTRAINT,
    MESSAGE_CONSTRAINT
  }

  // constraint-id -> constraint-item
  private final Map<ConstraintId, ConstraintItem> _constraints =
      new HashMap<ConstraintId, ConstraintItem>();

  /**
   * Instantiate constraints as a given type
   * @param type {@link ConstraintType} representing what this constrains
   */
  public ClusterConstraints(ConstraintType type) {
    super(type.toString());
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#getType()
   */
  public ConstraintType getType() {
    return ConstraintType.valueOf(getId());
  }

  /**
   * Instantiate constraints from a pre-populated ZNRecord
   * @param record ZNRecord containing all constraints
   */
  public ClusterConstraints(ZNRecord record) {
    super(record);

    for (String constraintId : _record.getMapFields().keySet()) {
      ConstraintItemBuilder builder = new ConstraintItemBuilder();
      ConstraintItem item =
          builder.addConstraintAttributes(_record.getMapField(constraintId)).build();
      // ignore item with empty attributes or no constraint-value
      if (item.getAttributes().size() > 0 && item.getConstraintValue() != null) {
        addConstraintItem(ConstraintId.from(constraintId), item);
      } else {
        LOG.error("Skip invalid constraint. key: " + constraintId + ", value: "
            + _record.getMapField(constraintId));
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.model.IClusterConstraints#addConstraintItem(org.apache.helix.api.id.ConstraintId
   * , org.apache.helix.model.ConstraintItem)
   */
  public void addConstraintItem(ConstraintId constraintId, ConstraintItem item) {
    Map<String, String> map = new TreeMap<String, String>();
    for (ConstraintAttribute attr : item.getAttributes().keySet()) {
      map.put(attr.toString(), item.getAttributeValue(attr));
    }
    map.put(ConstraintAttribute.CONSTRAINT_VALUE.toString(), item.getConstraintValue());
    _record.setMapField(constraintId.toString(), map);
    _constraints.put(constraintId, item);
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#addConstraintItem(java.lang.String,
   * org.apache.helix.model.ConstraintItem)
   */
  public void addConstraintItem(String constraintId, ConstraintItem item) {
    addConstraintItem(ConstraintId.from(constraintId), item);
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#addConstraintItems(java.util.Map)
   */
  public <T extends ConstraintItem> void addConstraintItems(Map<String, T> items) {
    for (String constraintId : items.keySet()) {
      addConstraintItem(constraintId, items.get(constraintId));
    }
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#removeConstraintItem(org.apache.helix.api.id.
   * ConstraintId)
   */
  public void removeConstraintItem(ConstraintId constraintId) {
    _constraints.remove(constraintId);
    _record.getMapFields().remove(constraintId.toString());
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#removeConstraintItem(java.lang.String)
   */
  public void removeConstraintItem(String constraintId) {
    removeConstraintItem(ConstraintId.from(constraintId));
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.helix.model.IClusterConstraints#getConstraintItem(org.apache.helix.api.id.ConstraintId
   * )
   */
  public <T extends ConstraintItem> T getConstraintItem(ConstraintId constraintId) {
    return (T) _constraints.get(constraintId);
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#getConstraintItem(java.lang.String)
   */
  public <T extends ConstraintItem> T getConstraintItem(String constraintId) {
    return getConstraintItem(ConstraintId.from(constraintId));
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#match(java.util.Map)
   */
  public <T extends ConstraintItem> Set<T> match(Map<ConstraintAttribute, String> attributes) {
    Set<T> matches = new HashSet<T>();
    for (ConstraintItem item : _constraints.values()) {
      if (item.match(attributes)) {
        matches.add((T) item);
      }
    }
    return matches;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#getConstraintItems()
   */
  public <T extends ConstraintItem> Map<ConstraintId, T> getConstraintItems() {
    return (Map<ConstraintId, T>) _constraints;
  }

  /**
   * convert a message to constraint attribute pairs
   * @param msg a {@link Message} containing constraint attributes
   * @return constraint attribute scope-value pairs
   */
  public static Map<ConstraintAttribute, String> toConstraintAttributes(Message msg) {
    Map<ConstraintAttribute, String> attributes = new TreeMap<ConstraintAttribute, String>();
    String msgType = msg.getMsgType();
    attributes.put(ConstraintAttribute.MESSAGE_TYPE, msgType);
    if (MessageType.STATE_TRANSITION.toString().equals(msgType)) {
      if (msg.getTypedFromState() != null && msg.getTypedToState() != null) {
        attributes.put(ConstraintAttribute.TRANSITION,
            Transition.from(msg.getTypedFromState(), msg.getTypedToState()).toString());
      }
      if (msg.getResourceId() != null) {
        attributes.put(ConstraintAttribute.RESOURCE, msg.getResourceId().toString());
      }
      if (msg.getTgtName() != null) {
        attributes.put(ConstraintAttribute.INSTANCE, msg.getTgtName());
      }
      if (msg.getStateModelDefId() != null) {
        attributes.put(ConstraintAttribute.STATE_MODEL, msg.getStateModelDefId().toString());
      }
    }
    return attributes;
  }

  /*
   * (non-Javadoc)
   * @see org.apache.helix.model.IClusterConstraints#isValid()
   */
  public boolean isValid() {
    return true;
  }

}
