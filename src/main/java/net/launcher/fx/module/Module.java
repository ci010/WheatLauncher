package net.launcher.fx.module;

import net.launcher.fx.TaskProvider;

import java.nio.file.Path;
import java.util.List;

/**
 * @author ci010
 */
public abstract class Module
{
	private Path root;

	Module() {}

	public final void init(Path root) throws Exception
	{
		if (root == null)
			this.root = root;
		onInit();
	}

	protected abstract void onInit() throws Exception;

	protected final Path getRoot() {return root;}

	public abstract List<TaskProvider> getAllTaskProviders();
}
