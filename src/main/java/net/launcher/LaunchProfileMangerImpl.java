package net.launcher;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.to2mbn.jmccc.util.Builder;

import java.util.Optional;

/**
 * @author ci010
 */
class LaunchProfileMangerImpl implements LaunchProfileManager
{
	private ObservableList<LaunchProfile> profiles = FXCollections.observableArrayList();
	private ListProperty<LaunchProfile> profilesProperty = new SimpleListProperty<>(profiles);
	private ObjectProperty<LaunchProfile> selectingProfile = new SimpleObjectProperty<>();
	private Builder<LaunchProfile> factory;

	LaunchProfileMangerImpl(Builder<LaunchProfile> factory) {this.factory = factory;}

	@Override
	public LaunchProfile newProfileAndSelect(String name)
	{
		LaunchProfile profile = newProfile(name);
		select(name);
		return profile;
	}

	@Override
	public Optional<LaunchProfile> getProfile(String name)
	{
		for (LaunchProfile profile : profiles)
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
	public ReadOnlyProperty<LaunchProfile> selectedProfileProperty()
	{
		return selectingProfile;
	}

	@Override
	public ReadOnlyListProperty<LaunchProfile> allProfilesProperty()
	{
		return profilesProperty;
	}

	@Override
	public LaunchProfile newProfile(String name)
	{
		LaunchProfile profile = factory.build();
		profile.nameProperty().setValue(name);
		if (getProfile(profile.nameProperty().getValue()) != null)
			throw new IllegalArgumentException("duplicated launch profile!");
		profiles.add(profile);
		return profile;
	}

	@Override
	public LaunchProfile getSelectedProfile()
	{
		return selectingProfile.get();
	}
}
