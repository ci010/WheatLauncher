package net.launcher.resourcepack;

import api.launcher.ARML;
import api.launcher.MinecraftIcons;
import api.launcher.ResourcePackManager;
import api.launcher.setting.SettingType;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.OptionLaunchElementManager;
import net.launcher.TransformTask;
import net.launcher.game.ResourcePack;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.FetchOption;
import net.launcher.utils.resource.Resource;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class ResourcePackManImpl extends OptionLaunchElementManager<ResourcePack, String[]> implements ResourcePackManager
{
	private ArchiveRepository<ResourcePack> archiveRepository;
	private Map<String, Resource<ResourcePack>> nameToResource = new HashMap<>();
	private ObservableList<ResourcePack> resourcePacks;

	ResourcePackManImpl(ArchiveRepository<ResourcePack> archiveRepository)
	{
		this.archiveRepository = archiveRepository;
		this.resourcePacks = FXCollections.observableArrayList();
		archiveRepository.getResourceMap().forEach((k, v) ->
		{
			nameToResource.put(v.getContainData().getPackName(), v);
			resourcePacks.add(v.getContainData());
		});
		archiveRepository.getResourceMap().addListener((MapChangeListener<String, Resource<ResourcePack>>) change ->
		{
			if (change.wasAdded())
			{
				Resource<ResourcePack> valueAdded = change.getValueAdded();
				nameToResource.put(valueAdded.getContainData().getPackName(), valueAdded);
				resourcePacks.add(valueAdded.getContainData());
			}
			if (change.wasRemoved())
			{
				Resource<ResourcePack> valueRemoved = change.getValueRemoved();
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
		return ARML.core().getProfileSettingManager().getSettingMinecraft().getResourcePace();
	}

	@Override
	protected List<ResourcePack> from(String[] value)
	{
		List<ResourcePack> list = new ArrayList<>(value.length);
		for (String s : value)
		{
			Resource<ResourcePack> resourcePackResource = nameToResource.get(s.substring(0, s.lastIndexOf('.')));
			//TODO this must be fix.... search by the name+suffix but this not support directory resourcepack
			if (resourcePackResource != null)
				list.add(resourcePackResource.getContainData());
		}
		return list;
	}

	@Override
	protected String[] to(List<ResourcePack> lst)
	{
		String[] strings = new String[lst.size()];
		for (int i = 0; i < lst.size(); i++)
			strings[i] = lst.get(i).getPackName() + nameToResource.get(lst.get(i).getPackName()).getType().getSuffix();
		return strings;
	}

	@Override
	public Image getIcon(ResourcePack resourcePack)
	{
		Objects.requireNonNull(resourcePack);
		Resource<ResourcePack> packResource = nameToResource.get(resourcePack.getPackName());
		if (packResource != null)
			try (InputStream stream = archiveRepository.openStream(packResource, "pack.png"))
			{
				return new Image(stream);
			}
			catch (Exception e) {return MinecraftIcons.UNKNOWN;}
		return MinecraftIcons.UNKNOWN;
	}

	@Override
	public Task<?> update()
	{
		return archiveRepository.update();
	}

	@Override
	public Task<ResourcePack> importResourcePack(Path resourcePack)
	{
		return TransformTask.create("ImportResourcePack", archiveRepository.importResource(resourcePack),
				resource -> resource.setName(resource.getContainData().getPackName()).getContainData()
		);
	}

	@Override
	public Task<Void> exportResourcePack(Path path, Collection<ResourcePack> pack)
	{
		Objects.requireNonNull(path);
		Objects.requireNonNull(pack);
		return TransformTask.toVoid("ExportResourcePack", archiveRepository.fetchAllResource(path, pack.stream().map(ResourcePack::getPackName).map(nameToResource::get).map
				(Resource::getName).collect(Collectors.toList()), FetchOption.COPY));
	}
}
