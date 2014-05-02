package org.apache.helix.api.query;

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
 * A helix query base interface which marks all derived types to be specialized queries
 * to be executed against the cluster for entities in the Helix ecosystem. The entities are
 * <ul>
 *  <li>Cluster</li>
 *  <li>Member</li>
 *  <li>Resource</li>
 *  <li>Partition</li>
 * </ul>
 * 
 * To build a query you should leverage the HelixQueryBuilderFactory @see HelixQueryBuilderFactory
 */
public interface HelixQuery {

}
