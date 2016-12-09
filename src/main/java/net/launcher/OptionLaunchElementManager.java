package net.launcher;

import net.launcher.profile.LaunchProfile;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingInstance;
import org.to2mbn.jmccc.option.LaunchOption;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Manage IO, index and search.
 *
 * @author ci010
 */
public abstract class OptionLaunchElementManager<T, O> implements LaunchElementManager<T>
{
	protected abstract GameSetting.Option<O> getOption();

	@Override
	public List<T> getAllIncludedElement(LaunchProfile profile)
	{
		Objects.requireNonNull(profile);
		Optional<GameSettingInstance> setting = profile.getGameSetting(getOption().getParent());
		if (!setting.isPresent()) return Collections.emptyList();
		GameSettingInstance prop = setting.get();
		return from(prop.getOption(getOption()));
	}

	@Override
	public void manipulateIncludeElement(LaunchProfile profile, Consumer<List<T>> manipulator)
	{
		Objects.requireNonNull(profile);
		Objects.requireNonNull(manipulator);
		Optional<GameSettingInstance> setting = profile.getGameSetting(getOption().getParent());
		if (!setting.isPresent()) return;
		GameSettingInstance prop = setting.get();
		List<T> from = from(prop.getOption(getOption()));
		manipulator.accept(from);
		O to = to(from);
		prop.setOption(getOption(), to);
	}

	protected abstract List<T> from(O value);

	protected abstract O to(List<T> lst);

	@Override
	public void onLaunch(LaunchOption option, LaunchProfile profile)
	{
		Objects.requireNonNull(option);
		Objects.requireNonNull(profile);
		GameSetting.Option<O> goption = getOption();
		Optional<GameSettingInstance> gameSetting = profile.getGameSetting(goption.getParent());
		if (gameSetting.isPresent())
			implementRuntimePath(profile, option.getRuntimeDirectory().getRoot().toPath(), gameSetting.get(), option);
	}

	protected abstract void implementRuntimePath(LaunchProfile profile, Path path, GameSettingInstance instance, LaunchOption option);
}
