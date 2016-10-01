package net.launcher;

import javafx.scene.image.Image;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.ArchiveResource;
import net.launcher.utils.resource.ResourceType;
import net.launcher.utils.serial.BiSerializer;
import net.wheatlauncher.Core;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ci010
 */
class ResourcePackManImpl implements ResourcePackManger
{
	private ArchiveRepository<ResourcePack> archiveRepository;
	private static Image defaultImage = new Image(ResourcePackManImpl.class.getResourceAsStream("/pack.png"));

	@Override
	public Optional<ResourcePack> getResourcePack(String name)
	{
		Optional<ArchiveResource<ResourcePack>> resource = null;
		try
		{
			resource = archiveRepository.findResource(name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if (resource.isPresent()) return Optional.ofNullable(resource.get().getContainData());
		else return Optional.empty();
	}

	@Override
	public Future<File> importResourcePack(File file)
	{
		return null;
	}

	@Override
	public Image getIcon(ResourcePack resourcePack) throws IOException
	{
		Optional<ArchiveResource<ResourcePack>> resource = archiveRepository.findResource(resourcePack.getPackName());
		if (resource.isPresent())
		{
			ArchiveResource<ResourcePack> archiveResource = resource.get();
			File f = archiveRepository.getResourceFile(archiveResource);
			if (archiveResource.getType() == ResourceType.DIR)
			{
				File png = new File(f, "pack.png");
				if (png.isFile()) return new Image(new FileInputStream(png));
			}
			if (archiveResource.getType() == ResourceType.ZIP)
			{
				ZipFile zipFile = new ZipFile(f);
				ZipEntry entry = zipFile.getEntry("pack.png");
				if (entry != null) return new Image(zipFile.getInputStream(entry));
			}
		}
		return defaultImage;
	}

	@Override
	public Map<String, ResourcePack> getAllResourcePacks()
	{
		return null;
	}

	ResourcePackManImpl()
	{
		archiveRepository = new ArchiveRepository.Builder<ResourcePack>(Core.INSTANCE.getArchivesRoot(), "resourcepacks",
				BiSerializer.combine((s, m) -> null, (s, m) -> null))
				.registerParser(ResourceType.DIR, file ->
				{
					String name = file.getName(), descript = "";
					int format = -1;
					File meta = new File(file, "pack.mcmeta");
					try
					{
						JSONObject jsonObject = IOUtils.toJson(meta);
						format = jsonObject.optInt("pack_format", -1);
						descript = jsonObject.optString("description", "");
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					return new ResourcePackImpl(name, descript, format);
				})
				.registerParser(ResourceType.ZIP, file ->
				{
					String name = file.getName().split(".")[0], descript = "";
					int format = -1;
					try
					{
						ZipFile zipFile = new ZipFile(file);
						ZipEntry entry = zipFile.getEntry("pack.mcmeta");
						if (entry != null)
						{
							JSONObject metaObj = new JSONObject(IOUtils.toString(zipFile.getInputStream(entry)));
							format = metaObj.optInt("pack_format", -1);
							descript = metaObj.optString("description", "");
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					return new ResourcePackImpl(name, descript, format);
				})
				.build();
	}

	public static class ResourcePackImpl implements ResourcePack
	{
		private String packName, description;
		private int format;

		public ResourcePackImpl(String packName, String description, int format)
		{
			this.packName = packName;
			this.description = description;
			this.format = format;
		}

		@Override
		public String getPackName()
		{
			return packName;
		}

		@Override
		public int packFormat()
		{
			return format;
		}

		@Override
		public String getDescription()
		{
			return description;
		}
	}
}
