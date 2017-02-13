package net.launcher;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.launcher.profile.LaunchProfile;
import net.launcher.setting.Setting;
import net.launcher.setting.SettingProperty;
import net.launcher.setting.SettingType;
import org.to2mbn.jmccc.option.LaunchOption;

import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Manage IO, index and search.
 *
 * @author ci010
 */
public abstract class OptionLaunchElementManager<T, O> implements LaunchElementManager<T>
{
	protected WeakReference<ObservableList<T>> cache;

	protected abstract SettingType.Option<O> getOption();

	@Override
	public ObservableList<T> getIncludeElementContainer(LaunchProfile profile)
	{
		Objects.requireNonNull(profile);
		ObservableList<T> list = cache.get();
		if (list != null) return list;
		Optional<Setting> optional = profile.getGameSetting(getOption().getParent());
		Setting setting;
		if (!optional.isPresent())
			profile.addGameSetting(setting = getOption().getParent().defaultInstance());
		else setting = optional.get();

		SettingProperty<O> option = setting.getOption(getOption());
		list = FXCollections.observableArrayList(from(option.getValue()));
		cache = new WeakReference<>(list);
		list.addListener((ListChangeListener<T>) c -> option.setValue(to((List<T>) c.getList())));

		return list;
	}

	protected abstract List<T> from(O value);

	protected abstract O to(List<T> lst);

	@Override
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
