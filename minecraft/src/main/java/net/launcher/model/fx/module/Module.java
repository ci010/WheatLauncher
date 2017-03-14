package net.launcher.model.fx.module;

import net.launcher.model.fx.TaskProvider;

import java.nio.file.Path;
import java.util.List;

/**
 * @author ci010
 */
public abstract class Module
{
	private Path root;

	Module() {}

	public final void init(Path root)
	{
		if (root == null)
			this.root = root;
	}

	protected abstract void onInit();

	protected final Path getRoot() {return root;}

	public abstract List<TaskProvider> getAllTaskProviders();
}
