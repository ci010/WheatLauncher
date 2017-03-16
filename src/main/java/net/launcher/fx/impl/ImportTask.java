package net.launcher.fx.impl;

import javafx.concurrent.Task;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.serial.Deserializer;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ci010
 */
public class ImportTask<T> extends Task<T>
{
	private Path from, to;
	private Deserializer<T, Path> deserializer;

	public ImportTask(Path from, Path to, Deserializer<T, Path> deserializer)
	{
		this.from = from;
		this.to = to;
		this.deserializer = deserializer;
	}

	@Override
	protected T call() throws Exception
	{
		T value = deserializer.deserialize(from);
		if (Files.isDirectory(from))
			NIOUtils.copyDirectory(from, to);
		else Files.copy(from, to);
		return value;
	}
}
