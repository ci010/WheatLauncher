package net.launcher.mod;

import javafx.collections.MapChangeListener;
import net.launcher.LaunchElementManager;
import net.launcher.OptionLaunchElementManager;
import net.launcher.game.mod.Mod;
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
class ModMangerImpl extends OptionLaunchElementManager<Mod, String[]> implements LaunchElementManager<Mod>
{
	private ArchiveRepository<Mod[]> archiveResource;
	private Map<String, String> modVersionToResource = new TreeMap<>();
	private Map<String, Mod> toRelease = new TreeMap<>();
	private Map<LaunchOption, Repository.Delivery<ArchiveRepository.Resource<Mod[]>>> deliveryCache = new HashMap<>();

	ModMangerImpl(ArchiveRepository<Mod[]> archiveResource)
	{
		this.archiveResource = archiveResource;
		archiveResource.getResourceMap().forEach((k, v) ->
		{
			for (Mod release : v.getContainData())
				modVersionToResource.put(release.getModId() + ":" +
						release.getVersion(), k);
		});
		archiveResource.getResourceMap().addListener(new MapChangeListener<String, ArchiveRepository.Resource<Mod[]>>()
		{
			@Override
			public void onChanged(Change<? extends String, ? extends ArchiveRepository.Resource<Mod[]>> change)
			{
				if (change.wasAdded())
					for (Mod modMeta : change.getValueAdded().getContainData())
					{
						String s = modMeta.getModId() + ":" + modMeta.getMetaData().getVersion();
						modVersionToResource.put(s,
								change.getValueAdded().getHash());
						toRelease.put(s, modMeta);
					}
				if (change.wasRemoved())
					for (Mod release : change.getValueRemoved().getContainData())
					{
						String s = release.getModId() + ":" +
								release.getVersion();
						modVersionToResource.remove(s);
						toRelease.remove(s);
					}
			}
		});
	}

	@Override
	public Set<Mod> getAllElement()
	{
		HashSet<Mod> releases = new HashSet<>();
		Collection<ArchiveRepository.Resource<Mod[]>> values = archiveResource.getResourceMap().values();
		for (ArchiveRepository.Resource<Mod[]> value : values)
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
		Repository.Delivery<ArchiveRepository.Resource<Mod[]>> delivery = deliveryCache.get(option);
		if (delivery != null)
			delivery.markRelease();
	}

	@Override
	protected String[] to(List<Mod> lst)
	{
		String[] strings = new String[lst.size()];
		for (int i = 0; i < lst.size(); i++)
		{
			Mod element = lst.get(i);
			strings[i] = element.getModId() + ":" + element.getMetaData().getVersion();
		}
		return strings;
	}

	@Override
	protected List<Mod> from(String[] value)
	{
		List<Mod> lst = new ArrayList<>(value.length);
		for (String s : value)
			lst.add(toRelease.get(s));
		return lst;
	}

}
