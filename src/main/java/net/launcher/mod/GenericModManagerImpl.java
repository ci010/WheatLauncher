package net.launcher.mod;

import api.launcher.MinecraftIcons;
import api.launcher.setting.SettingType;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.OptionLaunchElementManager;
import net.launcher.TransformTask;
import net.launcher.game.ModManifest;
import net.launcher.game.mods.ModContainer;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.FetchOption;
import net.launcher.utils.resource.Resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author ci010
 */
public class GenericModManagerImpl extends OptionLaunchElementManager<ModContainer<?>, ModManifest> implements GeneircModManager
{
	private ArchiveRepository<ModContainer<?>[]> repository;
	private Map<String, Resource<ModContainer<?>[]>> idToResourceMap;
	private ObservableList<ModContainer<?>> allMods;

	GenericModManagerImpl(ArchiveRepository<ModContainer<?>[]> repository)
	{
		this.allMods = FXCollections.observableArrayList();
		this.idToResourceMap = new TreeMap<>();
		this.repository = repository;

		ObservableMap<String, Resource<ModContainer<?>[]>> map = repository.getResourceMap();
		Stream<Resource<ModContainer<?>[]>> stream = map.values().stream();
		stream.forEach(resource ->
		{
			for (ModContainer<?> container : resource.getContainData())
				idToResourceMap.put(createKey(container), resource);
		});
		stream.map(Resource::getContainData).forEach(allMods::addAll);
		map.addListener((MapChangeListener<String, Resource<ModContainer<?>[]>>) change ->
		{
			if (change.wasAdded())
			{
				Resource<ModContainer<?>[]> resources = change.getValueAdded();
				for (ModContainer<?> resource : resources.getContainData())
				{
					allMods.add(resource);
					idToResourceMap.put(createKey(resource), resources);
				}
			}
			if (change.wasRemoved())
			{
				Resource<ModContainer<?>[]> resources = change.getValueAdded();
				for (ModContainer<?> resource : resources.getContainData())
				{
					allMods.remove(resource);
					idToResourceMap.remove(createKey(resource));
				}
			}
		});
	}

	@Override
	public ObservableList<ModContainer<?>> getAllElement()
	{
		return allMods;
	}

	@Override
	public Task<Image> getLogo(ModContainer<?> mod)
	{
		return new Task<Image>()
		{
			@Override
			protected Image call() throws Exception
			{
				NBTCompound customMetadata = getCustomMetadata(mod);
				Optional<NBT> logo = customMetadata.getOption("logo");
				if (logo.isPresent())
				{
					String s = logo.get().asString();

				}
				return MinecraftIcons.UNKNOWN;
			}
		};

	}

	@Override
	public Task<ModContainer<?>[]> importMod(Path path)
	{
		return TransformTask.create("ImportMod", repository.importResource(path), Resource::getContainData);
	}

	@Override
	public Task<Void> exportMod(Path path, ModContainer<?> mod)
	{
		Objects.requireNonNull(path);
		Objects.requireNonNull(mod);
		return new Task<Void>()
		{
			{updateTitle("ExportMod");}

			@Override
			protected Void call() throws Exception
			{
				Path dir;
				if (Files.isDirectory(path)) dir = path;
				else dir = path.getParent();
				Resource<ModContainer<?>[]> resource = idToResourceMap.get(createKey(mod));
				repository.fetchResource(dir, resource.getHash(), FetchOption.COPY).run();
				if (dir == path)
					Files.move(path, dir.resolve(resource.getName() + resource.getType().getSuffix()));
				else Files.move(path, path);
				return null;
			}
		};
	}

	@Override
	public Task<?> update()
	{
		return new Task<Object>()
		{
			@Override
			protected Object call() throws Exception
			{
				repository.update().run();
				return null;
			}
		};
	}

	private String createKey(ModContainer<?> container)
	{
		return container.getId() + ":" + container.getVersion();
	}

	@Override
	public NBTCompound getCustomMetadata(ModContainer<?> forgeMod)
	{
		Resource<ModContainer<?>[]> resource = idToResourceMap.get(createKey(forgeMod));
		return resource.getCompound();
	}

	@Override
	protected SettingType.Option<ModManifest> getOption()
	{
		return null;
	}

	@Override
	protected List<ModContainer<?>> from(ModManifest value)
	{
		List<ModContainer<?>> list = new ArrayList<>();
		for (String s : value.getAllTypes())
			value.getMods(s).entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).
					map(idToResourceMap::get).filter(Objects::nonNull).map(Resource::getContainData).forEach
					(data -> Collections.addAll(list, data));
		return list;
	}

	@Override
	protected ModManifest to(List<ModContainer<?>> lst)
	{
		Map<String, Map<String, String>> allMods = new TreeMap<>();
		for (ModContainer<?> container : lst)
		{
			String modType = container.getModType();
			Map<String, String> modMap = allMods.get(modType);
			if (modMap == null)
				allMods.put(modType, modMap = new TreeMap<>());
			modMap.put(container.getId(), container.getVersion().getVersionString());
		}
		return new ModManifest(allMods);
	}
}
