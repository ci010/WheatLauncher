package net.launcher;

import net.launcher.utils.ProgressCallback;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public interface LaunchElementManager<T extends LaunchElement>
{
	Set<T> getAllElement();

	Set<T> getAllIncludedElement(LaunchProfile profile);

	void manipulateIncludeElement(LaunchProfile profile, Consumer<List<T>> manipulator);

	void onImplementVirtualPath(Path path, LaunchProfile profile, ProgressCallback<Void> callback);

	void onClose(LaunchProfile profile);
}
