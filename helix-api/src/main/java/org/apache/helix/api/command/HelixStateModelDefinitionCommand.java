package org.apache.helix.api.command;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.helix.api.id.StateModelDefinitionId;

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
 * Command to create a state model definition
 */
public class HelixStateModelDefinitionCommand {
  private final StateModelDefinitionId stateModelDefinitionId;
  private Set<String> states;
  private Map<String, Set<String>> transitions;
  private Map<String, HelixStateConstraintCommand> stateConstraints;
  private Map<String, HelixTransitionConstraintCommand> transitionConstraints;

  HelixStateModelDefinitionCommand(StateModelDefinitionId id) {
    this.stateModelDefinitionId = id;
  }

  /**
   * Retrieves the state model definition id
   * @return the state model definition id
   */
  public StateModelDefinitionId getId() {
    return this.stateModelDefinitionId;
  }

  /**
   * Adds states for the state model
   * @param states the states in the state model
   */
  public void addStates(Set<String> states) {
    this.states = states;
  }

  /**
   * Adds a state constraint for the state
   * @param state the state to constrain
   * @param stateConstraint the constrain command
   */
  public void addStateConstraint(String state, HelixStateConstraintCommand stateConstraint) {
    this.stateConstraints.put(state, stateConstraint);
  }

  /**
   * Adds transitions between states to the state model
   * @param fromState the begin state
   * @param toState the end state
   * @param transitionConstraint the constraint on the transition
   */
  public void addTransition(String fromState, String toState,
      HelixTransitionConstraintCommand transitionConstraint) {
    this.transitionConstraints.put(fromState + "_" + toState, transitionConstraint);
    Set<String> toStates = transitions.get(fromState);
    if (toStates == null) {
      toStates = new HashSet<String>();
    }
    toStates.add(toState);
    transitions.put(fromState, toStates);
  }

  /**
   * Adds transitions from a start state to a set of end states with constraints on each of them.
   * The
   * constraints are applied in to the transitions to the end state in the same order as defined
   * in the two sets. If a single constraint is passed in it is applied to all state transitions
   * @param fromState the begin state
   * @param toStates the end states
   * @param transitionConstraints the constraint
   */
  public void addTransition(String fromState, Set<String> toStates,
      Set<HelixTransitionConstraintCommand> transitionConstraints) {
    if (toStates != null && transitionConstraints != null) {
      HelixTransitionConstraintCommand constraintCommand = null;
      HelixTransitionConstraintCommand[] commands =
          new HelixTransitionConstraintCommand[transitionConstraints.size()];
      commands = transitionConstraints.toArray(commands);
      if (transitionConstraints.size() == 1) {
        constraintCommand = commands[0];
        for (String toState : toStates) {
          this.addTransition(fromState, toState, constraintCommand);
        }
      } else {
        String[] states = new String[toStates.size()];
        states = toStates.toArray(states);
        if (states.length != commands.length) {
          throw new IllegalArgumentException("States and constraints must match");
        }
        for (int i = 0; i < states.length; i++) {
          this.addTransition(fromState, states[i], commands[i]);
        }
      }
    }
  }
}
