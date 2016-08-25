package net.wheatlauncher.mod;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.io.File;
import java.util.*;

/**
 * @author ci010
 */
public class ModRepository implements ChangeListener<MinecraftDirectory>
{
	private Set<Mod> allMod = new HashSet<>();
	private Map<String, Entry> modIdToEntry = new HashMap<>();

	public Entry getModEntry(String modId)
	{
		return modIdToEntry.get(modId);
	}

	public void register(Mod mod)
	{
		if (allMod.contains(mod))
			return;
		allMod.add(mod);
		for (ModMeta modMeta : mod)
			if (modIdToEntry.containsKey(modMeta.getModId()))
				modIdToEntry.get(modMeta.getModId()).register(modMeta);
			else
			{
				Entry entry = new Entry(modMeta.getModId());
				entry.register(modMeta);
				modIdToEntry.put(modMeta.getModId(), entry);
			}
	}

	@Override
	public void changed(ObservableValue<? extends MinecraftDirectory> observable, MinecraftDirectory oldValue, MinecraftDirectory newValue)
	{
		File mods = new File(newValue.getRoot(), "mods");
		if (mods.isDirectory())
		{
			File[] files = mods.listFiles();
			if (files != null)
			{

			}
		}
	}

	public class Entry
	{
		private String modId;

		private Map<String, List<ModMeta>> mcVersionIndex = new HashMap<>();
		private Map<String, ModMeta> modVersionToToken = new HashMap<>();

		public Entry(String modId)
		{
			this.modId = modId;
		}

		private void register(ModMeta modMeta)
		{
			if (modVersionToToken.containsKey(modMeta.getVersion()))
			{
				//add to pending
			}
			else
				modVersionToToken.put(modMeta.getVersion(), modMeta);
			if (!modMeta.getMcVersion().equals(""))
				if (mcVersionIndex.containsKey(modMeta.getMcVersion()))
				{
					List<ModMeta> list = mcVersionIndex.get(modMeta.getMcVersion());
					list.add(modMeta);
//				Collections.sort(list, Mod.VERSION);
				}
				else
				{
					ArrayList<ModMeta> lst = new ArrayList<>();
					lst.add(modMeta);
					mcVersionIndex.put(modMeta.getMcVersion(), lst);
				}
		}

		public ModMeta getFromModVersion(String version)
		{
			return modVersionToToken.get(version);
		}

		public String getModId()
		{
			return modId;
		}

		public List<ModMeta> getAllFromMCVersion(String mcVersion)
		{
			return Collections.unmodifiableList(mcVersionIndex.get(mcVersion));
		}

		public ModMeta getLatestFromMCVersion(String mcVersion)
		{
			List<ModMeta> locations = mcVersionIndex.get(mcVersion);
			return locations.get(locations.size() - 1);
		}
	}
}
