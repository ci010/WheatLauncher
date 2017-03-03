package api.launcher;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import net.launcher.utils.UnsafeRunnable;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author ci010
 */
public interface TaskCenter
{
	<T> Task<T> runTask(Task<T> tTask);

	void runTasks(Collection<Task<?>> tasks);

	<T> Task<T> listenTask(Task<T> task);

	void reportError(String title, Throwable throwable);

	ObservableList<Worker<?>> getAllWorkerHistory();

	//@formatter:off
	default <T> T runSimpleTask(String title, Callable<T> callable)
	{
		Objects.requireNonNull(title);
		Objects.requireNonNull(callable);
		try {return callable.call();}
		catch (Exception e) {reportError(title, e);return null;}
	}
	default void runSimpleTask(String title, UnsafeRunnable runnable)
	{
		Objects.requireNonNull(title);
		Objects.requireNonNull(runnable);
		try {runnable.run();}
		catch (Exception e) {reportError(title, e);}
	}
	//@formatter:on

}
