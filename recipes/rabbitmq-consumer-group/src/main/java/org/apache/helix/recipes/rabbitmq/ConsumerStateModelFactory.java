package org.apache.helix.recipes.rabbitmq;

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

import org.apache.helix.api.id.PartitionId;
import org.apache.helix.participant.statemachine.HelixStateModelFactory;

public class ConsumerStateModelFactory extends HelixStateModelFactory<ConsumerStateModel> {
  private final String _consumerId;
  private final String _mqServer;

  public ConsumerStateModelFactory(String consumerId, String msServer) {
    _consumerId = consumerId;
    _mqServer = msServer;
  }

  @Override
  public ConsumerStateModel createNewStateModel(PartitionId partition) {
    ConsumerStateModel model = new ConsumerStateModel(_consumerId, partition.toString(), _mqServer);
    return model;
  }
}
