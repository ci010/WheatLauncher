package net.launcher.utils;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callbacks;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author ci010
 */
public class FutureCallbackTask<T> extends FutureTask<T>
{
	private Callback<T> callback = Callbacks.empty();

	public FutureCallbackTask(Callable<T> callable)
	{
		super(callable);
	}

	public FutureCallbackTask(Runnable runnable, T result)
	{
		super(runnable, result);
	}

	public void setCallback(Callback<T> callback)
	{
		this.callback = callback;
	}

	@Override
	protected void set(T t)
	{
		super.set(t);
		callback.done(t);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning)
	{
		boolean cancel = super.cancel(mayInterruptIfRunning);
		if (cancel) callback.cancelled();
		return cancel;
	}

	@Override
	protected void setException(Throwable t)
	{
		super.setException(t);
		callback.failed(t);
	}
}
