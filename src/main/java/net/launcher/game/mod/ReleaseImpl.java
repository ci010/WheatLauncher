package net.launcher.game.mod;

import net.launcher.game.Mod;
import net.launcher.utils.StringUtils;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @author ci010
 */
public class ReleaseImpl implements Mod.Release
{
	private String nickName;
	private ModMetaData metaData;
	private VersionRange range;

	public ReleaseImpl(ModMetaData metaData)
	{
		this.nickName = "";
		this.metaData = metaData;
		this.range = parseMCVersionRange(metaData.getAcceptMinecraftVersion());
	}

	@Override
	public String getModId()
	{
		return metaData.getModId();
	}

	@Override
	public String getVersion()
	{
		return metaData.getVersion();
	}

	@Override
	public String getNickName()
	{
		return nickName;
	}

	@Override
	public VersionRange getAcceptVersionRange()
	{
		return range;
	}

	@Override
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	@Override
	public ModMetaData getMetaData()
	{
		return metaData;
	}

	@Override
	public String toString()
	{
		return "ReleaseImpl{" +
				"modId='" + getModId() + '\'' +
				", nickName='" + nickName + '\'' +
				", version='" + getVersion() + '\'' +
				",\n metaData=" + metaData +
				'}';
	}

	public static VersionRange parseMCVersionRange(String mcVersionString)
	{
		if ("[1.8.8]".equals(mcVersionString))
			mcVersionString = "[1.8.8,1.8.9]"; // MC 1.8.8 and 1.8.9 is forward SRG compatible so accept these versions by default.
		if ("[1.9.4]".equals(mcVersionString) ||
				"[1.9,1.9.4]".equals(mcVersionString) ||
				"[1.9.4,1.10)".equals(mcVersionString) ||
				"[1.10]".equals(mcVersionString))
			mcVersionString = "[1.9.4,1.10.2]";
		if (StringUtils.isNotEmpty(mcVersionString))
			try
			{
				return VersionParser.parseRange(mcVersionString);
			}
			catch (InvalidVersionSpecificationException e)
			{
				return null;
			}
		else return null;
	}
}
