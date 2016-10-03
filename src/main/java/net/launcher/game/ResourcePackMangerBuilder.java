package net.launcher.game;

import net.wheatlauncher.utils.DirUtils;
import org.to2mbn.jmccc.util.Builder;

import java.io.File;
import java.lang.ref.Reference;
import java.util.concurrent.ExecutorService;

/**
 * @author ci010
 */
public class ResourcePackMangerBuilder implements Builder<ResourcePackManger>
{
	public static ResourcePackManger buildDefault() {return create().build();}

	public static ResourcePackMangerBuilder create()
	{
		return new ResourcePackMangerBuilder();
	}

	private File root = DirUtils.getAvailableWorkDir();
	private Reference<ExecutorService> service;

	public ResourcePackMangerBuilder setRoot(File root)
	{
		this.root = root;
		return this;
	}

	public ResourcePackMangerBuilder setExecutor(Reference<ExecutorService> service)
	{
		this.service = service;
		return this;
	}

	@Override
	public ResourcePackManger build()
	{
		return new ResourcePackManImpl(root, service);
	}
}
