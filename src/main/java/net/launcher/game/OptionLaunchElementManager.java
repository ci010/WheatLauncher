package net.launcher.game;

import javafx.beans.property.Property;
import net.launcher.LaunchElement;
import net.launcher.LaunchElementManager;
import net.launcher.LaunchProfile;
import net.launcher.game.setting.Option;
import net.launcher.utils.ProgressCallback;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

/**
 * Manage IO, index and search.
 *
 * @author ci010
 */
public abstract class OptionLaunchElementManager<T extends LaunchElement, O> implements LaunchElementManager<T>
{
	protected abstract Option<O> getOption();

	@Override
	public Set<T> getAllIncludedElement(LaunchProfile profile)
	{
		Objects.requireNonNull(profile);
		Optional<Property<O>> setting = profile.getSetting(getOption());
		if (!setting.isPresent()) return Collections.emptySet();
		Property<O> prop = setting.get();
		List<T> from = from(prop.getValue());
		return new TreeSet<>(from);
	}

	@Override
	public void manipulateIncludeElement(LaunchProfile profile, Consumer<List<T>> manipulator)
	{
		Objects.requireNonNull(profile);
		Objects.requireNonNull(manipulator);
		Property<O> prop = profile.createSettingIfAbsent(getOption());
		List<T> from = from(prop.getValue());
		manipulator.accept(from);
		O to = to(from);
		prop.setValue(to);
	}

	protected abstract List<T> from(O value);

	protected abstract O to(List<T> lst);

	@Override
	public void onImplementVirtualPath(Path path, LaunchProfile profile, ProgressCallback<Void> callback)
	{
		Objects.requireNonNull(path);
		Objects.requireNonNull(profile);
		Optional<Property<O>> setting = profile.getSetting(getOption());
		if (setting.isPresent())
			implementVirtualPath(profile, path, setting.get(), callback);
	}

	protected abstract void implementVirtualPath(LaunchProfile profile, Path path, Property<O> property, ProgressCallback<Void> callback);

	@Override
	public void onClose(LaunchProfile profile) {}
}
