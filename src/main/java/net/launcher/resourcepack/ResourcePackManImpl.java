package net.launcher.resourcepack;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import net.launcher.OptionLaunchElementManager;
import net.launcher.game.ResourcePack;
import net.launcher.profile.LaunchProfile;
import net.launcher.setting.Setting;
import net.launcher.setting.SettingMinecraft;
import net.launcher.setting.SettingType;
import net.launcher.utils.ProgressCallback;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repository;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.option.LaunchOption;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class ResourcePackManImpl extends OptionLaunchElementManager<ResourcePack, String[]> implements ResourcePackManager
{
	private static Image DEFAULT_IMG = new Image(ResourcePackManImpl.class.getResourceAsStream("/pack.png"));

	private ArchiveRepository<ResourcePack> archiveRepository;
	private Map<String, ArchiveRepository.Resource<ResourcePack>> nameToResource = new HashMap<>();
	private ObservableList<ResourcePack> resourcePacks;

	private Map<LaunchOption, Repository.Delivery<ArchiveRepository.Resource<ResourcePack>>> launchCache = new HashMap<>();

	ResourcePackManImpl(ArchiveRepository<ResourcePack> archiveRepository)
	{
		this.archiveRepository = archiveRepository;
		this.resourcePacks = FXCollections.observableArrayList();
		archiveRepository.getResourceMap().forEach((k, v) ->
		{
			nameToResource.put(v.getContainData().getPackName(), v);
			resourcePacks.add(v.getContainData());
		});
		archiveRepository.getResourceMap().addListener((MapChangeListener<String, ArchiveRepository.Resource<ResourcePack>>) change ->
		{
			if (change.wasAdded())
			{
				ArchiveRepository.Resource<ResourcePack> valueAdded = change.getValueAdded();
				nameToResource.put(valueAdded.getContainData().getPackName(), valueAdded);
				resourcePacks.add(valueAdded.getContainData());
			}
			if (change.wasRemoved())
			{
				ArchiveRepository.Resource<ResourcePack> valueRemoved = change.getValueRemoved();
				nameToResource.remove(valueRemoved.getContainData().getPackName());
				resourcePacks.add(valueRemoved.getContainData());
			}
		});
	}

	@Override
	public ObservableList<ResourcePack> getAllElement()
	{
		return resourcePacks;
	}

	@Override
	protected SettingType.Option<String[]> getOption()
	{
		return SettingMinecraft.INSTANCE.RESOURCE_PACE;
	}

	@Override
	protected List<ResourcePack> from(String[] value)
	{
		List<ResourcePack> list = new ArrayList<>(value.length);
		for (String s : value)
		{
			ArchiveRepository.Resource<ResourcePack> resourcePackResource = nameToResource.get(s);
			if (resourcePackResource != null)
				list.add(resourcePackResource.getContainData());
		}
		return list;
	}

	@Override
	protected String[] to(List<ResourcePack> lst)
	{
		return new String[0];
	}

	@Override
	protected void implementRuntimePath(LaunchProfile profile, Path path, Setting property, LaunchOption option)
	{
		String[] value = property.getOption(getOption()).getValue();
		for (String aValue : value)
			launchCache.put(option, archiveRepository.fetchResource(path, nameToResource.get(aValue).getHash(),
					Repository.FetchOption.SYMBOL_LINK));
	}

	@Override
	public void onClose(LaunchOption option, LaunchProfile profile)
	{
		Repository.Delivery<ArchiveRepository.Resource<ResourcePack>> resourceDelivery = launchCache.get(option);
		if (resourceDelivery != null)
			resourceDelivery.markRelease();
	}


	@Override
	public Image getIcon(ResourcePack resourcePack) throws IOException
	{
		ArchiveRepository.Resource<ResourcePack> packResource = nameToResource.get(resourcePack.getPackName());
		if (packResource != null)
			try (InputStream stream = archiveRepository.openStream(packResource, "pack.png"))
			{
				return new Image(stream);
			}
		return DEFAULT_IMG;
	}

	@Override
	public Future<?> update()
	{
		return archiveRepository.update();
	}

	@Override
	public void importResourcePack(Path resourcePack, Callback<ResourcePack> callback)
	{
		archiveRepository.importFile(resourcePack, new ProgressCallback<ArchiveRepository.Resource<ResourcePack>>()
		{
			@Override
			public void updateProgress(long done, long total, String message)
			{

			}

			@Override
			public void done(ArchiveRepository.Resource<ResourcePack> result)
			{
				callback.done(result.getContainData());
			}

			@Override
			public void failed(Throwable e)
			{
				callback.failed(e);
			}

			@Override
			public void cancelled()
			{
				callback.cancelled();
			}
		});
	}

	@Override
	public void exportResourcePack(Path path, Collection<ResourcePack> pack)
	{
		Objects.requireNonNull(path);
		Objects.requireNonNull(pack);
		archiveRepository.fetchAllResources(path, pack.stream().map(ResourcePack::getPackName).map(nameToResource::get).map(ArchiveRepository
				.Resource::getName).collect(Collectors.toList()), Repository.FetchOption.COPY);
	}
}
