package net.launcher.game.mod;

import net.launcher.game.Mod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ModImpl implements Mod
{
	private String modId;

	private Map<String, Release> modVersionToToken = new HashMap<>();
	private Map<String, List<Release>> mcVersionToMeta = new HashMap<>();
	private List<Release> view = new ArrayList<>();

	public ModImpl(String modId)
	{
		this.modId = modId;
	}

	public boolean register(Release modMeta)
	{
		if (modVersionToToken.containsKey(modMeta.getVersion()))
		{

			//onWatch to pending
		}
		else
			modVersionToToken.put(modMeta.getVersion(), modMeta);
		return true;
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
	public List<Release> getAllReleases()
	{
		return view;
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
