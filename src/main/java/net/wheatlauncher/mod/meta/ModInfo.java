package net.wheatlauncher.mod.meta;

import net.wheatlauncher.mod.ModMeta;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.util.Collections;
import java.util.Set;

/**
 * @author ci010
 */
public class ModInfo implements ModMeta
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
		name = object.optString("name", null);
		description = object.optString(DESCRIPTION, null);
		updateJSON = object.optString(UPDATE_JSON, null);
		url = object.optString(URL, null);
		logoFile = object.optString(LOGO, null);
		version = object.optString("version", null);
		credits = object.optString(CREDITS, null);
		parent = object.optString(PARENT, null);

		JSONArray lst = object.optJSONArray(AUTHOR_LIST);
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
	public Set<String> getAllSupportMinecraftVersions()
	{
		return Collections.emptySet();
	}

	@Override
	public Object getMeta(String s)
	{
		switch (s)
		{
			case DESCRIPTION: return description;
			case UPDATE_JSON: return updateJSON;
			case URL: return url;
			case LOGO: return logoFile;
			case CREDITS: return credits;
			case PARENT: return parent;
			case AUTHOR_LIST: return authorList;
			case SCREENSHOT: return screenshots;
		}
		return null;
	}

	@Override
	public ModMeta merge(ModMeta meta)
	{
		if (meta == this)
			return this;
		if (COMPARATOR.compare(this, meta) == 0)
		{
			if (meta instanceof ModInfo) //this should not happen... it's better to just ignore it than throw exception
				return this;
			if (meta instanceof RuntimeAnnotation)
				return new SuperModMeta(this, (RuntimeAnnotation) meta);
		}
		return null;
	}
}
