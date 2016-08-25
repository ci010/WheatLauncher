package net.wheatlauncher.resourcepack;

import net.wheatlauncher.Core;

import java.io.File;

/**
 * @author ci010
 */
public class ResourcePack
{
	private String packName;
	private byte[] hash;
	private long size;
	private boolean isZip;

	public ResourcePack(String packName, byte[] hash, long size, boolean isZip)
	{
		this.packName = packName;
		this.hash = hash;
		this.size = size;
		this.isZip = isZip;
	}

	public File getLocation()
	{
		return new File(Core.INSTANCE.getRoot(), "resourcepacks/" + packName);
	}

	public String getPackName()
	{
		return packName;
	}

	public byte[] getMD5()
	{
		return hash;
	}

	public long getSize()
	{
		return size;
	}
}
