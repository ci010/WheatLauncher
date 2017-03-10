package net.launcher.utils.serial;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

/**
 * @author ci010
 */
public interface SerializeMetadata
{
	static void decroateWithFileInfo(Map<Object, Object> context, File file)
	{
		context.putIfAbsent("fileName", file.getName());
		context.putIfAbsent("size", file.length());
		context.putIfAbsent("lastModified", file.lastModified());
		context.putIfAbsent("path", file.getAbsolutePath());
	}

	static void decorateWithFileInfo(Map<Object, Object> context, Path file) throws IOException
	{
		Objects.requireNonNull(context);
		Objects.requireNonNull(file);

		if (!file.getFileSystem().equals(FileSystems.getDefault()))
			file = Paths.get(file.getFileSystem().toString());
		context.putIfAbsent("fileName", file.getFileName().toString());
		context.putIfAbsent("size", Files.size(file));
		context.putIfAbsent("lastModified", Files.getLastModifiedTime(file));
		context.putIfAbsent("path", file.toAbsolutePath().toString());
	}
}
