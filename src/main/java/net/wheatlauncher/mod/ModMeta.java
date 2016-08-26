package net.wheatlauncher.mod;

import javafx.util.Builder;
import net.wheatlauncher.utils.JsonSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.util.*;

/**
 * @author ci010
 */
public class ModMeta implements Cloneable
{
//	public static final Comparator<ModMeta> VERSION = (o1, o2) ->
//			o1.getComparableVersionCache().compareTo(o2.getComparableVersionCache());

	private Mod mod;

	private String modId;
	private String name;
	private String mcVersion = "";
	private String description = "";
	private String updateJSON = "";
	private String url = "";

	private String logoFile = "";
	private String version = "";
	private String[] authorList = new String[0];
	private String credits = "";
	private String parent = "";
	private String[] screenshots = new String[0];

	private ModMeta(Mod mod)
	{
		this.mod = mod;
	}

	public ModMeta(ModMeta meta)
	{
		this.mod = meta.mod;
		this.modId = meta.modId;
		this.name = meta.name;
		this.description = meta.description;
		this.updateJSON = meta.updateJSON;
		this.logoFile = meta.logoFile;
		this.version = meta.version;
		this.authorList = Arrays.copyOf(meta.authorList, meta.authorList.length);
		this.credits = meta.credits;
		this.parent = meta.parent;
		this.screenshots = Arrays.copyOf(meta.screenshots, meta.screenshots.length);
	}

	public String getModId()
	{
		return modId;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public String getUpdateJSON()
	{
		return updateJSON;
	}

	public String getLogoFile()
	{
		return logoFile;
	}

	public String getVersion()
	{
		return version;
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

	public Mod getMod()
	{
		return mod;
	}

	public String getMcVersion()
	{
		return mcVersion;
	}

	public String getUrl()
	{
		return url;
	}

	public void loadFromJsonObject(JSONObject object)
	{

	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ModMeta modMeta = (ModMeta) o;

		if (modId != null ? !modId.equals(modMeta.getModId()) : modMeta.getModId() != null) return false;
		if (version != null ? !version.equals(modMeta.getVersion()) : modMeta.getVersion() != null) return false;
		return mod != null ? mod.equals(modMeta.getMod()) : modMeta.getMod() == null;
	}

	@Override
	public int hashCode()
	{
		int result = modId != null ? modId.hashCode() : 0;
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (mod != null ? mod.hashCode() : 0);
		return result;
	}


	public static class MetaBuilder implements Builder<ModMeta>
	{
		private ModMeta cache;

		public MetaBuilder(Mod mod)
		{
			this.cache = new ModMeta(mod);
		}

		public MetaBuilder setModid(String modid)
		{
			cache.modId = modid;
			return this;
		}

		public MetaBuilder setName(String name)
		{
			cache.name = name;
			return this;
		}

		public MetaBuilder setVersion(String version)
		{
			cache.version = version;
			return this;
		}

		public MetaBuilder setDescription(String description)
		{
			cache.description = description;
			return this;
		}

		public MetaBuilder setUpdateJson(String json)
		{
			cache.updateJSON = json;
			return this;
		}

		public MetaBuilder setLogoFile(String logoFile)
		{
			cache.logoFile = logoFile;
			return this;
		}

		public MetaBuilder setCredit(String credit)
		{
			cache.credits = credit;
			return this;
		}

		public MetaBuilder setScreenShot(String[] screenShot)
		{
			cache.screenshots = screenShot;
			return this;
		}

		public MetaBuilder setMcVersion(String mcVersion)
		{
			cache.mcVersion = mcVersion;
			return this;
		}

		public MetaBuilder setAuthorList(String[] authorList)
		{
			cache.authorList = authorList;
			return this;
		}


		public MetaBuilder setUrl(String url)
		{
			cache.url = url;
			return this;
		}

		@Override
		public ModMeta build()
		{
			return new ModMeta(cache);
		}
	}
}
