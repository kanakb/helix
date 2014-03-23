package org.apache.helix.api.event;

import java.util.Set;

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
 * An interface meant to be implemented by Event recipients so that they can
 * choose to receive events they are interested in through getSubscribedEvents
 * and getFilter.
 * 
 * The event system will deliver events to the handleEvent callback when an
 * event which meets the criteria is received.
 * 
 * It is recommended that you use the AbstractEventSubscriber to subscribe for events
 *
 */
public interface EventSubscriber {

  /**
   * Retrieves a list of subscribed events. This method is only
   * called once during the lifetime of a subscriber which is
   * during registration of the event subscriber
   * @return Class<? extends HelixEvent> the set of helix events
   *         the subscriber subscribes to
   */
  public Set<Class<? extends HelixEvent>> getSubscribedEvents();

  /**
   * Retrieves the event filter for the subscribed events
   * @return EventFilter the filter for the event
   */
  public EventFilter getEventFilter(Class<? extends HelixEvent> event);

  /**
   * Callback where an event is delivered to this subscriber after
   * the filters are applied if any were provided.
   * 
   * @param event the event to be handled
   */
  public void handleEvent(HelixEvent event);
}
