package net.launcher;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;

/**
 * @author ci010
 */
class LaunchProfileMangerImpl implements LaunchProfileManager
{
	private ObservableList<ILaunchProfile> profiles = FXCollections.observableArrayList();
	private ListProperty<ILaunchProfile> profilesProperty = new SimpleListProperty<>(profiles);
	private ObjectProperty<ILaunchProfile> selectingProfile = new SimpleObjectProperty<>();

	LaunchProfileMangerImpl() {}

	@Override
	public ILaunchProfile newProfileAndSelect(String name)
	{
		ILaunchProfile profile = newProfile(name);
		select(name);
		return profile;
	}

	@Override
	public Optional<ILaunchProfile> getProfile(String name)
	{
		for (ILaunchProfile profile : profiles)
			if (profile.nameProperty().getValue().equals(name))
				return Optional.of(profile);
		return Optional.empty();
	}

	@Override
	public void select(String profileName)
	{
		selectingProfile.set(getProfile(profileName).orElse(this.profiles.size() == 0 ? newProfile("default") : this.profiles.get(0)));
	}

	@Override
	public ReadOnlyProperty<ILaunchProfile> selectedProfileProperty()
	{
		return selectingProfile;
	}

	@Override
	public ReadOnlyListProperty<ILaunchProfile> allProfilesProperty()
	{
		return profilesProperty;
	}

	@Override
	public ILaunchProfile newProfile(String name)
	{
		LaunchProfileImpl profile = new LaunchProfileImpl();
		profile.nameProperty().setValue(name);
		if (getProfile(profile.nameProperty().getValue()) != null)
			throw new IllegalArgumentException("duplicated launch profile!");
		profiles.add(profile);
		return profile;
	}
}
