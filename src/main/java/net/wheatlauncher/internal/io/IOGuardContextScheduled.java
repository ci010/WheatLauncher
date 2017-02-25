package net.wheatlauncher.internal.io;

import api.launcher.ARML;
import api.launcher.event.ModuleLoadedEvent;
import api.launcher.io.IOGuard;
import api.launcher.io.IOGuardContext;
import javafx.beans.Observable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class IOGuardContextScheduled implements IOGuardContext
{
	private Path root;
	private Map<Class, IOGuard> ioGuards;
	private List<IOTask> tasks = Collections.synchronizedList(new LinkedList<>());
	private Lock lock = new ReentrantLock();

	private IOGuardContextScheduled(Path root, Map<Class, IOGuard> ioGuards)
	{
		this.root = root;
		this.ioGuards = ioGuards;
	}

	@Override
	public void saveAll() throws Exception
	{
		for (IOTask ioTask : tasks) ioTask.performance(this.getRoot());
	}

	@Override
	public void loadAll() throws IOException
	{
		for (Class clz : ioGuards.keySet()) load(clz);
	}

	@Override
	public <T> T load(Class<T> tClass) throws IOException
	{
		IOGuard<T> ioGuard = ioGuards.get(tClass);
		if (ioGuard.isActive()) return ioGuard.getInstance();
		T load = ioGuard.load();
		ARML.bus().postEvent(new ModuleLoadedEvent<>(load));
		return load;
	}

	@Override
	public <T> T getInstance(Class<T> tClass)
	{
		IOGuard<T> ioGuard = ioGuards.get(tClass);
		if (ioGuard.isActive()) return ioGuard.getInstance();
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IOGuard<T> getGuard(Class<T> clz) {return ioGuards.get(clz);}

	@Override
	public Path getRoot() {return root;}

	@Override
	public void enqueue(IOTask task)
	{
		ARML.logger().info("listen " + task);
		lock.tryLock();
		for (IOTask ioTask : tasks)
			if (ioTask.isEquivalence(task))
			{
				lock.unlock();
				return;
			}
		tasks.add(task);
		lock.unlock();
	}

	@Override
	public void registerSaveTask(IOTask task, Observable... observables)
	{
		for (Observable observable : observables)
			observable.addListener(o -> enqueue(task));
		//the life cycle of the task is equal to the observables
	}

	public static class Builder implements org.to2mbn.jmccc.util.Builder<IOGuardContext>
	{
		private Map<Class, IOGuard> ioGuards = new HashMap<>();
		private Path path = null;
		private ScheduledExecutorService service;
		private Consumer<Throwable> exceptionHandler = Throwable::printStackTrace;
		private long period = 1;
		private TimeUnit timeUnit = TimeUnit.MINUTES;

		private Builder(Path path, ScheduledExecutorService service)
		{
			this.path = path;
			this.service = service;
		}

		public static Builder create(Path path, ScheduledExecutorService scheduledExecutorService)
		{
			return new Builder(path, scheduledExecutorService);
		}

		public Builder setPath(Path path)
		{
			Objects.requireNonNull(path);
			this.path = path;
			return this;
		}

		public Builder setService(ScheduledExecutorService service)
		{
			Objects.requireNonNull(service);
			this.service = service;
			return this;
		}

		public Builder setExceptionHandler(Consumer<Throwable> exceptionHandler)
		{
			Objects.requireNonNull(exceptionHandler);
			this.exceptionHandler = exceptionHandler;
			return this;
		}

		public Builder setPeriod(long period)
		{
			this.period = period;
			return this;
		}

		public Builder setTimeUnit(TimeUnit timeUnit)
		{
			Objects.requireNonNull(timeUnit);
			this.timeUnit = timeUnit;
			return this;
		}

		public <T> Builder register(Class<T> clz, IOGuard<T> guard)
		{
			Objects.requireNonNull(clz);
			Objects.requireNonNull(guard);
			if (ioGuards.containsKey(clz)) return this;
			ioGuards.put(clz, guard);
			return this;
		}

		@Override
		public IOGuardContext build()
		{
			IOGuardContextScheduled context = new IOGuardContextScheduled(path, ioGuards);
			for (IOGuard ioGuard : ioGuards.values())
				ioGuard.init(context);
			service.scheduleAtFixedRate(() ->
			{
				context.lock.tryLock();
				LinkedList<IOTask> copy = new LinkedList<>(context.tasks);
				context.tasks.clear();
				context.lock.unlock();
				while (!copy.isEmpty())
				{
					IOTask task = copy.remove(0);
					try {task.performance(context.root);}
					catch (Exception e) {exceptionHandler.accept(e);}
				}

			}, period, period, timeUnit);
			return context;
		}
	}
}
