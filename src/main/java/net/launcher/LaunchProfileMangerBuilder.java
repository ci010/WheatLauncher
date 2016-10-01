package net.launcher;

import org.to2mbn.jmccc.util.Builder;

/**
 * @author ci010
 */
public class LaunchProfileMangerBuilder implements Builder<LaunchProfileManager>
{
	public static LaunchProfileMangerBuilder create()
	{
		return new LaunchProfileMangerBuilder();
	}

	public static LaunchProfileManager buildDefault()
	{
		return create().build();
	}

	@Override
	public LaunchProfileManager build()
	{
		return new LaunchProfileMangerImpl();
	}
}
