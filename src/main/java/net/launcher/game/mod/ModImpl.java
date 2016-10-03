package net.launcher.game.mod;

import net.launcher.game.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ModImpl implements Mod
{
	private String modId;

	private Map<String, Release> modVersionToToken = new HashMap<>();
	private Map<String, List<Release>> mcVersionToMeta = new HashMap<>();

	public ModImpl(String modId)
	{
		this.modId = modId;
	}

	public void register(Release modMeta)
	{
		if (modVersionToToken.containsKey(modMeta.getVersion()))
		{
			//onWatch to pending
		}
		else
			modVersionToToken.put(modMeta.getVersion(), modMeta);
	}

	public String getModId()
	{
		return modId;
	}

	@Override
	public Release getRelease(String version)
	{
		return modVersionToToken.get(version);
	}

	@Override
	public String toString()
	{
		return "Artifact{" +
				"modId='" + modId +
				" , versions=[" + modVersionToToken.keySet().stream().collect(Collectors.joining(", ")) + "]" +
				'\'' +
				'}';
	}

	@Override
	public List<Release> getReleaseFromMinecraftVersion(String minecraftVersion)
	{
		return mcVersionToMeta.get(minecraftVersion);
	}

	@Override
	public Release getLatestReleaseFromMinecraftVersion(String minecraftVersion)
	{
		List<Release> metas = mcVersionToMeta.get(minecraftVersion);
		return metas.get(metas.size() - 1);
	}

	@Override
	public Iterator<Release> iterator()
	{
		return modVersionToToken.values().iterator();
	}
}
