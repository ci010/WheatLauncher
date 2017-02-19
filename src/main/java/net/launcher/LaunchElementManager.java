package net.launcher;

import javafx.collections.ObservableList;
import net.launcher.profile.LaunchProfile;

/**
 * @author ci010
 */
public interface LaunchElementManager<T> extends LaunchHandler
{
	ObservableList<T> getAllElement();

	ObservableList<T> getIncludeElementContainer(LaunchProfile profile);
}
