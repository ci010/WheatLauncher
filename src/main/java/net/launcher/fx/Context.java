package net.launcher.fx;

import javafx.concurrent.Task;

/**
 * @author ci010
 */
public interface Context
{
	Task<?> buildTask(String id, String... args);

	<T> T getInstance(Class<T> tClass);

	<T> View<T> getView(Class<T> type);
}
