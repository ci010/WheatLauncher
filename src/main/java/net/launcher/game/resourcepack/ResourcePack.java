package net.launcher.game.resourcepack;

import net.launcher.LaunchElement;

/**
 * @author ci010
 */
public class ResourcePack implements LaunchElement
{
	private String packName, description;
	private int format;

	public ResourcePack(String packName, String description, int format)
	{
		this.packName = packName;
		this.description = description;
		this.format = format;
	}

	public String getPackName()
	{
		return packName;
	}

	public int packFormat()
	{
		return format;
	}

	public String getDescription()
	{
		return description;
	}
}
