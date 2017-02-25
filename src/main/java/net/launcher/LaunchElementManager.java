package net.launcher;

import api.launcher.LaunchProfile;
import javafx.collections.ObservableList;

/**
 * @author ci010
 */
public interface LaunchElementManager<T>
{
	ObservableList<T> getAllElement();

	ObservableList<T> getIncludeElementContainer(LaunchProfile profile);
}
