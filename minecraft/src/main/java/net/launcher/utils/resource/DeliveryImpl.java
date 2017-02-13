package net.launcher.utils.resource;

import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author ci010
 */
public class DeliveryImpl<T> implements Repository.Delivery<T>
{
	private Future<T> future;
	private Set<Path> virtualPaths;
	private WeakReference<ExecutorService> service;

	public DeliveryImpl(Future<T> future, Set<Path> virtualPaths, ExecutorService service)
	{
		this.future = future;
		this.virtualPaths = Collections.unmodifiableSet(virtualPaths);
		this.service = new WeakReference<>(service);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {return future.cancel(mayInterruptIfRunning);}

	@Override
	public boolean isCancelled() {return future.isCancelled();}

	@Override
	public boolean isDone() {return future.isDone();}

	@Override
	public T get() throws InterruptedException, ExecutionException {return future.get();}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {return future.get(timeout, unit);}

	@Override
	public Set<Path> getResourceVirtualPaths()
	{
		return virtualPaths;
	}

	@Override
	public boolean markRelease() throws IllegalStateException
	{
		if (virtualPaths == null) throw new IllegalStateException("The delivery has been closed.");
		ExecutorService executorService = service.get();
		if (executorService == null)
			return true;
		else
		{
			service = new WeakReference<>(null);
			executorService.submit(() ->
			{
				get();
				for (Path path : virtualPaths) Files.deleteIfExists(path);
				return null;
			});
		}
		return true;
	}

	@Override
	public void close() throws Exception
	{
		future = null;
		virtualPaths = null;
		service = null;
	}
}
