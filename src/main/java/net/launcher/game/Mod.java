package net.launcher.game;

import net.launcher.game.mod.ModMetaData;

import java.util.Comparator;
import java.util.List;

/**
 * @author ci010
 */
public interface Mod extends Iterable<Mod.Release>
{
	String getModId();

	Release getRelease(String version);

	List<Release> getReleaseFromMinecraftVersion(String minecraftVersion);

	Release getLatestReleaseFromMinecraftVersion(String minecraftVersion);

	interface Release
	{
		Comparator<Release> COMPARATOR = (o1, o2) ->
		{
			if (o1.getModId().compareTo(o2.getModId()) == 0)
				return o1.getVersion().compareTo(o2.getVersion());
			return Integer.MIN_VALUE;
		};

		String getModId();

		String getVersion();

		String getName();

		String getNickName();

		void setNickName(String nickName);

		ModMetaData getMetaData();
	}
}
