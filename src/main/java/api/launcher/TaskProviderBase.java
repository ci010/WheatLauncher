package api.launcher;

import javafx.concurrent.Task;

import java.util.function.Function;

/**
 * @author ci010
 */
public class TaskProviderBase implements TaskProvider
{
	private String id;
	private Function<String[], Task<?>> function;

	public TaskProviderBase(String id, Function<String[], Task<?>> function)
	{
		this.id = id;
		this.function = function;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public Task<?> createTask(Context widget, String... args)
	{
		return function.apply(args);
	}
}
