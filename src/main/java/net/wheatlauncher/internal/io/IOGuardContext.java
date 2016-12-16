package net.wheatlauncher.internal.io;

import io.datafx.controller.context.ApplicationContext;
import javafx.beans.Observable;
import net.wheatlauncher.control.utils.WindowsManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class IOGuardContext
{
	private Path root;
	private Map<Class, IOGuard> ioGuards;
	private Consumer<IOTask> taskConsumer;

	private IOGuardContext(Path root, Map<Class, IOGuard> ioGuards, Consumer<IOTask> taskConsumer)
	{
		this.root = root;
		this.ioGuards = ioGuards;
		if (taskConsumer == null) taskConsumer = task ->
		{
			try
			{
				task.performance(getRoot());
			}
			catch (IOException e)
			{
				ApplicationContext.getInstance().getRegisteredObject(WindowsManager.class).addSupressedException(e);
			}
		};
		for (IOGuard ioGuard : ioGuards.values())
			ioGuard.init(this);
		this.taskConsumer = taskConsumer;
	}

	public void saveAll() throws IOException {for (IOGuard guard : ioGuards.values()) guard.forceSave();}

	public void loadAll() throws IOException {for (Class clz : ioGuards.keySet()) load(clz);}

	public <T> T load(Class<T> tClass) throws IOException
	{
		IOGuard<T> ioGuard = ioGuards.get(tClass);
		if (ioGuard.isActive()) return ioGuard.getInstance();
		return ioGuard.load();
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

	public static class Builder implements org.to2mbn.jmccc.util.Builder<IOGuardContext>
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

		public <T> Builder register(Class<T> clz, IOGuard<T> guard)
		{
			Objects.requireNonNull(clz);
			Objects.requireNonNull(guard);
			if (ioGuards.containsKey(clz)) return this;
			ioGuards.put(clz, guard);
			return this;
		}

		@Override
		public IOGuardContext build() {return new IOGuardContext(path, ioGuards, taskExecutor);}
	}
}
