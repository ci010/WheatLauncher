package net.launcher;

import api.launcher.LaunchProfile;
import api.launcher.setting.Setting;
import api.launcher.setting.SettingProperty;
import api.launcher.setting.SettingType;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.lang.ref.WeakReference;
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
		System.out.println(option.getName());
		O value = option.getValue();
		if (value != null)
			list = FXCollections.observableList(from(value));
		else list = FXCollections.observableArrayList();
		list.addListener((ListChangeListener<T>) c ->
				option.setValue(to((List<T>) c.getList())));
		cacheMap.put(profile.getId(), new WeakReference<>(list));
		return list;
	}

	protected abstract List<T> from(O value);

	protected abstract O to(List<T> lst);
}
