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

import org.apache.helix.api.model.HelixProperty;
import org.apache.helix.api.model.ZNRecord;

/**
 * Wraps updates to Helix constructs, e.g. state transitions and controller task statuses
 */
public class StatusUpdate extends HelixProperty {

  /**
   * Instantiate with a pre-populated record
   * @param record ZNRecord corresponding to a status update
   * @deprecated Creating status update from a ZNRecord
   *             will be removed from API, the code will be moved to SPI
   */
  @Deprecated
  public StatusUpdate(ZNRecord record) {
    super(record);
  }

  @Override
  public boolean isValid() {
    return true;
  }

}
