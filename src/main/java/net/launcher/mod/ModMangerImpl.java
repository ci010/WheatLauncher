package net.launcher.mod;

import javafx.collections.MapChangeListener;
import net.launcher.LaunchElementManager;
import net.launcher.OptionLaunchElementManager;
import net.launcher.game.forge.ForgeMod;
import net.launcher.profile.LaunchProfile;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingInstance;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repository;
import org.to2mbn.jmccc.option.LaunchOption;

import java.nio.file.Path;
import java.util.*;

/**
 * @author ci010
 */
class ModMangerImpl extends OptionLaunchElementManager<ForgeMod, String[]> implements LaunchElementManager<ForgeMod>
{
	private ArchiveRepository<ForgeMod[]> archiveResource;
	private Map<String, String> modVersionToResource = new TreeMap<>();
	private Map<String, ForgeMod> toRelease = new TreeMap<>();
	private Map<LaunchOption, Repository.Delivery<ArchiveRepository.Resource<ForgeMod[]>>> deliveryCache = new HashMap<>();

	ModMangerImpl(ArchiveRepository<ForgeMod[]> archiveResource)
	{
		this.archiveResource = archiveResource;
		archiveResource.getResourceMap().forEach((k, v) ->
		{
			for (ForgeMod release : v.getContainData())
				modVersionToResource.put(release.getModId() + ":" +
						release.getVersion(), k);
		});
		archiveResource.getResourceMap().addListener(new MapChangeListener<String, ArchiveRepository.Resource<ForgeMod[]>>()
		{
			@Override
			public void onChanged(Change<? extends String, ? extends ArchiveRepository.Resource<ForgeMod[]>> change)
			{
				if (change.wasAdded())
					for (ForgeMod modMeta : change.getValueAdded().getContainData())
					{
						String s = modMeta.getModId() + ":" + modMeta.getMetaData().getVersion();
						modVersionToResource.put(s, change.getValueAdded().getHash());
						toRelease.put(s, modMeta);
					}
				if (change.wasRemoved())
					for (ForgeMod release : change.getValueRemoved().getContainData())
					{
						String s = release.getModId() + ":" + release.getVersion();
						modVersionToResource.remove(s);
						toRelease.remove(s);
					}
			}
		});
	}

	@Override
	public Set<ForgeMod> getAllElement()
	{
		HashSet<ForgeMod> releases = new HashSet<>();
		Collection<ArchiveRepository.Resource<ForgeMod[]>> values = archiveResource.getResourceMap().values();
		for (ArchiveRepository.Resource<ForgeMod[]> value : values)
			Collections.addAll(releases, value.getContainData());
		return releases;
	}

	@Override
	protected GameSetting.Option<String[]> getOption()
	{
		return GameSettingMod.INSTANCE.MODS;
	}

	@Override
	protected void implementRuntimePath(LaunchProfile profile, Path path, GameSettingInstance instance, LaunchOption option)
	{
		String[] value = instance.getOption(getOption());
		for (int i = 0; i < value.length; i++)
			deliveryCache.put(option,
					archiveResource.fetchResource(path, modVersionToResource.get(value[i]), null, Repository.FetchOption
							.SYMBOL_LINK));
	}

	@Override
	public void onClose(LaunchOption option, LaunchProfile profile)
	{
		Repository.Delivery<ArchiveRepository.Resource<ForgeMod[]>> delivery = deliveryCache.get(option);
		if (delivery != null)
			delivery.markRelease();
	}

	@Override
	protected String[] to(List<ForgeMod> lst)
	{
		String[] strings = new String[lst.size()];
		for (int i = 0; i < lst.size(); i++)
		{
			ForgeMod element = lst.get(i);
			strings[i] = element.getModId() + ":" + element.getMetaData().getVersion();
		}
		return strings;
	}

	@Override
	protected List<ForgeMod> from(String[] value)
	{
		List<ForgeMod> lst = new ArrayList<>(value.length);
		for (String s : value)
			lst.add(toRelease.get(s));
		return lst;
	}

}
