package net.wheatlauncher.internal.repository;

import javafx.beans.value.ObservableValue;
import net.wheatlauncher.Core;
import net.wheatlauncher.MinecraftRepository;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.parsing.Versions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author ci010
 */
public class VesionRepository implements MinecraftRepository<String>
{
	private File root;
	private Set<String> storage;

	private Set<String> getAllVesions()
	{
		Set<String> versions = new TreeSet<>();

		File[] subdirs = root.listFiles();
		if (subdirs != null)
			for (File file : subdirs)
				if (file.isDirectory() && new File(file, file.getName() + ".json").isFile())
					versions.add(file.getName());
		return Collections.unmodifiableSet(versions);
	}

	@Override
	public void changed(ObservableValue<? extends MinecraftDirectory> observable, MinecraftDirectory oldValue, MinecraftDirectory newValue)
	{
		Core.INSTANCE.getService().submit(() -> {
			Set<String> versions = Versions.getVersions(newValue);
			for (String s : versions)
			{
				File versionDir = newValue.getVersion(s);
				String name = versionDir.getName();
				if (!storage.contains(name))
				{
					File targetDir = new File(root, name);
					try
					{
						Files.copy(versionDir.toPath(), targetDir.toPath());
						storage.add(name);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

	}

	@Override
	public Set<String> getAllKey()
	{
		return storage;
	}

	@Override
	public String get(String key)
	{
		if (storage.contains(key))
			return key;
		return null;
	}
}
