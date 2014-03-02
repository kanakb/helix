package org.apache.helix.api.model;

import java.util.List;

import org.apache.helix.api.config.State;
import org.apache.helix.api.id.StateModelDefId;

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
public interface IStateModelDefinition {

  /**
   * Get a concrete state model definition id
   * @return StateModelDefId
   */
  public abstract StateModelDefId getStateModelDefId();

  /**
   * Get an ordered priority list of transitions
   * @return transitions in the form SRC-DEST, the first of which is highest priority
   */
  public abstract List<String> getStateTransitionPriorityList();

  /**
   * Get an ordered priority list of transitions
   * @return Transition objects, the first of which is highest priority (immutable)
   */
  public abstract <T extends ITransition> List<T> getTypedStateTransitionPriorityList();

  /**
   * Get an ordered priority list of states
   * @return state names, the first of which is highest priority
   */
  public abstract List<String> getStatesPriorityList();

  /**
   * Get an ordered priority list of states
   * @return immutable list of states, the first of which is highest priority (immutable)
   */
  public abstract List<State> getTypedStatesPriorityList();

  /**
   * Get the intermediate state required to transition from one state to the other
   * @param fromState the source
   * @param toState the destination
   * @return the intermediate state
   */
  public abstract String getNextStateForTransition(String fromState, String toState);

  /**
   * Get the intermediate state required to transition from one state to the other
   * @param fromState the source
   * @param toState the destination
   * @return the intermediate state, or null if not present
   */
  public abstract State getNextStateForTransition(State fromState, State toState);

  /**
   * Get the starting state in the model
   * @return name of the initial state
   */
  public abstract String getInitialState();

  /**
   * Get the starting state in the model
   * @return name of the initial state
   */
  public abstract State getTypedInitialState();

  /**
   * Number of instances that can be in each state
   * @param state the state name
   * @return maximum instance count per state, can be "N" or "R"
   */
  public abstract String getNumInstancesPerState(String state);

  /**
   * Number of participants that can be in each state
   * @param state the state
   * @return maximum instance count per state, can be "N" or "R"
   */
  public abstract String getNumParticipantsPerState(State state);

  public abstract boolean isValid();

}
