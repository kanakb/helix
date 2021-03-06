package org.apache.helix.api.command;

import org.apache.helix.api.id.AdministratorId;

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
 * This command is to create/update the administrator member
 */
public class HelixAdministratorCommand extends HelixMemberCommand {

  /**
   * Creates a cluster member of type administrator for the given cluster
   * @param clusterId the cluster for which the administrator is created
   */
  protected HelixAdministratorCommand(AdministratorId adminId) {
    super(adminId, HelixMemberType.ADMINISTRATOR);
  }

}
