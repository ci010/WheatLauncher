package net.launcher.fx.profile;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import net.launcher.fx.Context;
import net.launcher.model.Profile;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.WindowSize;

/**
 * @author ci010
 */
public interface LaunchProfiler extends Profile, Context
{
	void load(Profile profile);

	ReadOnlyStringProperty idProperty();

	ReadOnlyIntegerProperty minMemoryProperty();

	ReadOnlyIntegerProperty maxMemoryProperty();

	ReadOnlyStringProperty nameProperty();

	ReadOnlyStringProperty versionProperty();

	ReadOnlyObjectProperty<WindowSize> resolutionProperty();

	ReadOnlyObjectProperty<JavaEnvironment> javaLocationProperty();
}
