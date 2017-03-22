package api.launcher.version;

import javafx.collections.ObservableMap;

/**
 * @author ci010
 */
public interface MinecraftVersion
{
	String getVersionId();

	ObservableMap<String, String> getMetadata();
}
