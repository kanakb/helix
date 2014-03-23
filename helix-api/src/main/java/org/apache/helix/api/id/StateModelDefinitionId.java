package org.apache.helix.api.id;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

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
public final class StateModelDefinitionId extends Id {
  public static final StateModelDefinitionId SCHEDULER_TASK_QUEUE = StateModelDefinitionId
      .from("SchedulerTaskQueue");
  /**
   * Create a state model definition id
   * @param id string representing a state model definition id
   */
  @JsonCreator
  private StateModelDefinitionId(@JsonProperty("id") String id) {
    super(id);
  }

  /**
   * Check if the underlying state model definition id is equal if case is ignored
   * @param that the StateModelDefId to compare
   * @return true if equal ignoring case, false otherwise
   */
  public boolean equalsIgnoreCase(StateModelDefinitionId that) {
    return this.toString().equalsIgnoreCase(that.toString());
  }

  /**
   * Get a concrete state model definition id
   * @param stateModelDefId string state model identifier
   * @return StateModelDefId
   */
  public static StateModelDefinitionId from(String stateModelDefId) {
    if (stateModelDefId == null) {
      return null;
    }
    return new StateModelDefinitionId(stateModelDefId);
  }
}
