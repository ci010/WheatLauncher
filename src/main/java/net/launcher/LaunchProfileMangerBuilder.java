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

	private static Builder<LaunchProfile> NEW = LaunchProfileImpl::new;

	private Builder<LaunchProfile> factory = NEW;

	public LaunchProfileMangerBuilder setProfileFactory(Builder<LaunchProfile> factory)
	{
		this.factory = factory;
		return this;
	}

	@Override
	public LaunchProfileManager build()
	{
		return new LaunchProfileMangerImpl(factory);
	}
}
