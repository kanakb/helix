package org.apache.helix.core;

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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.helix.api.id.StateModelDefinitionId;
import org.apache.helix.api.model.HelixProperty;
import org.apache.helix.api.model.ZNRecord;
import org.apache.helix.api.model.statemachine.State;
import org.apache.helix.api.model.statemachine.Transition;

import com.google.common.collect.ImmutableList;

/**
 * Describe the state model
 */
public class StateModelDefinition extends HelixProperty {
  public enum StateModelDefinitionProperty {
    INITIAL_STATE,
    STATE_TRANSITION_PRIORITYLIST,
    STATE_PRIORITY_LIST
  }

  /**
   * state model's initial state
   */
  private final String _initialState;

  /**
   * State Names in priority order. Indicates the order in which states are
   * fulfilled
   */
  private final List<String> _statesPriorityList;

  /**
   * Specifies the number of instances for a given state <br>
   * -1 don't care, don't try to keep any resource in this state on any instance <br>
   * >0 any integer number greater than 0 specifies the number of instances
   * needed to be in this state <br>
   * R all instances in the preference list can be in this state <br>
   * N all instances in the cluster will be put in this state.PreferenceList
   * must be denoted as '*'
   */
  private final Map<String, String> _statesCountMap;

  private final List<String> _stateTransitionPriorityList;

  /**
   * StateTransition which is used to find the nextState given StartState and
   * FinalState
   */
  private final Map<String, Map<String, String>> _stateTransitionTable;

  /**
   * Instantiate from a pre-populated record
   * @param record ZNRecord representing a state model definition
   * @deprecated Creating state model definition from a ZNRecord
   *             will be removed from API, the code will be moved to SPI
   */
  @Deprecated
  public StateModelDefinition(ZNRecord record) {
    super(record);

    _initialState = record.getSimpleField(StateModelDefinitionProperty.INITIAL_STATE.toString());

    if (_initialState == null) {
      throw new IllegalArgumentException("initial-state for " + record.getId() + " is null");
    }

    _statesPriorityList =
        record.getListField(StateModelDefinitionProperty.STATE_PRIORITY_LIST.toString());
    _stateTransitionPriorityList =
        record.getListField(StateModelDefinitionProperty.STATE_TRANSITION_PRIORITYLIST.toString());
    _stateTransitionTable = new HashMap<String, Map<String, String>>();
    _statesCountMap = new HashMap<String, String>();
    if (_statesPriorityList != null) {
      for (String state : _statesPriorityList) {
        Map<String, String> metaData = record.getMapField(state + ".meta");
        if (metaData != null) {
          if (metaData.get("count") != null) {
            _statesCountMap.put(state, metaData.get("count"));
          }
        }
        Map<String, String> nextData = record.getMapField(state + ".next");
        _stateTransitionTable.put(state, nextData);
      }
    }

    // add transitions for helix-defined states
    for (HelixDefinedState state : HelixDefinedState.values()) {
      if (!_statesPriorityList.contains(state.toString())) {
        _statesCountMap.put(state.toString(), "-1");
      }
    }

    addDefaultTransition(HelixDefinedState.ERROR.toString(), HelixDefinedState.DROPPED.toString(),
        HelixDefinedState.DROPPED.toString());
    addDefaultTransition(HelixDefinedState.ERROR.toString(), _initialState, _initialState);
    addDefaultTransition(_initialState, HelixDefinedState.DROPPED.toString(),
        HelixDefinedState.DROPPED.toString());
  }

  /**
   * add transitions involving helix-defines states
   * these transitions need not to be specified in state-model-definition
   * @param from source state
   * @param to destination state
   * @param next intermediate state to reach the destination
   */
  void addDefaultTransition(String from, String to, String next) {
    if (!_stateTransitionTable.containsKey(from)) {
      _stateTransitionTable.put(from, new TreeMap<String, String>());
    }

    if (!_stateTransitionTable.get(from).containsKey(to)) {
      _stateTransitionTable.get(from).put(to, next);
    }
  }

  /**
   * Get a concrete state model definition id
   * @return StateModelDefId
   */
  public StateModelDefinitionId getStateModelDefId() {
    return StateModelDefinitionId.from(getId());
  }

  /**
   * Get an ordered priority list of transitions
   * @return transitions in the form SRC-DEST, the first of which is highest priority
   */
  public List<String> getStateTransitionPriorityList() {
    return _stateTransitionPriorityList;
  }

  /**
   * Get an ordered priority list of transitions
   * @return Transition objects, the first of which is highest priority (immutable)
   */
  public List<Transition> getTypedStateTransitionPriorityList() {
    ImmutableList.Builder<Transition> builder = new ImmutableList.Builder<Transition>();
    for (String transition : getStateTransitionPriorityList()) {
      String fromState = transition.substring(0, transition.indexOf('-'));
      String toState = transition.substring(transition.indexOf('-') + 1);
      builder.add(Transition.from(State.from(fromState), State.from(toState)));
    }
    return builder.build();
  }

  /**
   * Get an ordered priority list of states
   * @return state names, the first of which is highest priority
   */
  public List<String> getStatesPriorityList() {
    return _statesPriorityList;
  }

  /**
   * Get an ordered priority list of states
   * @return immutable list of states, the first of which is highest priority (immutable)
   */
  public List<State> getTypedStatesPriorityList() {
    ImmutableList.Builder<State> builder = new ImmutableList.Builder<State>();
    for (String state : getStatesPriorityList()) {
      builder.add(State.from(state));
    }
    return builder.build();
  }

  /**
   * Get the intermediate state required to transition from one state to the other
   * @param fromState the source
   * @param toState the destination
   * @return the intermediate state
   */
  public String getNextStateForTransition(String fromState, String toState) {
    Map<String, String> map = _stateTransitionTable.get(fromState);
    if (map != null) {
      return map.get(toState);
    }
    return null;
  }

  /**
   * Get the intermediate state required to transition from one state to the other
   * @param fromState the source
   * @param toState the destination
   * @return the intermediate state, or null if not present
   */
  public State getNextStateForTransition(State fromState, State toState) {
    String next = getNextStateForTransition(fromState.toString(), toState.toString());
    if (next != null) {
      return State.from(getNextStateForTransition(fromState.toString(), toState.toString()));
    }
    return null;
  }

  /**
   * Get the starting state in the model
   * @return name of the initial state
   */
  public String getInitialState() {
    // return _record.getSimpleField(StateModelDefinitionProperty.INITIAL_STATE
    // .toString());
    return _initialState;
  }

  /**
   * Get the starting state in the model
   * @return name of the initial state
   */
  public State getTypedInitialState() {
    // return _record.getSimpleField(StateModelDefinitionProperty.INITIAL_STATE
    // .toString());
    return State.from(_initialState);
  }

  /**
   * Number of instances that can be in each state
   * @param state the state name
   * @return maximum instance count per state, can be "N" or "R"
   */
  public String getNumInstancesPerState(String state) {
    return _statesCountMap.get(state);
  }

  /**
   * Number of participants that can be in each state
   * @param state the state
   * @return maximum instance count per state, can be "N" or "R"
   */
  public String getNumParticipantsPerState(State state) {
    return _statesCountMap.get(state.toString());
  }

  public boolean isValid() {
    return StateModelDefinitionValidator.isStateModelDefinitionValid(this);
  }

}
