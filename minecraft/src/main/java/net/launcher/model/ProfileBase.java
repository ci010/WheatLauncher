package net.launcher.model;

import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.WindowSize;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author ci010
 */
public class ProfileBase implements Profile, Serializable
{
	private String id, name;
	private MinecraftVersion minecraftVersion;
	private WindowSize resolution;
	private JavaEnvironment javaLocation;
	private int minMemory = 512, maxMemory = 512;

	public static Profile create(String name)
	{
		Objects.requireNonNull(name);
		ProfileBase profileBase = new ProfileBase();
		profileBase.setName(name);
		return profileBase;
	}

	public ProfileBase(String id, String name, MinecraftVersion minecraftVersion, WindowSize resolution, JavaEnvironment javaLocation)
	{
		this(id, name, minecraftVersion, resolution, javaLocation, 512, 512);
	}

	public ProfileBase(String id, String name, MinecraftVersion minecraftVersion, WindowSize resolution, JavaEnvironment javaLocation, int minMemory, int maxMemory)
	{
		this.id = id;
		this.name = name;
		this.minecraftVersion = minecraftVersion;
		this.resolution = resolution;
		this.javaLocation = javaLocation;
		this.minMemory = minMemory;
		this.maxMemory = maxMemory;
	}

	public ProfileBase(Profile profile)
	{
		this(System.currentTimeMillis() + "", profile.getName(), profile.getVersion(), profile.getResolution(), profile
				.getJavaLocation(), profile.getMinMemory(), profile.getMaxMemory());
	}

	public ProfileBase(String name)
	{
		this(System.currentTimeMillis() + "", name, null, WindowSize.fullscreen(), JavaEnvironment.current());
	}

	public ProfileBase()
	{
		this(System.currentTimeMillis() + "", "Default", null, WindowSize.fullscreen(), JavaEnvironment.current());
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
	public int getMinMemory() {return minMemory;}

	@Override
	public void setMinMemory(int minMemory)
	{
		this.minMemory = minMemory;
	}

	@Override
	public MinecraftVersion getVersion()
	{
		return minecraftVersion;
	}

	@Override
	public void setVersion(MinecraftVersion version) {this.minecraftVersion = version;}

	@Override
	public String getName() {return name;}

	@Override
	public void setName(String name)
	{
		this.name = name;
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
