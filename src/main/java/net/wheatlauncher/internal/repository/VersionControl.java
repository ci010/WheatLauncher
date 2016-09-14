package net.wheatlauncher.internal.repository;

import net.wheatlauncher.Core;
import net.wheatlauncher.utils.JsonSerializer;
import net.wheatlauncher.utils.resource.ArchiveRepository;
import net.wheatlauncher.utils.resource.ResourceType;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.version.Version;

import java.util.Set;

/**
 * @author ci010
 */
public class VersionControl //implements MinecraftRepository<String>
{
	private ArchiveRepository<Version> versionArchiveRepository;

	public VersionControl()
	{
		this.versionArchiveRepository =
				new ArchiveRepository.Builder<>(Core.INSTANCE.getArchivesRoot(), "versions",
						new JsonSerializer<Version>()
						{
							@Override
							public Version deserialize(JSONObject jsonObject)
							{
								return null;
							}

							@Override
							public JSONObject serialize(Version data)
							{
								return null;
							}
						})
						.registerParser(ResourceType.DIR, file -> {
							return null;
						}).build();
	}

	public boolean contains(Version version)
	{
		return false;
	}

	public Set<Version> getAllVersion()
	{
		return null;
	}
//	private File root;
//	private Set<String> storage;
//
//	private Set<String> getAllVesions()
//	{
//		Set<String> versions = new TreeSet<>();
//
//		File[] subdirs = root.listFiles();
//		if (subdirs != null)
//			for (File file : subdirs)
//				if (file.isDirectory() && new File(file, file.getName() + ".json").isFile())
//					versions.add(file.getName());
//		return Collections.unmodifiableSet(versions);
//	}
//
//	@Override
//	public void changed(ObservableValue<? extends MinecraftDirectory> observable, MinecraftDirectory oldValue, MinecraftDirectory newValue)
//	{
//		Core.INSTANCE.getService().submit(() -> {
//			Set<String> versions = Versions.getVersions(newValue);
//			for (String s : versions)
//			{
//				File versionDir = newValue.getVersion(s);
//				String name = versionDir.getName();
//				if (!storage.contains(name))
//				{
//					File targetDir = new File(root, name);
//					try
//					{
//						Files.copy(versionDir.toPath(), targetDir.toPath());
//						storage.add(name);
//					}
//					catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//
//	}
//
//	@Override
//	public Set<String> getAllKey()
//	{
//		return storage;
//	}
//
//	@Override
//	public String get(String key)
//	{
//		if (storage.contains(key))
//			return key;
//		return null;
//	}
}
