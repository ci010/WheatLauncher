package net.wheatlauncher.mod;

import java.util.Comparator;
import java.util.Set;

/**
 * @author ci010
 */
public interface ModMeta
{
	//String
	String DESCRIPTION = "description", UPDATE_JSON = "updateJSON", URL = "url", LOGO = "logoFile", CREDITS = "credits",
			PARENT = "parent", DEPENDENCIES = "dependencies", ACCEPTABLE_REMOTE_VERSION = "acceptableRemoteVersions",
			ACCEPTABLE_SAVE_VERSION = "acceptableSaveVersions", FINGURPRINT = "certificateFingerprint";

	//boolean
	String CLIENT_ONLY = "clientSideOnly", SEVER_ONLY = "severSideOnly";

	//String[]
	String AUTHOR_LIST = "author_list", SCREENSHOT = "screenShot";

	Comparator<ModMeta> COMPARATOR = (o1, o2) -> {
		if (o1.getModId().compareTo(o2.getModId()) == 0)
			return o1.getVersion().compareTo(o2.getVersion());
		return Integer.MIN_VALUE;
	};

	String getModId();

	String getVersion();

	String getName();

	Set<String> getAllSupportMinecraftVersions();

	Object getMeta(String s);

	ModMeta merge(ModMeta meta);

	//	public ModMeta setModid(String modid)
//	{
//		this.modId = modid;
//		return this;
//	}
//
//	public ModMeta setName(String name)
//	{
//		this.name = name;
//		return this;
//	}
//
//	public ModMeta setVersion(String version)
//	{
//		this.version = version;
//		return this;
//	}
//
//	public ModMeta setDescription(String description)
//	{
//		this.description = description;
//		return this;
//	}
//
//	public ModMeta setUpdateJson(String json)
//	{
//		this.updateJSON = json;
//		return this;
//	}
//
//	public ModMeta setLogoFile(String logoFile)
//	{
//		this.logoFile = logoFile;
//		return this;
//	}
//
//	public ModMeta setCredit(String credit)
//	{
//		this.credits = credit;
//		return this;
//	}
//
//	public ModMeta setScreenShot(String[] screenShot)
//	{
//		this.screenshots = screenShot;
//		return this;
//	}
//
//	public ModMeta setMcVersion(String mcVersion)
//	{
//		this.mcVersion = mcVersion;
//		return this;
//	}
//
//	public ModMeta setAuthorList(String[] authorList)
//	{
//		this.authorList = authorList;
//		return this;
//	}
//
//
//	public ModMeta setUrl(String url)
//	{
//		this.url = url;
//		return this;
//	}


}
