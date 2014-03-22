package org.apache.helix.model.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.helix.api.ZNRecord;
import org.apache.helix.api.model.statemachine.State;
import org.apache.helix.api.model.statemachine.StateModelDefinition;
import org.apache.helix.api.model.statemachine.Transition;
import org.apache.helix.api.model.statemachine.StateModelDefinition.StateModelDefinitionProperty;
import org.apache.helix.api.model.statemachine.id.StateModelDefId;

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

public class StateModelDefinitionBuilder {
  // TODO move this to model.builder package, refactor StateModelConfigGenerator to use this
  /**
   * Construct a state model
   */
  private final String _statemodelName;
  private String initialState;
  Map<String, Integer> statesMap;
  Map<Transition, Integer> transitionMap;
  Map<String, String> stateConstraintMap;

  /**
   * Start building a state model with a id
   * @param stateModelDefId state model id
   */
  public StateModelDefinitionBuilder(StateModelDefId stateModelDefId) {
    this._statemodelName = stateModelDefId.stringify();
    statesMap = new HashMap<String, Integer>();
    transitionMap = new HashMap<Transition, Integer>();
    stateConstraintMap = new HashMap<String, String>();
  }

  /**
   * Start building a state model with a name
   * @param stateModelDefId state model name
   */
  public StateModelDefinitionBuilder(String stateModelName) {
    this(StateModelDefId.from(stateModelName));
  }

  /**
   * initial state of a replica when it starts, most commonly used initial
   * state is OFFLINE
   * @param initialState
   */
  public StateModelDefinitionBuilder initialState(State initialState) {
    return initialState(initialState.toString());
  }

  /**
   * initial state of a replica when it starts, most commonly used initial
   * state is OFFLINE
   * @param initialState
   */
  public StateModelDefinitionBuilder initialState(String initialState) {
    this.initialState = initialState;
    return this;
  }

  /**
   * Define all valid states using this method. Set the priority in which the
   * constraints must be satisfied. Lets say STATE1 has a constraint of 1 and
   * STATE2 has a constraint of 3 but only one node is up then Helix will uses
   * the priority to see STATE constraint has to be given higher preference <br/>
   * Use -1 to indicates states with no constraints, like OFFLINE
   * @param state the state to add
   * @param priority the state priority, lower number is higher priority
   */
  public StateModelDefinitionBuilder addState(State state, int priority) {
    return addState(state.toString(), priority);
  }

  /**
   * Define all valid states using this method. Set the priority in which the
   * constraints must be satisfied. Lets say STATE1 has a constraint of 1 and
   * STATE2 has a constraint of 3 but only one node is up then Helix will uses
   * the priority to see STATE constraint has to be given higher preference <br/>
   * Use -1 to indicates states with no constraints, like OFFLINE
   * @param state the state to add
   * @param priority the state priority, lower number is higher priority
   */
  public StateModelDefinitionBuilder addState(String state, int priority) {
    statesMap.put(state, priority);
    return this;
  }

  /**
   * Sets the priority to Integer.MAX_VALUE
   * @param state
   */
  public StateModelDefinitionBuilder addState(State state) {
    addState(state, Integer.MAX_VALUE);
    return this;
  }

  /**
   * Sets the priority to Integer.MAX_VALUE
   * @param state
   */
  public StateModelDefinitionBuilder addState(String state) {
    addState(state, Integer.MAX_VALUE);
    return this;
  }

  /**
   * Define all legal transitions between states using this method. Priority
   * is used to order the transitions. Helix tries to maximize the number of
   * transitions that can be fired in parallel without violating the
   * constraint. The transitions are first sorted based on priority and
   * transitions are selected in a greedy way until the constriants are not
   * violated.
   * @param fromState source
   * @param toState destination
   * @param priority priority, higher value is higher priority
   * @return Builder
   */
  public StateModelDefinitionBuilder addTransition(State fromState, State toState, int priority) {
    transitionMap.put(new Transition(fromState, toState), priority);
    return this;
  }

  /**
   * Define all legal transitions between states using this method. Priority
   * is used to order the transitions. Helix tries to maximize the number of
   * transitions that can be fired in parallel without violating the
   * constraint. The transitions are first sorted based on priority and
   * transitions are selected in a greedy way until the constriants are not
   * violated.
   * @param fromState source
   * @param toState destination
   * @param priority priority, higher value is higher priority
   * @return Builder
   */
  public StateModelDefinitionBuilder addTransition(String fromState, String toState, int priority) {
    transitionMap.put(new Transition(fromState, toState), priority);
    return this;
  }

  /**
   * Add a state transition with maximal priority value
   * @see #addTransition(String, String, int)
   * @param fromState
   * @param toState
   * @return Builder
   */
  public StateModelDefinitionBuilder addTransition(State fromState, State toState) {
    addTransition(fromState, toState, Integer.MAX_VALUE);
    return this;
  }

  /**
   * Add a state transition with maximal priority value
   * @see #addTransition(String, String, int)
   * @param fromState
   * @param toState
   * @return Builder
   */
  public StateModelDefinitionBuilder addTransition(String fromState, String toState) {
    addTransition(fromState, toState, Integer.MAX_VALUE);
    return this;
  }

  /**
   * Set a maximum for replicas in this state
   * @param state state name
   * @param upperBound maximum
   * @return Builder
   */
  public StateModelDefinitionBuilder upperBound(State state, int upperBound) {
    return upperBound(state.toString(), upperBound);
  }

  /**
   * Set a maximum for replicas in this state
   * @param state state name
   * @param upperBound maximum
   * @return Builder
   */
  public StateModelDefinitionBuilder upperBound(String state, int upperBound) {
    stateConstraintMap.put(state, String.valueOf(upperBound));
    return this;
  }

  /**
   * You can use this to have the bounds dynamically change based on other
   * parameters. <br/>
   * Currently support 2 values <br/>
   * R --> Refers to the number of replicas specified during resource
   * creation. This allows having different replication factor for each
   * resource without having to create a different state machine. <br/>
   * N --> Refers to all nodes in the cluster. Useful for resources that need
   * to exist on all nodes. This way one can add/remove nodes without having
   * the change the bounds.
   * @param state
   * @param bound
   * @return Builder
   */
  public StateModelDefinitionBuilder dynamicUpperBound(State state, String bound) {
    return dynamicUpperBound(state.toString(), bound);
  }

  /**
   * You can use this to have the bounds dynamically change based on other
   * parameters. <br/>
   * Currently support 2 values <br/>
   * R --> Refers to the number of replicas specified during resource
   * creation. This allows having different replication factor for each
   * resource without having to create a different state machine. <br/>
   * N --> Refers to all nodes in the cluster. Useful for resources that need
   * to exist on all nodes. This way one can add/remove nodes without having
   * the change the bounds.
   * @param state
   * @param bound
   * @return Builder
   */
  public StateModelDefinitionBuilder dynamicUpperBound(String state, String bound) {
    stateConstraintMap.put(state, bound);
    return this;
  }

  /**
   * Create a StateModelDefinition from this Builder
   * @return StateModelDefinition
   */
  public StateModelDefinition build() {
    ZNRecord record = new ZNRecord(_statemodelName);

    // get sorted state priorities by specified values
    ArrayList<String> statePriorityList = new ArrayList<String>(statesMap.keySet());
    Comparator<? super String> c1 = new Comparator<String>() {

      @Override
      public int compare(String o1, String o2) {
        return statesMap.get(o1).compareTo(statesMap.get(o2));
      }
    };
    Collections.sort(statePriorityList, c1);

    // get sorted transition priorities by specified values
    ArrayList<Transition> transitionList = new ArrayList<Transition>(transitionMap.keySet());
    Comparator<? super Transition> c2 = new Comparator<Transition>() {
      @Override
      public int compare(Transition o1, Transition o2) {
        return transitionMap.get(o1).compareTo(transitionMap.get(o2));
      }
    };
    Collections.sort(transitionList, c2);
    List<String> transitionPriorityList = new ArrayList<String>(transitionList.size());
    for (Transition t : transitionList) {
      transitionPriorityList.add(t.toString());
    }

    record.setSimpleField(StateModelDefinitionProperty.INITIAL_STATE.toString(), initialState);
    record.setListField(StateModelDefinitionProperty.STATE_PRIORITY_LIST.toString(),
        statePriorityList);
    record.setListField(StateModelDefinitionProperty.STATE_TRANSITION_PRIORITYLIST.toString(),
        transitionPriorityList);

    // compute full paths for next states
    StateTransitionTableBuilder stateTransitionTableBuilder = new StateTransitionTableBuilder();
    Map<String, Map<String, String>> transitionTable =
        stateTransitionTableBuilder.buildTransitionTable(statePriorityList,
            new ArrayList<Transition>(transitionMap.keySet()));
    for (String state : transitionTable.keySet()) {
      record.setMapField(state + ".next", transitionTable.get(state));
    }

    // state counts
    for (String state : statePriorityList) {
      HashMap<String, String> metadata = new HashMap<String, String>();
      if (stateConstraintMap.get(state) != null) {
        metadata.put("count", stateConstraintMap.get(state));
      } else {
        metadata.put("count", "-1");
      }
      record.setMapField(state + ".meta", metadata);
    }
    return new StateModelDefinition(record);
  }

}
