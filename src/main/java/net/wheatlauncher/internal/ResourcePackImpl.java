package net.wheatlauncher.internal;

import net.wheatlauncher.Core;
import net.wheatlauncher.ResourcePack;

import java.io.File;
import java.util.Arrays;

/**
 * @author ci010
 */
public class ResourcePackImpl implements ResourcePack
{
	private String packName;
	private byte[] hash;
	private long size;
	private boolean isZip;

	public ResourcePackImpl(String packName, byte[] hash, long size, boolean isZip)
	{
		this.packName = packName;
		this.hash = hash;
		this.size = size;
		this.isZip = isZip;
	}

	@Override
	public File getLocation()
	{
		return new File(Core.INSTANCE.getRoot(), "resourcepacks/" + packName);
	}

	@Override
	public String getPath() { return "resourcepacks/" + packName + (isZip ? "zip" : "");}

	@Override
	public String getPackName()
	{
		return packName;
	}

	@Override
	public byte[] getMD5()
	{
		return hash;
	}

	@Override
	public long getSize()
	{
		return size;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ResourcePackImpl that = (ResourcePackImpl) o;

		return Arrays.equals(hash, that.hash);
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(hash);
	}
}
