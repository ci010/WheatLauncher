package net.launcher.utils;

import javafx.beans.value.ChangeListener;

import java.util.function.Function;

/**
 * @author ci010
 */
public class ChangeListenerCache<T, R>
{
	private ChangeListener<R> changeListener;
	private Function<T, ChangeListener<R>> function;

	public ChangeListenerCache(Function<T, ChangeListener<R>> function)
	{
		this.function = function;
	}

	public ChangeListener<R> listener()
	{
		return changeListener;
	}

	public ChangeListener<R> listener(T v)
	{
		return changeListener = function.apply(v);
	}
}
