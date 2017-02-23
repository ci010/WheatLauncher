package net.launcher.api;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.util.Collection;

/**
 * @author ci010
 */
public interface TaskCenter
{
	Task<?> runTask(Task<?> tTask);

	void runTasks(Collection<Task<?>> tasks);

	void reportError(Throwable throwable);

	ObservableList<Throwable> getAllErrors();

	ObservableList<Worker<?>> getAllRunningWorkers();
}
