package net.launcher.model;

import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.WindowSize;

/**
 * @author ci010
 */
public class ProfileBase implements Profile
{
	private String id, name, minecraftVersion;
	private WindowSize resolution;
	private JavaEnvironment javaLocation;
	private int minMemory, maxMemory;

	public ProfileBase(String id, String name, String minecraftVersion, WindowSize resolution, JavaEnvironment javaLocation)
	{
		this.id = id;
		this.name = name;
		this.minecraftVersion = minecraftVersion;
		this.resolution = resolution;
		this.javaLocation = javaLocation;
	}

	@Override
	public String getId() {return id;}

	@Override
	public int getMaxMemory() {return maxMemory;}

	@Override
	public void setMaxMemory(int maxMemory)
	{
		this.maxMemory = maxMemory;
	}

	@Override
	public int setMinMemory() {return minMemory;}

	@Override
	public void setMinMemory(int minMemory)
	{
		this.minMemory = minMemory;
	}

	@Override
	public String getName() {return name;}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getMinecraftVersion() {return minecraftVersion;}

	@Override
	public void setMinecraftVersion(String minecraftVersion)
	{
		this.minecraftVersion = minecraftVersion;
	}

	@Override
	public WindowSize getResolution() {return resolution;}

	@Override
	public void setResolution(WindowSize resolution)
	{
		this.resolution = resolution;
	}

	@Override
	public JavaEnvironment getJavaLocation() {return javaLocation;}

	@Override
	public void setJavaLocation(JavaEnvironment javaLocation)
	{
		this.javaLocation = javaLocation;
	}
}
