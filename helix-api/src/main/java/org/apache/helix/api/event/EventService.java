package org.apache.helix.api.event;

import org.apache.helix.api.id.SubscriberId;

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
public interface EventService {

  /**
   * Publisher invoked API which allows the publisher to send
   * an event. The publish does not guarantee that the invokers
   * event will be delivered before the call returns, events
   * may be enqueued for delivery asynchronously based on the 
   * volume of events that are received.
   * 
   * @param event the event to be delivered to the subscribers
   * 
   * @return <b>True</b> if the event was delivered or queued for delivery
   * <b>False</b> if the event delivery failed.
   */
  boolean publish(HelixEvent event);

  /**
   * Registers a subscription for the identified subscriber
   * 
   * @param subscriber the subscriber to register to receive events
   * 
   * @return SubscriberId the unique id for the subscriber
   */
  SubscriberId registerSubscription(AbstractEventSubscriber subscriber);

  /**
   * Unregisters a subscription for the susbcriber. The id was returned
   * during the subscription registration.
   * 
   * @param subscriberId the subscriber id to unsubscribe
   * 
   * @return <b>True</b> if the susbcription was unregistered
   *         <b>False</b> if not
   * 
   */
  boolean unregisterSubscription(SubscriberId subscriberId);

}
