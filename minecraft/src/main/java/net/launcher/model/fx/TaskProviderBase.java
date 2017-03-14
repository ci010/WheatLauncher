package net.launcher.model.fx;

/**
 * @author ci010
 */
public abstract class TaskProviderBase implements TaskProvider
{
	private String id;

	public TaskProviderBase(String id)
	{
		this.id = id;
	}

	@Override
	public String getId()
	{
		return id;
	}
}
