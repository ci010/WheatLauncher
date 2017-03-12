package net.launcher.model;

import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.WindowSize;

/**
 * @author ci010
 */
public interface Profile
{
	String getId();

	int getMaxMemory();

	void setMaxMemory(int maxMemory);

	int setMinMemory();

	void setMinMemory(int minMemory);

	String getName();

	void setName(String name);

	String getMinecraftVersion();

	void setMinecraftVersion(String minecraftVersion);

	WindowSize getResolution();

	void setResolution(WindowSize resolution);

	JavaEnvironment getJavaLocation();

	void setJavaLocation(JavaEnvironment javaLocation);
}
