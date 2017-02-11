package net.launcher.resourcepack;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import net.launcher.OptionLaunchElementManager;
import net.launcher.game.ResourcePack;
import net.launcher.profile.LaunchProfile;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingMinecraft;
import net.launcher.setting.GameSettingType;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repository;
import org.to2mbn.jmccc.option.LaunchOption;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ci010
 */
class ResourcePackManImpl extends OptionLaunchElementManager<ResourcePack, String[]> implements ResourcePackManager
{
	private static Image DEFAULT_IMG = new Image(ResourcePackManImpl.class.getResourceAsStream("/pack.png"));

	private ArchiveRepository<ResourcePack> archiveRepository;
	private Map<String, ArchiveRepository.Resource<ResourcePack>> record = new HashMap<>();
	private Map<LaunchOption, Repository.Delivery<ArchiveRepository.Resource<ResourcePack>>> launchCache = new HashMap<>();

	ResourcePackManImpl(ArchiveRepository<ResourcePack> archiveRepository)
	{
		this.archiveRepository = archiveRepository;
		archiveRepository.getResourceMap().forEach((k, v) -> record.put(v.getName(), v));
		archiveRepository.getResourceMap().addListener((MapChangeListener<String, ArchiveRepository.Resource<ResourcePack>>) change ->
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
		});
	}

	@Override
	public ObservableList<ResourcePack> getAllElement()
	{
		return null;
//		return archiveRepository.getResourceMap().entrySet().stream().map(e -> e.getValue().getContainData()).collect(Collectors.toSet());
	}

	@Override
	protected GameSettingType.Option<String[]> getOption()
	{
		return GameSettingMinecraft.INSTANCE.RESOURCE_PACE;
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
		return new String[0];
	}

	@Override
	protected void implementRuntimePath(LaunchProfile profile, Path path, GameSetting property, LaunchOption option)
	{
		String[] value = property.getOption(getOption()).getValue();
		for (String aValue : value)
			launchCache.put(option, archiveRepository.fetchResource(path, record.get(aValue).getHash(),
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
		ArchiveRepository.Resource<ResourcePack> packResource = record.get(resourcePack.getPackName());
		if (packResource != null)
			try (InputStream stream = archiveRepository.openStream(packResource, "pack.png"))
			{
				return new Image(stream);
			}
		return DEFAULT_IMG;
	}
}
