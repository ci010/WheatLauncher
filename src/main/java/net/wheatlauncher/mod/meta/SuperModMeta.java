package net.wheatlauncher.mod.meta;

import net.wheatlauncher.mod.ModMeta;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ci010
 */
public class SuperModMeta implements ModMeta
{
	private ModMeta info, annoInfo;
	private Set<String> supportVersions;

	SuperModMeta(ModInfo info, RuntimeAnnotation annoInfo)
	{
		this.info = info;
		this.annoInfo = annoInfo;
		this.supportVersions = new HashSet<>();
		this.supportVersions.addAll(info.getAllSupportMinecraftVersions());
		this.supportVersions.addAll(annoInfo.getAllSupportMinecraftVersions());
	}

	@Override
	public String getModId()
	{
		String modId = info.getModId();
		if (modId == null)
			return annoInfo.getModId();
		return modId;
	}

	@Override
	public String getVersion()
	{
		String version = info.getVersion();
		if (version == null)
			return annoInfo.getVersion();
		return version;
	}

	@Override
	public String getName()
	{
		String name = info.getName();
		if (name == null)
			return annoInfo.getName();
		return name;
	}

	@Override
	public Set<String> getAllSupportMinecraftVersions()
	{
		return supportVersions;
	}

	@Override
	public Object getMeta(String s)
	{
		Object meta = info.getMeta(s);
		if (meta == null)
			return annoInfo.getMeta(s);
		return meta;
	}

	@Override
	public ModMeta merge(ModMeta meta)
	{
		return null;
	}
}
