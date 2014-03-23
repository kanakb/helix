package org.apache.helix.api.event;

import java.lang.reflect.Method;

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
 * Eventsubscriber is used by listeners of event to express interest in event types.
 * 
 * The subscriber is expected to provide an extension to the AbstractEventSubscriber
 * so that they can return filters for the events they receive which is expressed through
 * the getSubscribedEvents.
 * 
 * The handleEvent will only receive events which meet the event criteria and filters
 * as expressed by the subscriber.
 */
public abstract class AbstractEventSubscriber implements EventSubscriber{


  @Override
  public Class<? extends HelixEvent> getSubscribedEvents() {
    return null;
  }

  @Override
  public EventFilter getEventFilter() {
    return null;
  }

  /**
   * Receives all the events
   * @param event
   */
  public void handleEvent(HelixEvent event) {
    Class paramTypes[] = new Class[]{event.getClass()};
    try {
      // Look for an inform method in the current object
      // that takes the event subtype as a parameter
      Method method = getClass().getDeclaredMethod("handleEvent", paramTypes);
      Object paramList[] = new Object[1];
      paramList[0] = event;
      method.invoke(this, paramList);
      // Catch appropriate exceptions
    } catch (Exception e) {

    }
  }

}
