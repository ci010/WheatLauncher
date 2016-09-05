package net.wheatlauncher;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author ci010
 */
public interface Mod extends Iterable<Mod.Meta>
{
	String getModId();

	Meta getMeta(String version);

	List<Meta> getMetaFromMinecraftVersion(String minecraftVersion);

	Meta getLatestMetaFromMinecraftVersion(String minecraftVersion);

	interface Meta
	{
		//String
		String DESCRIPTION = "description", UPDATE_JSON = "updateJSON", URL = "url", LOGO = "logoFile", CREDITS = "credits",
				PARENT = "parent", DEPENDENCIES = "dependencies", ACCEPTABLE_REMOTE_VERSION = "acceptableRemoteVersions",
				ACCEPTABLE_SAVE_VERSION = "acceptableSaveVersions", FINGURPRINT = "certificateFingerprint";

		//boolean
		String CLIENT_ONLY = "clientSideOnly", SEVER_ONLY = "severSideOnly";

		//String[]
		String AUTHOR_LIST = "author_list", SCREENSHOT = "screenShot";

		Comparator<Meta> COMPARATOR = (o1, o2) -> {
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
