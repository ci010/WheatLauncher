package net.launcher.game.forge;

import net.launcher.utils.StringUtils;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.util.Map;

/**
 * @author ci010
 */
class MetaDataImpl implements ForgeModMetaData
{
	private String modId = StringUtils.EMPTY;
	private String description = StringUtils.EMPTY;
	private String updateJSON = StringUtils.EMPTY;
	private String url = StringUtils.EMPTY;

	private String logoFile;
	private String[] authorList;
	private String[] screenshots;
	private String credits = StringUtils.EMPTY;
	private String parent = StringUtils.EMPTY;
	private String version = StringUtils.EMPTY,
			name = StringUtils.EMPTY,
			mcVersion = StringUtils.EMPTY,
			fingerprint = StringUtils.EMPTY,
			dependencies = StringUtils.EMPTY,
			acceptableRemoteVersions = StringUtils.EMPTY,
			acceptableSaveVersions = StringUtils.EMPTY;

	private boolean clientOnly, severOnly;

	private String[] collapsed = new String[4];

	MetaDataImpl() {}

	MetaDataImpl(String modId, String description, String updateJSON, String url, String logoFile, String[] authorList,
				 String[] screenshots, String credits, String parent, String version, String name, String mcVersion,
				 String fingerprint, String dependencies, String acceptableRemoteVersions,
				 String acceptableSaveVersions, boolean clientOnly, boolean severOnly)
	{
		this.modId = modId;
		this.description = description;
		this.updateJSON = updateJSON;
		this.url = url;
		this.logoFile = logoFile;
		this.authorList = authorList;
		this.screenshots = screenshots;
		this.credits = credits;
		this.parent = parent;
		this.version = version;
		this.name = name;
		this.mcVersion = mcVersion;
		this.fingerprint = fingerprint;
		this.dependencies = dependencies;
		this.acceptableRemoteVersions = acceptableRemoteVersions;
		this.acceptableSaveVersions = acceptableSaveVersions;
		this.clientOnly = clientOnly;
		this.severOnly = severOnly;
	}

	void loadFromModInfo(JSONObject object)
	{
		putModID(object.optString("modid"));
		putJson(object.optString("updateJSON"));
		putVersion(object.optString("version"));
		putName(object.optString("name"));

		description = object.optString("description");
		url = object.optString("url");
		logoFile = object.optString("logoFile");
		credits = object.optString("credits");
		parent = object.optString("parent");

		JSONArray lst = object.optJSONArray("author_list");
		if (lst != null && lst.length() > 1)
		{
			authorList = new String[lst.length()];
			for (int i = 0; i < lst.length(); i++)
				authorList[i] = lst.getString(i);
		}
		else authorList = new String[0];

		lst = object.optJSONArray("screenshots");
		if (lst != null && lst.length() > 1)
		{
			screenshots = new String[lst.length()];
			for (int i = 0; i < lst.length(); i++)
				screenshots[i] = lst.getString(i);
		}
		else screenshots = new String[0];
	}

	private void putModID(String modid)
	{
		if (StringUtils.isEmpty(modid)) return;
		if (StringUtils.isEmpty(this.modId))
			this.modId = modid;
		else collapsed[0] = modid;
	}

	private void putVersion(String v)
	{
		if (StringUtils.isEmpty(v)) return;
		if (StringUtils.isEmpty(this.version))
			this.version = v;
		else collapsed[1] = v;
	}

	private void putJson(String v)
	{
		if (StringUtils.isEmpty(v)) return;
		if (StringUtils.isEmpty(this.updateJSON))
			this.updateJSON = v;
		else collapsed[2] = v;
	}

	private void putName(String v)
	{
		if (StringUtils.isEmpty(v)) return;
		if (StringUtils.isEmpty(this.name))
			this.name = v;
		else collapsed[3] = v;
	}

	public void loadFromAnnotationMap(Map<String, Object> map)
	{
		map.forEach((s, o) ->
		{
			switch (s)
			{
				case "modid": putModID((String) o); break;
				case "version": putVersion((String) o); break;
				case "updateJSON": putJson((String) o); break;

				case "name": putName((String) o); break;
				case "certificateFingerprint": fingerprint = (String) o; break;
				case "dependencies": dependencies = (String) o; break;
				case "acceptableRemoteVersions": acceptableRemoteVersions = (String) o; break;
				case "acceptableSaveVersions": acceptableSaveVersions = (String) o; break;
				case "clientSideOnly": clientOnly = (boolean) o; break;
				case "severSideOnly": severOnly = (boolean) o; break;
				case "acceptedMinecraftVersions":
					mcVersion = (String) o;
					break;
			}
		});
	}

	@Override
	public String getModId()
	{
		return modId;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public String getUpdateJSON()
	{
		return updateJSON;
	}

	@Override
	public String getUrl()
	{
		return url;
	}

	@Override
	public String getLogoFile()
	{
		return logoFile;
	}

	@Override
	public String[] getAuthorList()
	{
		return authorList;
	}

	@Override
	public String getCredits()
	{
		return credits;
	}

	@Override
	public String getParent()
	{
		return parent;
	}

	@Override
	public String[] getScreenshots()
	{
		return screenshots;
	}

	@Override
	public String getAcceptMinecraftVersion()
	{
		return mcVersion;
	}

	@Override
	public String getFingerprint()
	{
		return fingerprint;
	}

	@Override
	public String getDependencies()
	{
		return dependencies;
	}

	@Override
	public String acceptableRemoteVersions()
	{
		return acceptableRemoteVersions;
	}

	@Override
	public String acceptableSaveVersions()
	{
		return acceptableSaveVersions;
	}

	@Override
	public boolean isClientOnly()
	{
		return clientOnly;
	}

	@Override
	public boolean isSeverOnly()
	{
		return severOnly;
	}

	@Override
	public String getAlternativeName()
	{
		return collapsed[3];
	}

	@Override
	public String getCollapsedVersion()
	{
		return collapsed[1];
	}
}
