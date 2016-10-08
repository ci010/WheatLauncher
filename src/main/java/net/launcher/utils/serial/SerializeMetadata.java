package net.launcher.utils.serial;

import java.io.File;
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
}
