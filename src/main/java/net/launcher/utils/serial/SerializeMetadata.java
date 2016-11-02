package net.launcher.utils.serial;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author ci010
 */
public interface SerializeMetadata
{
	static void decroateWithFileInfo(Map<Object, Object> context, File file)
	{
		context.put("fileName", file.getName());
		context.put("size", file.length());
		context.put("lastModified", file.lastModified());
		context.put("path", file.getAbsolutePath());
	}

	static void decorateWithFileInfo(Map<Object, Object> context, Path file) throws IOException
	{
		context.put("fileName", file.getFileName().toString());
		context.put("size", Files.size(file));
		context.put("lastModified", Files.getLastModifiedTime(file));
		context.put("path", file.toAbsolutePath().toString());
	}
}
