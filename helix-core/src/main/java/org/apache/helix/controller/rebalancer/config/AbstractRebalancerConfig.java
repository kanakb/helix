package org.apache.helix.controller.rebalancer.config;

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

import org.apache.helix.api.config.RebalancerConfig;
import org.apache.helix.controller.rebalancer.HelixRebalancer;
import org.apache.helix.controller.serializer.StringSerializer;
import org.apache.helix.model.IdealState.RebalanceMode;

/*
 * TODO: all the methods in this class should belong in the base interface
 */
public abstract class AbstractRebalancerConfig implements RebalancerConfig {

  /**
   * Get the serializer for this config
   * @return StringSerializer class object
   */
  public abstract Class<? extends StringSerializer> getSerializerClass();

  /**
   * Get a reference to the class used to rebalance this resource
   * @return RebalancerRef
   */
  public abstract Class<? extends HelixRebalancer> getRebalancerClass();

  /**
   * Get the rebalancer mode of the resource
   * @return RebalanceMode
   */
  public abstract RebalanceMode getRebalanceMode();

}
