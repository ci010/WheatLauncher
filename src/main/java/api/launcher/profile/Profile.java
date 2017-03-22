package api.launcher.profile;

import api.launcher.version.MinecraftVersion;
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

	int getMinMemory();

	void setMinMemory(int minMemory);

	MinecraftVersion getVersion();

	void setVersion(MinecraftVersion version);

	String getName();

	void setName(String name);

	WindowSize getResolution();

	void setResolution(WindowSize resolution);

	JavaEnvironment getJavaLocation();

	void setJavaLocation(JavaEnvironment javaLocation);
}
