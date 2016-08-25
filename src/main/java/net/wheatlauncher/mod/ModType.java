package net.wheatlauncher.mod;

import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * @author ci010
 */
public enum ModType
{
	JAR
			{
				private Pattern zipJar = Pattern.compile("(.+).(zip|jar)$");

				@Override
				public Mod parseFile(File file)
				{
					String[] split = file.getName().split("-");
					String inspectMCVersion, inspectModVersion;
					if (split.length > 2)
					{
						inspectMCVersion = split[1];
						inspectModVersion = split[2];
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
						for (int i = 0; i < arr.length(); i++)
						{
							JSONObject obj = arr.getJSONObject(i);
							String modid = obj.getString("modid");
							String version = obj.getString("version");
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
