package org.apache.helix.api.model.statemachine;

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
 * A transition handler annotation which annotates methods to indicate
 * what method is to be used for transitions from a given state to 
 * the to state
 */
public @interface TransitionHandler {

  /**
   * Sets the from state to start transition from
   * @return String the state to start transition
   */
  String fromState();

  /**
   * Sets the to state to end transition to
   * @return String the state to end transition into
   */
  String toState();
}
