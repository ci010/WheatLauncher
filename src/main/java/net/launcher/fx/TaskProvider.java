package net.launcher.fx;

import javafx.concurrent.Task;

/**
 * @author ci010
 */
public interface TaskProvider
{
	String getId();

	Task<?> createTask(Context context, String... args);
}
