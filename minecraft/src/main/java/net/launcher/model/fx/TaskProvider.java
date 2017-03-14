package net.launcher.model.fx;

import javafx.concurrent.Task;

/**
 * @author ci010
 */
public interface TaskProvider
{
	String getId();

	Task<?> createTask(String... args);
}
