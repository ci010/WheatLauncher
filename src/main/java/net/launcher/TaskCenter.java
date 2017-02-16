package net.launcher;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.util.concurrent.Callable;

/**
 * @author ci010
 */
public interface TaskCenter
{
	<T> Task<T> wrap(Callable<T> callable);

	<T> Task<T> run(Callable<T> callable);

	void reportError(Throwable throwable);

	void listen(Worker<?> worker);

	ObservableList<Worker<?>> getAllRunningWorkers();

	ObservableList<Service<?>> getAllRunningServices();
}
