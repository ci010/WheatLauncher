package net.wheatlauncher.mod;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author ci010
 */
public class Mod implements Iterable<ModMeta>
{
	private ModType type;
	private File file;
	private ModMeta[] modMeta;

	Mod(ModType type, File file, ModMeta[] metas)
	{
		this.type = type;
		this.file = file;
		this.modMeta = metas;
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
}
