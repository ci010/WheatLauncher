package net.launcher;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
		//String
		String DESCRIPTION = "description", UPDATE_JSON = "updateJSON", URL = "url", LOGO = "logoFile", CREDITS = "credits",
				PARENT = "parent", DEPENDENCIES = "dependencies", ACCEPTABLE_REMOTE_VERSION = "acceptableRemoteVersions",
				ACCEPTABLE_SAVE_VERSION = "acceptableSaveVersions", FINGURPRINT = "certificateFingerprint";

		//boolean
		String CLIENT_ONLY = "clientSideOnly", SEVER_ONLY = "severSideOnly";

		//String[]
		String AUTHOR_LIST = "author_list", SCREENSHOT = "screenShot";

		Comparator<Release> COMPARATOR = (o1, o2) ->
		{
			if (o1.getModId().compareTo(o2.getModId()) == 0)
				return o1.getVersion().compareTo(o2.getVersion());
			return Integer.MIN_VALUE;
		};

		String getModId();

		String getVersion();

		String getName();

		Set<String> getAllSupportMinecraftVersions();

		Object getMeta(String s);
	}
}
