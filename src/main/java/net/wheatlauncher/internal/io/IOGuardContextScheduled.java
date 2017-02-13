package net.wheatlauncher.internal.io;

import javafx.beans.Observable;
import net.launcher.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class IOGuardContextScheduled implements IOGuardContext
{
	private Path root;
	private Map<Class, IOGuard> ioGuards;
	private ScheduledExecutorService service;
	private List<IOTask> tasks = Collections.synchronizedList(new LinkedList<>());

	private IOGuardContextScheduled(Path root, Map<Class, IOGuard> ioGuards, ScheduledExecutorService service)
	{
		this.root = root;
		this.ioGuards = ioGuards;
		this.service = service;
	}

	@Override
	public void saveAll() throws Exception
	{
		for (IOTask ioTask : tasks)
			ioTask.performance(this.getRoot());
	}

	@Override
	public void loadAll() throws IOException
	{
		service.submit(() ->
		{
			for (Class clz : ioGuards.keySet()) load(clz);
			return null;
		});
	}

	@Override
	public <T> T load(Class<T> tClass) throws IOException
	{
		IOGuard<T> ioGuard = ioGuards.get(tClass);
		if (ioGuard.isActive()) return ioGuard.getInstance();
		return ioGuard.load();
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
		Logger.trace("enqueue " + task);
		boolean merged = false;
		for (IOTask ioTask : tasks)
			if (ioTask.canMerge(task)) merged = true;
		if (!merged) tasks.add(task);
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
			IOGuardContextScheduled context = new IOGuardContextScheduled(path, ioGuards, service);
			for (IOGuard ioGuard : ioGuards.values())
				ioGuard.init(context);
			service.scheduleAtFixedRate(() ->
			{
				LinkedList<IOTask> copy = new LinkedList<>();
				copy.addAll(context.tasks);//I know this is not thread safe... but, just leave it now....
				context.tasks.clear();
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
