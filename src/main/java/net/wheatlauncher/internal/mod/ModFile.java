package net.wheatlauncher.internal.mod;

import jdk.internal.org.objectweb.asm.ClassReader;
import net.wheatlauncher.Mod;
import net.wheatlauncher.internal.mod.meta.ModInfo;
import net.wheatlauncher.internal.mod.meta.RuntimeAnnotation;
import net.wheatlauncher.utils.MD5;
import net.wheatlauncher.utils.Patterns;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author ci010
 */
public class ModFile implements Iterable<Mod.Release>
{
	private Type type;
	private String md5;
	private Mod.Release[] modMeta;

	public ModFile(Type type, byte[] md5, Mod.Release[] modMeta)
	{
		this.type = type;
		this.md5 = MD5.toString(md5);
		this.modMeta = modMeta;
	}

	public Set<String> getAllModId()
	{
		HashSet<String> set = new HashSet<>();
		for (Mod.Release meta : modMeta)
			set.add(meta.getModId());
		return set;
	}

	public Mod.Release getRelease(String modid)
	{
		for (int i = 0; i < modMeta.length; i++)
			if (modMeta[i].getModId().equals(modid))
				return modMeta[i];
		return null;
	}

	public String getMd5()
	{
		return md5;
	}

	public Type getType()
	{
		return type;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ModFile metas = (ModFile) o;

		return md5.equals(metas.md5);
	}

	@Override
	public int hashCode()
	{
		return md5.hashCode();
	}

	@Override
	public Iterator<Mod.Release> iterator()
	{
		return Arrays.asList(modMeta).iterator();
	}

	public enum Type
	{
		JAR
				{
					@Override
					public ModFile parseFile(File file) throws IOException
					{
						List<Mod.Release> meta = new ArrayList<>();
						JarFile jar = new JarFile(file);
						try
						{
							ZipEntry modInfo = jar.getEntry("mcmod.info");
							if (modInfo != null)
							{
								String modInfoString = IOUtils.toString(jar.getInputStream(modInfo));

								JSONArray arr;
								if (modInfoString.startsWith("{"))
									arr = new JSONObject(modInfoString).getJSONArray("modList");
								else
									arr = new JSONArray(modInfoString);

								for (int i = 0; i < arr.length(); i++)
									meta.add(new ModInfo(arr.getJSONObject(i)));
							}
							Set<Map<String, Object>> set = new HashSet<>();

							for (JarEntry jarEntry : Collections.list(jar.entries()))
								if (Patterns.CLASS_FILE.matcher(jarEntry.getName()).matches())
								{
									set.clear();
									ClassReader reader = new ClassReader(jar.getInputStream(jarEntry));
									reader.accept(new RuntimeAnnotation.Visitor(set), 0);
									for (Map<String, Object> map : set)
										meta.add(new RuntimeAnnotation(map));
								}
						}
						catch (Exception ignored) {}

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
						return Patterns.ZIP_JAR.matcher(file.getName()).matches();
					}

				},
		DIR
				{
					@Override
					public ModFile parseFile(File file)
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

		public abstract ModFile parseFile(File file) throws IOException;

		public abstract String getSuffix();

		public abstract boolean match(File file);
	}
}
