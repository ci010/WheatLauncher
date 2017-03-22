package api.launcher.profile;

import api.launcher.version.MinecraftVersion;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableMap;

/**
 * @author ci010
 */
public interface VersionProxy extends MinecraftVersion
{
	ReadOnlyStringProperty versionIdProperty();

	@Override
	ObservableMap<String, String> getMetadata();
}
