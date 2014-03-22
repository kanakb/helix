package org.apache.helix.api.event;

public interface EventService {

  boolean publish(HelixEvent event);

  boolean subscribe(Class<? extends HelixEvent> eventType, EventSubscriber subscriber);

  boolean subscribe(Class<? extends HelixEvent> eventType, EventFilter filter,
      EventSubscriber subscriber);

  boolean unsubscribe(Class<? extends HelixEvent> eventType, EventFilter filter,
      EventSubscriber subscriber);

  boolean unsubscribe(Class<? extends HelixEvent> eventType, EventSubscriber subscriber);

}
