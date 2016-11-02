package net.launcher.game.mod;

import net.launcher.LaunchElement;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

import java.util.Comparator;

/**
 * @author ci010
 */
public interface Mod extends LaunchElement
{
	Comparator<Mod> VERSION = (o1, o2) ->
	{
		if (o1.getModId().compareTo(o2.getModId()) == 0)
			return o1.getVersion().compareTo(o2.getVersion());
		return Integer.MIN_VALUE;
	};

	Comparator<Mod> MCVERSION = (o1, o2) ->
			o1.getMinecraftVersionRange().getRecommendedVersion().compareTo(o2.getMinecraftVersionRange()
					.getRecommendedVersion());

	String getModId();

	ArtifactVersion getVersion();

	VersionRange getMinecraftVersionRange();

	ModMetaData getMetaData();
}
