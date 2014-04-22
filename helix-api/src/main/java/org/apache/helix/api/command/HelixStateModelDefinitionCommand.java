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
  
  public StateModelDefinitionId getId(){
    return this.stateModelDefinitionId;
  }

  public void addStates(Set<String> states) {
    this.states = states;
  }
  
  public void addStateConstraint(String state, HelixStateConstraintCommand stateConstraint){
    this.stateConstraints.put(state, stateConstraint);
  }
  
  public void addTransition(String fromState, String toState, HelixTransitionConstraintCommand transitionConstraint){
    this.transitionConstraints.put(fromState+"_" + toState, transitionConstraint);
    Set<String> toStates = transitions.get(fromState);
    if(toStates == null){
      toStates = new HashSet<String>();
    }
    toStates.add(toState);
    transitions.put(fromState, toStates);
  }
  
  public void addTransition(String fromState, Set<String> toStates, HelixTransitionConstraintCommand transitionConstraint){
    for(String toState: toStates){
      this.addTransition(fromState, toState, transitionConstraint);
    }
  }
}
