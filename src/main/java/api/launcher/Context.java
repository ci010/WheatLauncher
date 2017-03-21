package api.launcher;

import javafx.concurrent.Task;

/**
 * @author ci010
 */
public interface Context
{
	<T> Task<T> buildTask(String id, String... args);

	<T> T getInstance(Class<T> tClass);

	<T> T getInstance(String id, Class<T> type);

	<T> View<T> getView(Class<T> type);

	<T> View<T> getView(String id, Class<T> type);
}
