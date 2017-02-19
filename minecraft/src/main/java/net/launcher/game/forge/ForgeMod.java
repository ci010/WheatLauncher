package net.launcher.game.forge;

import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.*;
import net.launcher.utils.StringUtils;

import java.util.Comparator;

/**
 * @author ci010
 */
public class ForgeMod
{
	private ForgeModMetaData metaData;
	private VersionRange range;
	private ArtifactVersion version;

	public ForgeMod(ForgeModMetaData metaData)
	{
		this.metaData = metaData;
		this.range = parseMCVersionRange(metaData.getAcceptMinecraftVersion());
		this.version = new DefaultArtifactVersion(metaData.getModId(), metaData.getVersion());
	}

	public String getModId()
	{
		return metaData.getModId();
	}

	public ArtifactVersion getVersion()
	{
		return version;
	}

	public VersionRange getMinecraftVersionRange()
	{
		return range;
	}

	public ForgeModMetaData getMetaData()
	{
		return metaData;
	}

	@Override
	public String toString()
	{
		return "ForgeMod{" +
				"modId='" + getModId() + '\'' +
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

	Comparator<ForgeMod> VERSION = (o1, o2) ->
	{
		if (o1.getModId().compareTo(o2.getModId()) == 0)
			return o1.getVersion().compareTo(o2.getVersion());
		return Integer.MIN_VALUE;
	};

	Comparator<ForgeMod> MCVERSION = (o1, o2) ->
			o1.getMinecraftVersionRange().getRecommendedVersion().compareTo(o2.getMinecraftVersionRange()
					.getRecommendedVersion());

}
