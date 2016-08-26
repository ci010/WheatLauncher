package net.wheatlauncher.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * important... maybe this is a problem of framework design.., I have to refresh this so that it could
 * adapt initial state.
 *
 * @author ci010
 */
public class ListenerUtils
{
	public static <T> void addListenerAndNotify(ObservableValue<T> value, ChangeListener<T> listener)
	{
		value.addListener(listener);
		listener.changed(value, null, value.getValue());
	}

	public static void addListenerAndNotify(Observable observable, InvalidationListener listener)
	{
		observable.addListener(listener);
		listener.invalidated(observable);
	}
}
