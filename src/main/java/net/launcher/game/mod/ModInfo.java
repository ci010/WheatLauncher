package net.launcher.game.mod;

import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

/**
 * @author ci010
 */
public class ModInfo
{
	private String modId;
	private String name;
	private String description;
	private String updateJSON;
	private String url;

	private String logoFile;
	private String version;
	private String[] authorList;
	private String credits;
	private String parent;
	private String[] screenshots;

	public ModInfo(JSONObject object)
	{
		modId = object.optString("modid", null);
		name = object.optString("nameProperty", null);
		description = object.optString("description", null);
		updateJSON = object.optString("updateJSON", null);
		url = object.optString("url", null);
		logoFile = object.optString("logoFile", null);
		version = object.optString("version", null);
		credits = object.optString("credits", null);
		parent = object.optString("parent", null);

		JSONArray lst = object.optJSONArray("author_list");
		if (lst != null && lst.length() > 1)
		{
			authorList = new String[lst.length()];
			for (int i = 0; i < lst.length(); i++)
				authorList[i] = lst.getString(i);
		}

		lst = object.optJSONArray("screenshots");
		if (lst != null && lst.length() > 1)
		{
			screenshots = new String[lst.length()];
			for (int i = 0; i < lst.length(); i++)
				screenshots[i] = lst.getString(i);
		}
	}

	public String getDescription()
	{
		return description;
	}

	public String getUpdateJSON()
	{
		return updateJSON;
	}

	public String getUrl()
	{
		return url;
	}

	public String getLogoFile()
	{
		return logoFile;
	}

	public String[] getAuthorList()
	{
		return authorList;
	}

	public String getCredits()
	{
		return credits;
	}

	public String getParent()
	{
		return parent;
	}

	public String[] getScreenshots()
	{
		return screenshots;
	}

	public String getModId()
	{
		return modId;
	}

	public String getVersion()
	{
		return version;
	}

	public String getName()
	{
		return name;
	}

	public static ModInfo empty()
	{
		return new ModInfo(new JSONObject());
	}
}
