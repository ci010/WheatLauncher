package api.launcher;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.Optional;

/**
 * @author ci010
 */
public interface LaunchProfileManager
{
	String getSelectedProfile();

	StringProperty selectedProfileProperty();

	void setSelectedProfile(String id);

	LaunchProfile newProfile();

	LaunchProfile newProfile(String name);

	LaunchProfile copyProfile(String id);

	void deleteProfile(String id);

	Optional<LaunchProfile> getProfile(String id);

	ObservableMap<String, LaunchProfile> getProfilesMap();

	ObservableList<LaunchProfile> getAllProfiles();

	LaunchProfile selecting();
}
