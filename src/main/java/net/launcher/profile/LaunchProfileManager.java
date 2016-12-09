package net.launcher.profile;

import javafx.collections.ObservableMap;

import java.util.Optional;

/**
 * @author ci010
 */
public interface LaunchProfileManager
{
	LaunchProfile newProfile(String name);

	void deleteProfile(String name);

	void renameProfile(String profile, String newName);

	Optional<LaunchProfile> getProfile(String name);

	ObservableMap<String, LaunchProfile> getAllProfiles();
}
