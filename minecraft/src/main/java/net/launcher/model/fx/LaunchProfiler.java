package net.launcher.model.fx;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableMap;
import net.launcher.model.Profile;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.WindowSize;

/**
 * @author ci010
 */
public interface LaunchProfiler extends Profile
{
	ObservableMap<String, Profile> getAllProfile();

	void loadProfile(Profile profile);

	ReadOnlyIntegerProperty minMemoryProperty();

	ReadOnlyIntegerProperty maxMemoryProperty();

	ReadOnlyStringProperty nameProperty();

	ReadOnlyStringProperty versionProperty();

	ReadOnlyObjectProperty<WindowSize> resolutionProperty();

	ReadOnlyObjectProperty<JavaEnvironment> javaLocationProperty();
}
