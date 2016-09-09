package net.wheatlauncher.internal.repository;

import javafx.beans.value.ObservableValue;
import net.wheatlauncher.MinecraftRepository;
import net.wheatlauncher.Mod;
import net.wheatlauncher.internal.mod.ModFile;
import net.wheatlauncher.internal.mod.ModImpl;
import net.wheatlauncher.utils.JsonSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ci010
 */
public class ModRepository implements MinecraftRepository<Mod>
{
	private Set<ModFile> allModFile = new HashSet<>();
	private Map<String, ModImpl> modIdToMod = new HashMap<>();

	private File root;

	public ModRepository(File root)
	{
		this.root = root;
	}

	private boolean register(ModFile file)
	{
		if (file == null)
			return false;
		if (allModFile.contains(file))
			return false;
		allModFile.add(file);
		for (Mod.Release modMeta : file)
			if (modIdToMod.containsKey(modMeta.getModId()))
				modIdToMod.get(modMeta.getModId()).register(modMeta);
			else
			{
				ModImpl entry = new ModImpl(modMeta.getModId());
				entry.register(modMeta);
				modIdToMod.put(modMeta.getModId(), entry);
			}
		return true;
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
					for (ModFile.Type modType : ModFile.Type.values())
						if (modType.match(file))
							try
							{
								ModFile modFile = modType.parseFile(file);
								if (register(modFile))
								{
									File dir = new File(root, modFile.getMd5());
									if (!dir.exists()) if (!dir.mkdir())
										throw new IOException("Could not create directory for file " + file);
									Files.copy(file.toPath(), new File(dir, file.getName()).toPath());
								}
							}
							catch (IOException e) {e.printStackTrace();}
		}
	}

	@Override
	public Set<String> getAllKey()
	{
		return modIdToMod.keySet();
	}

	@Override
	public Mod get(String key)
	{
		return modIdToMod.get(key);
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
