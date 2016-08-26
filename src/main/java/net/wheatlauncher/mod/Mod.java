package net.wheatlauncher.mod;

import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * @author ci010
 */
public class Mod implements Iterable<ModMeta>
{
	private Type type;
	private File file;
	private ModMeta[] modMeta;

	private Mod(Type type, File file)
	{
		this.type = type;
		this.file = file;
	}

	public File getFile()
	{
		return file;
	}

	public Set<String> getAllModId()
	{
		HashSet<String> set = new HashSet<>();
		for (ModMeta meta : modMeta)
			set.add(meta.getModId());
		return set;
	}

	public ModMeta getMeta(String modid)
	{
		for (int i = 0; i < modMeta.length; i++)
			if (modMeta[i].getModId().equals(modid))
				return modMeta[i];
		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Mod that = (Mod) o;

		if (that.type != this.type) return false;
		return file.equals(that.file);
	}

	@Override
	public int hashCode()
	{
		int result = type.hashCode();
		result = 31 * result + file.hashCode();
		return result;
	}

	@Override
	public Iterator<ModMeta> iterator()
	{
		return Arrays.asList(modMeta).iterator();
	}

	public enum Type
	{
		JAR
				{
					private Pattern zipJar = Pattern.compile("(.+).(zip|jar)$");

					@Override
					public Mod parseFile(File file)
					{
						Mod mod = new Mod(this, file);

						String[] split = file.getName().split("-");
						boolean singleMod;
						String inspectMCVersion, inspectModVersion, inspectNameOrModId;
						if (split.length > 2)
						{
							inspectNameOrModId = split[0];
							inspectMCVersion = split[1];
							inspectModVersion = split[2];
						}
						else
						{
							split = file.getName().split(" ");
							if (split.length > 2)
							{
								inspectNameOrModId = split[0];
								inspectMCVersion = split[1];
								inspectModVersion = split[2];
							}
						}
						try
						{
							JarFile jar = new JarFile(file);
							ZipEntry modInfo = jar.getEntry("mcmod.info");
							JSONArray arr;
							String modInfoString = IOUtils.toString(jar.getInputStream(modInfo));

							if (modInfoString.startsWith("{"))
								arr = new JSONObject(modInfoString).getJSONArray("modList");
							else
								arr = new JSONArray(modInfoString);
							singleMod = arr.length() == 1;

							if (singleMod)
							{

							}
							else
								for (int i = 0; i < arr.length(); i++)
								{
									JSONObject obj = arr.getJSONObject(i);
									ModMeta.MetaBuilder metaBuilder = new ModMeta.MetaBuilder(mod);
									metaBuilder.setModid(obj.optString("modid"))
											.setName(obj.optString("name"))
											.setDescription(obj.optString("description"))
											.setVersion(obj.optString("version"))
											.setMcVersion(obj.optString("mcVersion"))
											.setUrl(obj.optString("url"))
											.setUpdateJson(obj.optString("updateUrl"))
											.setLogoFile(obj.optString("logoFile"));
									JSONArray authorList = obj.optJSONArray("authorList");
									if (authorList != null)
									{
										String[] strings = new String[authorList.length()];
										for (int j = 0; j < authorList.length(); j++)
											strings[j] = authorList.getString(j);
										metaBuilder.setAuthorList(strings);
									}
									JSONArray screenshots = obj.optJSONArray("screenshots");
								}
						}
						catch (Exception e)
						{
						}
						return null;
					}

					@Override
					public String getSuffix()
					{
						return ".jar";
					}

					@Override
					public boolean match(File file)
					{
						return zipJar.matcher(file.getName()).matches();
					}

				},
		DIR
				{
					@Override
					public Mod parseFile(File file)
					{
						return null;
					}

					@Override
					public String getSuffix()
					{
						return "";
					}

					@Override
					public boolean match(File file)
					{
						return file.isDirectory();
					}
				};

		public abstract Mod parseFile(File file);

		public abstract String getSuffix();

		public abstract boolean match(File file);
	}
}
