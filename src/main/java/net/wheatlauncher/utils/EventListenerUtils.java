package net.wheatlauncher.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;

import java.util.Objects;

/**
 * important... maybe this is a problem of framework design.., I have to refresh this so that it could
 * adapt initial state.
 *
 * @author ci010
 */
public class EventListenerUtils
{
	public static <T extends Event> EventHandler<T> compose(EventHandler<T> handler, EventHandler<T> another)
	{
		Objects.requireNonNull(handler);
		Objects.requireNonNull(another);
		return event ->
		{
			handler.handle(event);
			another.handle(event);
		};
	}

	public static <T> void lock(Property<T> property, T value)
	{
		Objects.requireNonNull(property);
		property.addListener(observable ->
		{
			if (property.getValue() != value) property.setValue(value);
		});
		property.setValue(value);
	}

	public static <T> void addListenerAndNotify(ObservableValue<T> value, ChangeListener<T> listener)
	{
		Objects.requireNonNull(value);
		Objects.requireNonNull(listener);

		value.addListener(listener);
		listener.changed(value, null, value.getValue());
	}

	public static void addListenerAndNotify(Observable observable, InvalidationListener listener)
	{
		Objects.requireNonNull(observable);
		Objects.requireNonNull(listener);

		observable.addListener(listener);
		listener.invalidated(observable);
	}
}
