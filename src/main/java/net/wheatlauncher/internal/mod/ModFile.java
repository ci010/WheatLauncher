package net.wheatlauncher.internal.mod;

import net.wheatlauncher.Mod;
import net.wheatlauncher.utils.MD5;
import net.wheatlauncher.utils.resource.ArchiveResource;
import net.wheatlauncher.utils.resource.ResourceType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ci010
 */
public class ModFile extends ArchiveResource<Mod.Release[]>
{
	public ModFile(ResourceType type, byte[] md5, Mod.Release[] modMeta)
	{
		super(type, MD5.toString(md5), modMeta);
	}

	public Set<String> getAllModId()
	{
		HashSet<String> set = new HashSet<>();
		for (Mod.Release meta : super.getContainData())
			set.add(meta.getModId());
		return set;
	}

	public Mod.Release getRelease(String modid)
	{
		for (int i = 0; i < super.getContainData().length; i++)
			if (super.getContainData()[i].getModId().equals(modid))
				return super.getContainData()[i];
		return null;
	}

	@Override
	public Mod.Release[] getContainData()
	{
		Mod.Release[] containData = super.getContainData();
		return Arrays.copyOf(containData, containData.length);
	}
//	public enum Type
//	{
//		JAR
//				{
//					@Override
//					public ModFile parseFile(File file) throws IOException
//					{
//						List<Artifact.Release> meta = new ArrayList<>();
//						JarFile jar = new JarFile(file);
//						try
//						{
//							ZipEntry modInfo = jar.getEntry("mcmod.info");
//							if (modInfo != null)
//							{
//								String modInfoString = IOUtils.toString(jar.getInputStream(modInfo));
//
//								JSONArray arr;
//								if (modInfoString.startsWith("{"))
//									arr = new JSONObject(modInfoString).getJSONArray("modList");
//								else
//									arr = new JSONArray(modInfoString);
//
//								for (int i = 0; i < arr.length(); i++)
//									meta.add(new Artifact(arr.getJSONObject(i)));
//							}
//							Set<Map<String, Object>> set = new HashSet<>();
//
//							for (JarEntry jarEntry : Collections.list(jar.entries()))
//								if (Patterns.CLASS_FILE.matcher(jarEntry.getName()).matches())
//								{
//									set.clear();
//									ClassReader reader = new ClassReader(jar.getInputStream(jarEntry));
//									reader.accept(new RuntimeAnnotation.Visitor(set), 0);
//									for (Map<String, Object> map : set)
//										meta.add(new RuntimeAnnotation(map));
//								}
//						}
//						catch (Exception ignored) {}
//
//						return null;
//					}
//
//					@Override
//					public String getSuffix()
//					{
//						return ".jar";
//					}
//
//					@Override
//					public boolean match(File file)
//					{
//						return Patterns.ZIP_JAR.matcher(file.getName()).matches();
//					}
//
//				},
//		DIR
//				{
//					@Override
//					public ModFile parseFile(File file)
//					{
//						return null;
//					}
//
//					@Override
//					public String getSuffix()
//					{
//						return "";
//					}
//
//					@Override
//					public boolean match(File file)
//					{
//						return file.isDirectory();
//					}
//				};
//
//		public abstract ModFile parseFile(File file) throws IOException;
//
//		public abstract String getSuffix();
//
//		public abstract boolean match(File file);
//	}
}
