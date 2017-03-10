package net.launcher.utils.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ci010
 */
public interface ResourceType
{
	String getSuffix();

	boolean match(File file);

	InputStream openStream(File file, String path) throws IOException;
}
