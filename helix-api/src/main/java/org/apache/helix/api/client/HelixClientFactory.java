package org.apache.helix.api.client;

import java.util.Properties;

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
 * Client factory used to create both administrator clients or non-administrator clients
 */
public class HelixClientFactory {

  private static volatile HelixClientFactory instance = new HelixClientFactory();

  /**
   * Private constructor
   */
  private HelixClientFactory() {

  }

  /**
   * Singleton instance
   * @return HelixClientFactory the client factory instance
   */
  public static HelixClientFactory instance() {
    return instance;
  }

  /**
   * Creates a client which allows querying the cluster, members, resources and partitions. The
   * client does not allow mutation capabilities to any of the helix entities
   * @return HelixClient
   */
  public HelixClient createClient(Properties properties) {
    return null;
  }
}
