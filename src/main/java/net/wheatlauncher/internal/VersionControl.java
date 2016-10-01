package net.wheatlauncher.internal;

import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.ResourceType;
import net.launcher.utils.serial.BiSerializer;
import net.wheatlauncher.Core;
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
				new ArchiveRepository.Builder<Version>(Core.INSTANCE.getArchivesRoot(), "versions",
						BiSerializer.combine((s, m) -> null, (s, m) -> null))
						.registerParser(ResourceType.DIR, file -> null).build();
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
//				if (file.isDirectory() && new File(file, file.getId() + ".json").isFile())
//					versions.add(file.getId());
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
//				String nameProperty = versionDir.getId();
//				if (!storage.contains(nameProperty))
//				{
//					File targetDir = new File(root, nameProperty);
//					try
//					{
//						Files.copy(versionDir.toPath(), targetDir.toPath());
//						storage.add(nameProperty);
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
