package net.wheatlauncher.internal.repository;

import javafx.scene.image.Image;
import net.wheatlauncher.ResourcePack;
import net.wheatlauncher.internal.ResourcePackImpl;
import net.wheatlauncher.utils.JsonSerializer;
import net.wheatlauncher.utils.resource.ArchiveResource;
import net.wheatlauncher.utils.resource.ResourceType;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ci010
 */
public class ResourcePackControl
{
	private ArchiveRepository<ResourcePack> archiveRepository;
	private static Image defaultImage = new Image(ResourcePackControl.class.getResourceAsStream("/pack.png"));

	public Set<ResourcePack> getAllResourcePack()
	{
		return archiveRepository.getAllStorage().values().stream()
				.map(ArchiveResource::getContainData)
				.collect(Collectors.toSet());
	}

	public Optional<ResourcePack> getResourcePack(String name)
	{
		Optional<ArchiveResource<ResourcePack>> resource =
				archiveRepository.findResource(name);
		if (resource.isPresent()) return Optional.ofNullable(resource.get().getContainData());
		else return Optional.empty();
	}

	public Image getIcon(ResourcePack resourcePack) throws IOException
	{
		Optional<ArchiveResource<ResourcePack>> resource = archiveRepository.findResource(resourcePack.getPackName());
		if (resource.isPresent())
		{
			ArchiveResource<ResourcePack> archiveResource = resource.get();
			File f = archiveRepository.getFileLocation(archiveResource);
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

	public ResourcePackControl()
	{
		archiveRepository = new ArchiveRepository.Builder<>("resourcepacks",
				new JsonSerializer<ResourcePack>()
				{
					@Override
					public ResourcePack deserialize(JSONObject jsonObject)
					{
						return new ResourcePackImpl(
								jsonObject.getString("name"),
								jsonObject.optString("description"),
								jsonObject.optInt("format"));
					}

					@Override
					public JSONObject serialize(ResourcePack data)
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("name", data.getPackName());
						jsonObject.put("description", data.getDescription());
						jsonObject.put("format", data.packFormat());
						return jsonObject;
					}
				})
				.registerParser(ResourceType.DIR, file -> {
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
				.registerParser(ResourceType.ZIP, file -> {
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
}
