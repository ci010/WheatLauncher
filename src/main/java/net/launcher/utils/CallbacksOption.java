package net.launcher.utils;


import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class CallbacksOption
{
	@FunctionalInterface
	public interface SafeCallable<V> extends Callable<V>
	{
		@Override
		V call();
	}

	public static <T> Callable<T> wrap(SafeCallable<T> safeCallable)
	{
		Objects.requireNonNull(safeCallable);
		return safeCallable;
	}

	public static <T> Optional<T> optional(Callable<T> callable, Consumer<Exception> exceptionConsumer)
	{
		Objects.requireNonNull(callable);
		Objects.requireNonNull(exceptionConsumer);

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
		Objects.requireNonNull(callable);

		try
		{
			return Optional.ofNullable(callable.call());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	public static <V> Callback<V> accpet(Consumer<V> consumer)
	{
		Objects.requireNonNull(consumer);
		return new CallbackAdapter<V>()
		{
			@Override
			public void done(V result)
			{
				consumer.accept(result);
			}
		};
	}

	public static <V> Callback<V> handle(Consumer<Throwable> consumer)
	{
		Objects.requireNonNull(consumer);

		return new CallbackAdapter<V>()
		{
			@Override
			public void failed(Throwable e)
			{
				consumer.accept(e);
			}
		};
	}

	public static <V> Callback<V> create(Consumer<V> consumer, Consumer<Throwable> handler)
	{
		Objects.requireNonNull(consumer);
		Objects.requireNonNull(handler);

		return new CallbackAdapter<V>()
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
		};
	}

	public static <T> Callback<T> empty()
	{
		return new Callback<T>()
		{
			@Override
			public void done(T result) {}

			@Override
			public void failed(Throwable e) {}

			@Override
			public void cancelled() {}
		};
	}

	public static <T> Callable<T> wrap(Callable<T> callable, Callback<T> callback)
	{
		Objects.requireNonNull(callable);

		Callback<T> back = callback == null ? empty() : callback;
		return () ->
		{
			try
			{
				T call = callable.call();
				back.done(call);
				return call;
			}
			catch (Exception e)
			{
				back.failed(e);
				throw e;
			}
		};
	}

	public static <T> Callable<T> wrapFallback(Callable<T> primary, Callable<T> fallback, Callback<T> callback)
	{
		Objects.requireNonNull(primary);
		Objects.requireNonNull(fallback);
		Callback<T> back = callback == null ? empty() : callback;

		return () ->
		{
			try
			{
				T call = primary.call();
				back.done(call);
				return call;
			}
			catch (Exception e)
			{
				try
				{
					T call = fallback.call();
					back.done(call);
					return call;
				}
				catch (Exception fail)
				{
					fail.addSuppressed(e);
					back.failed(fail);
					throw fail;
				}
			}
		};
	}
}
