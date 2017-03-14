package net.launcher.model.fx;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.concurrent.Callable;

/**
 * @author ci010
 */
public interface FXShell
{
	FXModelCore getLauncherCore();

	View<TaskProvider> getAllTaskProviders();

	Task<?> buildTask(String id, String... args);

	<T> Task<T> execute(Task<T> task);

	<T> T executeImmediately(Task<T> task);

	<T> Task<T> execute(String title, Callable<T> task);

	<T> T executeImmediately(String title, Callable<T> task);

	ObservableList<Task<?>> getTaskRecords();
}
