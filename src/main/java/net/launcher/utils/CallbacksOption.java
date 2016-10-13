package net.launcher.utils;


import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class CallbacksOption
{
	public static <T> Optional<T> call(Callable<T> callable, Consumer<Exception> exceptionConsumer)
	{
		try
		{
			return Optional.ofNullable(callable.call());
		}
		catch (Exception e)
		{
			exceptionConsumer.accept(e);
			return Optional.empty();
		}
	}

	public static <T> Optional<T> whatever(Callable<T> callable)
	{
		try
		{
			return Optional.ofNullable(callable.call());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	public static <V> Callback<V> create(Consumer<V> consumer)
	{
		return new Callback<V>()
		{
			@Override
			public void done(V result)
			{
				consumer.accept(result);
			}

			@Override
			public void failed(Throwable e)
			{
				e.printStackTrace();
			}

			@Override
			public void cancelled()
			{}
		};
	}


	public static <T> Runnable createTask(Callable<T> callable, Callback<T> callback)
	{
		return () ->
		{
			try
			{
				T call = callable.call();
				callback.done(call);
			}
			catch (Exception e)
			{
				callback.failed(e);
			}
		};
	}

	public static <T> Callable<T> wrap(Callable<T> callable, Callback<T> callback)
	{
		return () ->
		{
			try
			{
				T call = callable.call();
				callback.done(call);
				return call;
			}
			catch (Exception e)
			{
				callback.failed(e);
				throw e;
			}
		};
	}

	public static <T> Callable<T> wrapFallback(Callable<T> primary, Callable<T> fallback, Callback<T> callback)
	{
		return () ->
		{
			try
			{
				T call = primary.call();
				callback.done(call);
				return call;
			}
			catch (Exception e)
			{
				try
				{
					T call = fallback.call();
					callback.done(call);
					return call;
				}
				catch (Exception fail)
				{
					fail.addSuppressed(e);
					callback.failed(fail);
					throw fail;
				}
			}
		};
	}

	public static <V> Callback<V> create(Consumer<V> consumer, Consumer<Throwable> handler)
	{
		return new Callback<V>()
		{
			@Override
			public void done(V result)
			{
				consumer.accept(result);
			}

			@Override
			public void failed(Throwable e)
			{
				handler.accept(e);
			}

			@Override
			public void cancelled() {}
		};
	}

	public static <V> Callback<V> create(Consumer<V> consumer, Thread.UncaughtExceptionHandler handler)
	{
		return new Callback<V>()
		{
			@Override
			public void done(V result)
			{
				consumer.accept(result);
			}

			@Override
			public void failed(Throwable e)
			{
				handler.uncaughtException(Thread.currentThread(), e);
			}

			@Override
			public void cancelled() {}
		};
	}
}
