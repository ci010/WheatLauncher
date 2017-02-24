package net.launcher.api;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

/**
 * @author ci010
 */
public interface EventBus
{
	<T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler);

	<T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler);

	<T extends Event> void addEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter);

	<T extends Event> void removeEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter);

	<T extends Event> T postEvent(T event);
}
