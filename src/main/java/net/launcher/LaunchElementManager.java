package net.launcher;

import net.launcher.profile.LaunchProfile;
import org.to2mbn.jmccc.option.LaunchOption;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public interface LaunchElementManager<T> extends LaunchManager
{
	Set<T> getAllElement();

	List<T> getAllIncludedElement(LaunchProfile profile);

	void manipulateIncludeElement(LaunchProfile profile, Consumer<List<T>> manipulator);

	void onLaunch(LaunchOption option, LaunchProfile profile);

	void onClose(LaunchOption option, LaunchProfile profile);
}
