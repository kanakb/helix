package org.apache.helix.api.rebalancer;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.helix.api.id.RebalancerId;

/**
 * A rebalancer registry which tracks all the rebalancers custom or
 * out-of-box rebalancers
 * @param <T>
 */
public final class RebalancerRegistry<T extends Rebalancer> {
  private final ConcurrentMap<RebalancerId, T> registry;
  private static final RebalancerRegistry<?> instance = new RebalancerRegistry<Rebalancer>();

  /**
   * Default constructor
   */
  private RebalancerRegistry() {
    registry = new ConcurrentHashMap<RebalancerId, T>();
  }

  public static <T extends Rebalancer> RebalancerRegistry<T> instance() {
    return (RebalancerRegistry<T>) instance;
  }

  /**
   * Register a rebalancer in the registry
   * @param rebalancer the rebalancer to register
   */
  public void registerRebalancer(T rebalancer) {
    registry.put(rebalancer.getId(), rebalancer);
  }

  /**
   * Retrieves a rebalancer with a given id
   * @param id the id for the rebalancer to fetch
   * @return T the rebalancer in the registry for the id
   */
  public T getRebalancer(String id) {
    return registry.get(id);
  }
}
