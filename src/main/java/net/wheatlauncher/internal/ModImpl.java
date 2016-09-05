package net.wheatlauncher.internal;

import net.wheatlauncher.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author ci010
 */
public class ModImpl implements Mod
{
	private String modId;

	private Map<String, Meta> modVersionToToken = new HashMap<>();
	private Map<String, List<Meta>> mcVersionToMeta = new HashMap<>();

	public ModImpl(String modId)
	{
		this.modId = modId;
	}

	public void register(Meta modMeta)
	{
		if (modVersionToToken.containsKey(modMeta.getVersion()))
		{
			//add to pending
		}
		else
			modVersionToToken.put(modMeta.getVersion(), modMeta);
	}

	public String getModId()
	{
		return modId;
	}

	@Override
	public Meta getMeta(String version)
	{
		return modVersionToToken.get(version);
	}

	@Override
	public List<Meta> getMetaFromMinecraftVersion(String minecraftVersion)
	{
		return mcVersionToMeta.get(minecraftVersion);
	}

	@Override
	public Meta getLatestMetaFromMinecraftVersion(String minecraftVersion)
	{
		List<Meta> metas = mcVersionToMeta.get(minecraftVersion);
		return metas.get(metas.size() - 1);
	}

	@Override
	public Iterator<Meta> iterator()
	{
		return modVersionToToken.values().iterator();
	}
}
