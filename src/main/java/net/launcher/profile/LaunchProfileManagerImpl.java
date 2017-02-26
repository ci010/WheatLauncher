package net.launcher.profile;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import api.launcher.LaunchProfileManager;
import api.launcher.event.ProfileEvent;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class LaunchProfileManagerImpl implements LaunchProfileManager
{
	private ObservableMap<String, LaunchProfile> map, view;

	private Consumer<LaunchProfile> createConsumer;
	private Consumer<LaunchProfile> deleteConsumer;
	private BiConsumer<LaunchProfile, LaunchProfile> copyConsumer;

	private ObservableList<LaunchProfile> profiles = FXCollections.observableArrayList();
	private StringProperty selectedProfile = new SimpleStringProperty();

	LaunchProfileManagerImpl(List<LaunchProfile> profiles, Consumer<LaunchProfile> createConsumer, Consumer<LaunchProfile> deleteConsumer, BiConsumer<LaunchProfile, LaunchProfile> copyConsumer)
	{
		this.profiles.addAll(profiles);
		this.createConsumer = createConsumer;
		this.deleteConsumer = deleteConsumer;
		this.copyConsumer = copyConsumer;
		this.map = FXCollections.observableMap(new TreeMap<>());
		for (LaunchProfile profile : profiles) map.put(profile.getId(), profile);
		this.view = FXCollections.unmodifiableObservableMap(map);
	}

	@Override
	public String getSelectedProfile()
	{
		return selectedProfile.get();
	}

	@Override
	public StringProperty selectedProfileProperty()
	{
		return selectedProfile;
	}

	@Override
	public void setSelectedProfile(String id)
	{
		Objects.requireNonNull(id);
		if (!map.containsKey(id)) throw new IllegalArgumentException("profile.select.exist");
		this.selectedProfile.set(id);
	}

	@Override
	public LaunchProfile newProfile()
	{
		return newProfile("");
	}

	@Override
	public LaunchProfile newProfile(String name)
	{
		Objects.requireNonNull(name);
		LaunchProfile profile = new LaunchProfile();
		createConsumer.accept(profile);
		profile.setDisplayName(name);
		reg0(profile);
		ARML.bus().postEvent(new ProfileEvent(profile, ProfileEvent.CREATE));
		return profile;
	}

	@Override
	public LaunchProfile copyProfile(String id)
	{
		Objects.requireNonNull(id);
		LaunchProfile launchProfile = map.get(id);
		if (launchProfile == null)
			throw new IllegalArgumentException("profile.exist");
		LaunchProfile copy = newProfile();
		copyConsumer.accept(launchProfile, copy);
		doCopy(launchProfile, copy);
		return copy;
	}

	@Override
	public void deleteProfile(String id)
	{
		Objects.requireNonNull(id);
		if (!map.containsKey(id)) throw new IllegalArgumentException("profile.delete.exist");
		if (map.size() == 1) throw new IllegalArgumentException("profile.delete.one");
		if (map.isEmpty()) throw new IllegalArgumentException("profile.delete.empty");
		LaunchProfile launchProfile = map.get(id);
		deleteConsumer.accept(launchProfile);
		unreg0(launchProfile);
		ARML.bus().postEvent(new ProfileEvent(launchProfile, ProfileEvent.CREATE));
	}

	@Override
	public Optional<LaunchProfile> getProfile(String id)
	{
		return Optional.ofNullable(map.get(id));
	}

	@Override
	public ObservableMap<String, LaunchProfile> getProfilesMap() {return view;}

	@Override
	public ObservableList<LaunchProfile> getAllProfiles() {return this.profiles;}

	private ObjectBinding<LaunchProfile> selectingInstance = Bindings.createObjectBinding(() ->
			getProfilesMap().get(getSelectedProfile()), selectedProfileProperty());

	public ObjectBinding<LaunchProfile> selectingInstanceBinding() {return selectingInstance;}

	@Override
	public LaunchProfile selecting() {return selectingInstance.get();}

	private void doCopy(LaunchProfile launchProfile, LaunchProfile copy)
	{
		copy.setMemory(launchProfile.getMemory());
		copy.setVersion(launchProfile.getVersion());
		copy.setResolution(launchProfile.getResolution());
		copy.setJavaEnvironment(launchProfile.getJavaEnvironment());
		copy.setDisplayName(launchProfile.getDisplayName() + ".copy");
	}

	private void reg0(LaunchProfile profile)
	{
		profiles.add(profile);
		map.put(profile.getId(), profile);
	}

	private void unreg0(LaunchProfile profile)
	{
		profiles.remove(profile);
		map.remove(profile.getId());
	}
}
