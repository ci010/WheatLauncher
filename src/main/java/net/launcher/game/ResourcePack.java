package net.launcher.game;

/**
 * @author ci010
 */
public class ResourcePack
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
