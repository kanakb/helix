package org.apache.helix.api.command;

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
import org.apache.helix.api.id.ParticipantId;

/**
 * Command allows creating Helix Participant members
 */
public class HelixParticipantCommand extends HelixMemberCommand {
  /**
   * Creates a participant member for the cluster
   * @param participantId the identifier for the participant
   */
  public HelixParticipantCommand(ParticipantId participantId) {
    super(participantId, HelixMemberType.PARTICIPANT);
  }

  /**
   * Sets the port number for the participant
   * @param port the port number
   */
  public void setPort(int port) {
    super.setPort(port);
  }

  /**
   * Returns the port number for the participant
   * 
   * @return the port number
   */
  public int getPort() {
    return super.getPort();
  }
}
