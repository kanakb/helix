package org.apache.helix.api.command;

import java.util.Collection;

import org.apache.helix.api.id.ParticipantId;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.api.id.ResourceId;

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

public class ClusterCommand {
  public ClusterCommand enable(boolean enable) {
    return this;
  }

  public ClusterCommand autoJoin(boolean autoJoin) {
    return this;
  }

  public ClusterCommand resetResources(Collection<ResourceId> resources) {
    return this;
  }

  public ClusterCommand resetParticipants(Collection<ParticipantId> participants) {
    return this;
  }

  public ClusterCommand resetPartitions(ParticipantId participantId, ResourceId resourceId,
      Collection<PartitionId> partitionId) {
    return this;
  }
}
