package net.launcher.fx.impl;

import javafx.concurrent.Task;
import net.launcher.utils.NIOUtils;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ci010
 */
public class ExportTask extends Task<Path>
{
	private Path from, to;

	public ExportTask(Path from, Path to)
	{
		this.from = from;
		this.to = to;
	}

	@Override
	protected Path call() throws Exception
	{
		if (Files.isDirectory(from))
			NIOUtils.copyDirectory(from, to);
		else Files.copy(from, to);
		return to;
	}
}
