package net.wheatlauncher.internal.io;

import javafx.beans.Observable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class IOGuardManger
{
	private Path root;
	private Map<Class, IOGuard> ioGuards;
	private Consumer<IOTask> taskConsumer;

	private IOGuardManger(Path root, Map<Class, IOGuard> ioGuards, Consumer<IOTask> taskConsumer)
	{
		this.root = root;
		this.ioGuards = ioGuards;
		if (taskConsumer == null) taskConsumer = task -> task.performance(getRoot());
		this.taskConsumer = taskConsumer;
	}

	public void loadAll() throws IOException {for (Class clz : ioGuards.keySet()) load(clz);}

	public <T> T load(Class<T> tClass) throws IOException
	{
		IOGuard<T> ioGuard = ioGuards.get(tClass);
		if (ioGuard.isActive()) return ioGuard.getInstance();
		return ioGuard.load(this);
	}

	@SuppressWarnings("unchecked")
	public <T> IOGuard<T> getGuard(Class<T> clz) {return ioGuards.get(clz);}

	public Path getRoot() {return root;}

	public void enqueue(IOTask task) {taskConsumer.accept(task);}

	public void registerSaveTask(Observable[] observables, IOTask task)
	{
		for (Observable observable : observables)
			observable.addListener(o -> taskConsumer.accept(task));
		//the life cycle of the task is equal to the observables
	}

	public interface IOTask
	{
		void performance(Path root) throws IOException;

		default boolean canMerge(IOTask task) {return task.getClass().equals(this.getClass());}
	}

	public static class Builder implements org.to2mbn.jmccc.util.Builder<IOGuardManger>
	{
		private Map<Class, IOGuard> ioGuards = new HashMap<>();
		private Path path = null;
		private Consumer<IOTask> taskExecutor = null;

		private Builder(Path path) {this.path = path;}

		public static Builder create(Path path) {return new Builder(path);}

		public Builder setPath(Path path)
		{
			Objects.requireNonNull(path);
			this.path = path;
			return this;
		}

		public Builder setTaskExecutor(Consumer<IOTask> taskExecutor)
		{
			Objects.requireNonNull(taskExecutor);
			this.taskExecutor = taskExecutor;
			return this;
		}

		public <T> boolean register(Class<T> clz, IOGuard<T> guard)
		{
			Objects.requireNonNull(clz);
			Objects.requireNonNull(guard);
			if (ioGuards.containsKey(clz)) return false;
			ioGuards.put(clz, guard);
			return true;
		}

		@Override
		public IOGuardManger build() {return new IOGuardManger(path, ioGuards, taskExecutor);}
	}
}
