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
import java.util.Set;

/**
 * Represents the state model for a given state machine. The state model
 * dictates the states and how transitions happen.
 * 
 * This class replaces the StateModelDefinition class.
 */
public interface StateModel {

  /**
   * Returns the transition between the from state to the to state
   * @param from the state the transition starts from
   * @param to the state the transition ends in
   * @return Transition
   */
  Transition getTransition(State from, State to);

  /**
   * Gets all states in the state model
   * @return Set<State> the states in the state model
   */
  Set<State> getStates();

  /**
   * Returns all transitions in the state model
   * @return Set<Transition> the transitions in the state model
   */
  Set<Transition> getTransitions();

  /**
   * Returns all the start states in the state model
   * @return Set<State> the start state
   */
  Set<State> getStartState();

  /**
   * Returns all the end states in the state model
   * @return Set<State> the start state
   */
  Set<State> getEndState();

  /**
   * Returns the next state for a given transition to end state
   * @return State the next state to transition from start state to get to end state
   */
  State getNextStateForTransition(State start, State end);

}
