package net.launcher.game.mod;

import javafx.beans.property.Property;
import javafx.collections.MapChangeListener;
import net.launcher.LaunchElementManager;
import net.launcher.LaunchProfile;
import net.launcher.game.OptionLaunchElementManager;
import net.launcher.game.setting.GameSettings;
import net.launcher.game.setting.Option;
import net.launcher.utils.ProgressCallback;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repository;

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

	@Override
	protected Option<String[]> getOption()
	{
		return GameSettings.MODS;
	}

	@Override
	protected void implementVirtualPath(LaunchProfile profile, Path path, Property<String[]> property, ProgressCallback<Void> callback)
	{
		String[] value = property.getValue();
		if (callback != null)
			callback.updateProgress(0, value.length, "start");
		for (int i = 0; i < value.length; i++)
		{
			if (callback != null)
				callback.updateProgress(i, value.length, "resolve mod");
			archiveResource.fetchResource(path, modVersionToResource.get(value[i]), null, Repository.FetchOption
					.SYMBOL_LINK);
		}
	}
}
