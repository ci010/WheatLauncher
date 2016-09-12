package net.wheatlauncher.utils.resource;

import java.io.InputStream;

/**
 * @author ci010
 */
public interface PendingResource
{
	String path();

	ResourceType type();

	String md5();

	InputStream openStream();
}
