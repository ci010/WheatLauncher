package net.wheatlauncher.mod;

import jdk.internal.org.objectweb.asm.ClassReader;
import net.wheatlauncher.mod.meta.ModInfo;
import net.wheatlauncher.mod.meta.RuntimeAnnotation;
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
public class Mod implements Iterable<ModMeta>
{
	private Type type;
	private File file;
	private ModMeta[] modMeta;

	private Mod(Type type, File file, ModMeta[] modMeta)
	{
		this.type = type;
		this.file = file;
		this.modMeta = modMeta;
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
					@Override
					public Mod parseFile(File file) throws IOException
					{
						List<ModMeta> meta = new ArrayList<>();
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

		public abstract Mod parseFile(File file) throws IOException;

		public abstract String getSuffix();

		public abstract boolean match(File file);
	}
}
