package net.launcher.model;

import java.util.Map;

/**
 * @author ci010
 */
public interface MinecraftVersion
{
	String getVersionId();

	Map<String, String> getMetadata();
}
