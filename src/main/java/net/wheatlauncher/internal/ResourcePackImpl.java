package net.wheatlauncher.internal;

import net.wheatlauncher.ResourcePack;

/**
 * @author ci010
 */
public class ResourcePackImpl implements ResourcePack
{
	private String packName, description;
	private int format;

	public ResourcePackImpl(String packName, String description, int format)
	{
		this.packName = packName;
		this.description = description;
		this.format = format;
	}

	@Override
	public String getPackName()
	{
		return packName;
	}

	@Override
	public int packFormat()
	{
		return format;
	}

	@Override
	public String getDescription()
	{
		return description;
	}
}
