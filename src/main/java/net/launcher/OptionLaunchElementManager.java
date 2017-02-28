package net.launcher;

import api.launcher.LaunchProfile;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.launcher.setting.Setting;
import net.launcher.setting.SettingProperty;
import net.launcher.setting.SettingType;
import org.to2mbn.jmccc.option.LaunchOption;

import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.*;

/**
 * Manage IO, index and search.
 *
 * @author ci010
 */
public abstract class OptionLaunchElementManager<T, O> implements LaunchElementManager<T>
{
	private Map<String, WeakReference<ObservableList<T>>> cacheMap = new TreeMap<>();

	protected abstract SettingType.Option<O> getOption();

	@Override
	public ObservableList<T> getIncludeElementContainer(LaunchProfile profile)
	{
		Objects.requireNonNull(profile);
		WeakReference<ObservableList<T>> reference = cacheMap.get(profile.getId());
		if (reference == null) reference = new WeakReference<>(null);
		ObservableList<T> list = reference.get();
		if (list != null) return list;
		Setting setting;
		Optional<Setting> optional = profile.getGameSetting(getOption().getParent());
		if (!optional.isPresent())
			profile.addGameSetting(setting = getOption().getParent().defaultInstance());
		else setting = optional.get();
		SettingProperty<O> option = setting.getOption(getOption());
		list = FXCollections.observableList(from(option.getValue()));
		list.addListener((ListChangeListener<T>) c ->
				option.setValue(to((List<T>) c.getList())));
		cacheMap.put(profile.getId(), new WeakReference<>(list));
		return list;
	}

	protected abstract List<T> from(O value);

	protected abstract O to(List<T> lst);

	public void onLaunch(LaunchOption option, LaunchProfile profile)
	{
		Objects.requireNonNull(option);
		Objects.requireNonNull(profile);
		SettingType.Option<O> goption = getOption();
		Optional<Setting> gameSetting = profile.getGameSetting(goption.getParent());
		if (gameSetting.isPresent())
			implementRuntimePath(profile, option.getRuntimeDirectory().getRoot().toPath(), gameSetting.get(), option);
	}

	protected abstract void implementRuntimePath(LaunchProfile profile, Path path, Setting instance, LaunchOption option);
}
