package net.wheatlauncher.mod;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.wheatlauncher.utils.JsonSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
		if (mod == null)
			return;
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
			if (files != null && files.length > 0)
				for (File file : files)
					for (Mod.Type modType : Mod.Type.values())
						if (modType.match(file))
							try {register(modType.parseFile(file));}
							catch (IOException ignored) {}
		}
	}

	public class Entry
	{
		private String modId;

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
		}

		public ModMeta getFromModVersion(String version)
		{
			return modVersionToToken.get(version);
		}

		public String getModId()
		{
			return modId;
		}

//		public ModMeta getLatestFromMCVersion(String mcVersion)
//		{
//			List<ModMeta> locations = mcVersionIndex.get(mcVersion);
//			return locations.get(locations.size() - 1);
//		}
	}

	public static final JsonSerializer<ModRepository> SERIALIZER = new JsonSerializer<ModRepository>()
	{
		@Override
		public ModRepository deserialize(JSONObject jsonObject)
		{
			return null;
		}

		@Override
		public JSONObject serialize(ModRepository data)
		{
			return null;
		}
	};
}
