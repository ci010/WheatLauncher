package net.launcher.game;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import net.launcher.utils.FutureCallbackTask;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.ArchiveResource;
import net.launcher.utils.resource.ResourceType;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ci010
 */
class ResourcePackManImpl implements ResourcePackManger
{
	private ArchiveRepository<ResourcePack> archiveRepository;
	private ObservableMap<String, ResourcePack> record = FXCollections.observableHashMap();
	private static Image defaultImage = new Image(ResourcePackManImpl.class.getResourceAsStream("/pack.png"));
	private ObservableList<ResourcePack> enabled = FXCollections.observableArrayList();
	private Reference<ExecutorService> service;

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
			Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
		}
		if (resource.isPresent()) return Optional.ofNullable(resource.get().getContainData());
		else return Optional.empty();
	}

	public void useService(Reference<ExecutorService> service)
	{
		this.service = service;
	}

	@Override
	public Future<ResourcePack> importResourcePack(File file, Callback<ResourcePack> callback)
	{
		FutureCallbackTask<ResourcePack> task = new FutureCallbackTask<>(() -> archiveRepository.importFile(file));
		if (callback != null) task.setCallback(callback);
		if (service != null)
		{
			ExecutorService ser = service.get();
			if (ser != null && !ser.isTerminated())
				return (Future<ResourcePack>) ser.submit(task);
		}
		task.run();
		return task;
	}

	@Override
	public ObservableList<ResourcePack> enabledResourcePack()
	{
		return enabled;
	}

	@Override
	public Image getIcon(ResourcePack resourcePack) throws IOException
	{
		Optional<ArchiveResource<ResourcePack>> resource = archiveRepository.findResource(resourcePack.getPackName());
		if (resource.isPresent())
			return new Image(archiveRepository.openStream(resource.get(), "pack.png"));
		return defaultImage;
	}

	@Override
	public ObservableMap<String, ResourcePack> getAllResourcePacks()
	{
		return record;
	}

	ResourcePackManImpl(File root, Reference<ExecutorService> service)
	{
		this.service = service;
		archiveRepository = new ArchiveRepository.Builder<ResourcePack>(root, "resourcepacks",
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
						Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
					}
					return new ResourcePack(name, descript, format);
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
						entry = zipFile.getEntry("pack.png");
					}
					catch (IOException e)
					{
						Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
					}
					return new ResourcePack(name, descript, format);
				})
				.build();

		archiveRepository.getAllStorage().addListener(new MapChangeListener<String, ArchiveResource<ResourcePack>>()
		{
			@Override
			public void onChanged(Change<? extends String, ? extends ArchiveResource<ResourcePack>> change)
			{
				ArchiveResource<ResourcePack> valueAdded = change.getValueAdded();
				record.put(valueAdded.getName(), valueAdded.getContainData());
			}
		});
	}
}
