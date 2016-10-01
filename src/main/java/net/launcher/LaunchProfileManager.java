package net.launcher;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;

import java.util.Optional;

/**
 * @author ci010
 */
public interface LaunchProfileManager
{
	ILaunchProfile newProfileAndSelect(String name);

	ILaunchProfile newProfile(String name);

	Optional<ILaunchProfile> getProfile(String name);

	void select(String profileName);

	ReadOnlyProperty<ILaunchProfile> selectedProfileProperty();

	ReadOnlyListProperty<ILaunchProfile> allProfilesProperty();
}
