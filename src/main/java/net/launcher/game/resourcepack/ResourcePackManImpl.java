package net.launcher.game.resourcepack;

import javafx.beans.property.Property;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;
import net.launcher.LaunchProfile;
import net.launcher.game.OptionLaunchElementManager;
import net.launcher.game.setting.GameSettings;
import net.launcher.game.setting.Option;
import net.launcher.utils.ProgressCallback;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class ResourcePackManImpl extends OptionLaunchElementManager<ResourcePack, String[]> implements
																					 ResourcePackManager
{
	private static Image DEFAULT_IMG = new Image(ResourcePackManImpl.class.getResourceAsStream("/pack.png"));

	private ArchiveRepository<ResourcePack> archiveRepository;
	private Map<String, ArchiveRepository.Resource<ResourcePack>> record = new HashMap<>();

	ResourcePackManImpl(ArchiveRepository<ResourcePack> archiveRepository)
	{
		this.archiveRepository = archiveRepository;
		archiveRepository.getResourceMap().forEach((k, v) -> record.put(v.getName(), v));
		archiveRepository.getResourceMap().addListener(new MapChangeListener<String, ArchiveRepository.Resource<ResourcePack>>()
		{
			@Override
			public void onChanged(Change<? extends String, ? extends ArchiveRepository.Resource<ResourcePack>> change)
			{
				if (change.wasAdded())
				{
					ArchiveRepository.Resource<ResourcePack> valueAdded = change.getValueAdded();
					record.put(valueAdded.getName(), valueAdded);
				}
				if (change.wasRemoved())
				{
					ArchiveRepository.Resource<ResourcePack> valueRemoved = change.getValueRemoved();
					record.remove(valueRemoved.getName());
				}
			}
		});
	}

	@Override
	public Set<ResourcePack> getAllElement()
	{
		return archiveRepository.getResourceMap().entrySet().stream().map(e -> e.getValue().getContainData()).collect(Collectors.toSet());
	}

	@Override
	protected Option<String[]> getOption()
	{
		return GameSettings.RESOURCE_PACE;
	}

	@Override
	protected List<ResourcePack> from(String[] value)
	{
		List<ResourcePack> list = new ArrayList<>(value.length);
		for (String s : value)
		{
			ArchiveRepository.Resource<ResourcePack> resourcePackResource = record.get(s);
			if (resourcePackResource != null)
				list.add(resourcePackResource.getContainData());
		}
		return list;
	}

	@Override
	protected String[] to(List<ResourcePack> lst)
	{
		String[] strings = new String[lst.size()];
		for (int i = 0; i < strings.length; i++)
			strings[i] = lst.get(i).getPackName();
		return strings;
	}

	@Override
	protected void implementVirtualPath(LaunchProfile profile, Path path, Property<String[]> property, ProgressCallback<Void> callback)
	{
		String[] value = property.getValue();
		if (callback != null)
			callback.updateProgress(0, value.length + 1, "start");
		for (int i = 0; i < value.length; i++)
		{
			if (callback != null)
				callback.updateProgress(i + 1, value.length + 1, "resolve mod");
			archiveRepository.fetchResource(path, record.get(value[i]).getHash(), null, Repository.FetchOption
					.SYMBOL_LINK);
		}
	}

	@Override
	public Image getIcon(ResourcePack resourcePack) throws IOException
	{
		ArchiveRepository.Resource<ResourcePack> packResource = record.get(resourcePack.getPackName());
		if (packResource != null)
			try (InputStream stream = archiveRepository.openStream(packResource, "pack.png"))
			{
				return new Image(stream);
			}
		return DEFAULT_IMG;
	}
}
