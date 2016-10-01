package net.launcher;

import org.to2mbn.jmccc.util.Builder;

/**
 * @author ci010
 */
public class ResourcePackMangerBuilder implements Builder<ResourcePackManger>
{
	public static ResourcePackManger buildDefault()
	{
		return create().build();
	}

	public static ResourcePackMangerBuilder create() {return new ResourcePackMangerBuilder();}

	@Override
	public ResourcePackManger build()
	{
		return new ResourcePackManImpl();
	}
}
