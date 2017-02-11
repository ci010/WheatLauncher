package net.launcher.mod;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import net.launcher.LaunchElementManager;
import net.launcher.OptionLaunchElementManager;
import net.launcher.game.forge.ForgeMod;
import net.launcher.profile.LaunchProfile;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingType;
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
	private ObservableList<ForgeMod> list;

	ModMangerImpl(ArchiveRepository<ForgeMod[]> archiveResource)
	{
		this.archiveResource = archiveResource;
		archiveResource.getResourceMap().forEach((k, v) ->
		{
			for (ForgeMod release : v.getContainData())
				modVersionToResource.put(release.getModId() + ":" +
						release.getVersion(), k);
		});
		archiveResource.getResourceMap().addListener((MapChangeListener<String, ArchiveRepository.Resource<ForgeMod[]>>) change ->
		{
			if (change.wasAdded())
				for (ForgeMod modMeta : change.getValueAdded().getContainData())
				{
					String s = modMeta.getModId() + ":" + modMeta.getMetaData().getVersion();
					modVersionToResource.put(s,
							change.getValueAdded().getHash());
					toRelease.put(s, modMeta);
				}
			if (change.wasRemoved())
				for (ForgeMod release : change.getValueRemoved().getContainData())
				{
					String s = release.getModId() + ":" +
							release.getVersion();
					modVersionToResource.remove(s);
					toRelease.remove(s);
				}
		});
	}

	//
	@Override
	public ObservableList<ForgeMod> getAllElement()
	{
		if (list == null)
		{
			list = FXCollections.observableList(new ArrayList<>());
			Collection<ArchiveRepository.Resource<ForgeMod[]>> values = archiveResource.getResourceMap().values();
			for (ArchiveRepository.Resource<ForgeMod[]> value : values)
				Collections.addAll(list, value.getContainData());
		}
		return FXCollections.unmodifiableObservableList(list);
	}

	@Override
	protected GameSettingType.Option<String[]> getOption()
	{
		return GameSettingMod.INSTANCE.MODS;
	}

	@Override
	protected void implementRuntimePath(LaunchProfile profile, Path path, GameSetting instance, LaunchOption option)
	{
		ObservableList<ForgeMod> forgeMods = cache.get();
		if (forgeMods == null)
		{
			String[] value = instance.getOption(getOption()).getValue();
			if (value != null)
				for (String aValue : value)
					deliveryCache.put(option,
							archiveResource.fetchResource(path.resolve("mods"), modVersionToResource.get(aValue),
									Repository.FetchOption.SYMBOL_LINK));
		}
		else
		{
//			deliveryCache.put(option, )
		}
	}

	@Override
	public void onClose(LaunchOption option, LaunchProfile profile)
	{
		Repository.Delivery<ArchiveRepository.Resource<ForgeMod[]>> delivery = deliveryCache.get(option);
		if (delivery != null)
			delivery.markRelease();
	}

	@Override
	protected List<ForgeMod> from(String[] value)
	{
		List<ForgeMod> lst = new ArrayList<>(value.length);
		for (String s : value)
			lst.add(toRelease.get(s));
		return lst;
	}

	@Override
	protected String[] to(List<ForgeMod> lst)
	{
		return new String[0];
	}

}
