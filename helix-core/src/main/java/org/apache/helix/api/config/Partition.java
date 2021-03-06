package org.apache.helix.api.config;

import org.apache.helix.api.id.PartitionId;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

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
 * A partition of a resource
 */
public class Partition {
  @JsonProperty("id")
  private final PartitionId _id;

  /**
   * Construct a partition
   * @param id
   */
  @JsonCreator
  public Partition(@JsonProperty("id") PartitionId id) {
    _id = id;
  }

  /**
   * Get partition id
   * @return partition id
   */
  public PartitionId getId() {
    return _id;
  }

  @Override
  public String toString() {
    return _id.toString();
  }
}
