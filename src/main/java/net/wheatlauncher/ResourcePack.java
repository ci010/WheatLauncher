package net.wheatlauncher;

import java.io.File;

/**
 * @author ci010
 */
public interface ResourcePack
{
	File getLocation();

	String getPath();

	String getPackName();

	byte[] getMD5();

	long getSize();
}
