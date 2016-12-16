package net.launcher.profile;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableMap;

import java.util.Optional;

/**
 * @author ci010
 */
public interface LaunchProfileManager
{
	String getSelectedProfile();

	ReadOnlyStringProperty selectedProfileProperty();

	void setSelectedProfile(String selectedProperty);

	LaunchProfile newProfile(String name);

	void deleteProfile(String name);

	void renameProfile(String profile, String newName);

	Optional<LaunchProfile> getProfile(String name);

	ObservableMap<String, LaunchProfile> getAllProfiles();

	default LaunchProfile selecting() {return getAllProfiles().get(getSelectedProfile());}
}
