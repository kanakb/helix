package org.apache.helix.api.id;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
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
public final class StateModelFactoryId extends Id {
  @JsonIgnore
  public static final String DEFAULT_STATE_MODEL_FACTORY = "DEFAULT";

  /**
   * Create a state model factory id
   * @param id string representing a state model factory
   */
  @JsonCreator
  private StateModelFactoryId(@JsonProperty("id") String id) {
    super(id);
  }

  /**
   * Get a concrete state model factory id
   * @param stateModelFactoryId the string version of the id
   * @return StateModelFactoryId
   */
  public static StateModelFactoryId from(String stateModelFactoryId) {
    if (stateModelFactoryId == null) {
      return null;
    }
    return new StateModelFactoryId(stateModelFactoryId);
  }
}
