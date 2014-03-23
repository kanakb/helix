package org.apache.helix.api.role;

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

import org.apache.helix.LiveInstanceInfoProvider;
import org.apache.helix.PreConnectCallback;
import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.participant.StateMachineEngine;

/**
 * Helix participant
 */
public interface HelixParticipant extends HelixRole, HelixStartable {
  /**
   * get participant id
   * @return participant id
   */
  ParticipantId getParticipantId();

  /**
   * get state machine engine
   * @return state machine engine
   */
  StateMachineEngine getStateMachineEngine();

  /**
   * add pre-connect callback
   * @param callback
   */
  void addPreConnectCallback(PreConnectCallback callback);

  /**
   * Add a LiveInstanceInfoProvider that is invoked before creating liveInstance.</br>
   * This allows applications to provide additional information that will be published to zookeeper
   * and become available for discovery</br>
   * @see LiveInstanceInfoProvider#getAdditionalLiveInstanceInfo()
   * @param liveInstanceInfoProvider
   */
  void setLiveInstanceInfoProvider(LiveInstanceInfoProvider liveInstanceInfoProvider);

}
