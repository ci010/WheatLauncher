package api.launcher.profile;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableMap;
import net.launcher.model.MinecraftVersion;

/**
 * @author ci010
 */
public interface VersionProxy extends MinecraftVersion
{
	ReadOnlyStringProperty versionIdProperty();

	@Override
	ObservableMap<String, String> getMetadata();
}
