package net.launcher.utils;

import javafx.beans.InvalidationListener;

import java.util.function.Function;

/**
 * @author ci010
 */
public class InvalidListenerCache<T>
{
	private Function<T, InvalidationListener> function;
	private InvalidationListener listener;

	public InvalidListenerCache(Function<T, InvalidationListener> listenerFunction)
	{
		function = listenerFunction;
	}

	public InvalidationListener listener()
	{
		return listener;
	}

	public InvalidationListener listener(T v)
	{
		return listener = function.apply(v);
	}
}


