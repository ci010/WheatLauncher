package net.launcher;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;

import java.util.Optional;

/**
 * @author ci010
 */
public interface LaunchProfileManager
{
	LaunchProfile newProfileAndSelect(String name);

	LaunchProfile newProfile(String name);

	Optional<LaunchProfile> getProfile(String name);

	void select(String profileName);

	ReadOnlyProperty<LaunchProfile> selectedProfileProperty();

	ReadOnlyListProperty<LaunchProfile> allProfilesProperty();
}
