package net.launcher.profile;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ci010
 */
public class LaunchProfileManager
{
	private ObservableMap<String, LaunchProfile> map = FXCollections.observableMap(new TreeMap<>()),
			view = FXCollections.unmodifiableObservableMap(map);
	private ObservableList<LaunchProfile> profiles = FXCollections.observableArrayList();
	private Function<String, LaunchProfile> factory;
	private Consumer<String> deleteConsumer;

	private StringProperty selectedProfile = new SimpleStringProperty();

	LaunchProfileManager(Map<String, LaunchProfile> profileMap, Function<String, LaunchProfile> factory,
						 Consumer<String> deleteConsumer)
	{
		this.map.putAll(profileMap);
		this.factory = factory;
		this.deleteConsumer = deleteConsumer;
	}

	public String getSelectedProfile()
	{
		return selectedProfile.get();
	}

	public StringProperty selectedProfileProperty()
	{
		return selectedProfile;
	}

	public void setSelectedProfile(String id)
	{
		Objects.requireNonNull(id);
		if (!map.containsKey(id)) throw new IllegalArgumentException("profile.select.exist");
		this.selectedProfile.set(id);
	}

	private boolean containsName(String name)
	{
		for (LaunchProfile profile : map.values())
			if (profile.getDisplayName().equals(name))
				return true;
		return false;
	}

	public LaunchProfile newProfile()
	{
		String id = String.valueOf(System.currentTimeMillis());
		LaunchProfile profile = factory.apply(id);
		map.put(id, profile);
		return profile;
	}

	public LaunchProfile newProfile(String name)
	{
		Objects.requireNonNull(name);
		if (containsName(name))
			throw new IllegalArgumentException("profile.duplicate");
		String id = String.valueOf(System.currentTimeMillis());
		LaunchProfile profile = factory.apply(id);
		profile.setDisplayName(name);
		map.put(id, profile);
		return profile;
	}

	public void deleteProfile(String id)
	{
		Objects.requireNonNull(id);
		if (!map.containsKey(id)) throw new IllegalArgumentException("profile.delete.exist");
		if (map.size() == 1) throw new IllegalArgumentException("profile.delete.one");
		if (map.isEmpty()) throw new IllegalArgumentException("profile.delete.empty");
		deleteConsumer.accept(id);
		map.remove(id);
	}

	public Optional<LaunchProfile> getProfile(String id)
	{
		return Optional.ofNullable(map.get(id));
	}

	public ObservableMap<String, LaunchProfile> getProfilesMap() {return view;}

	public ObservableList<LaunchProfile> getAllProfiles() {return this.profiles;}

	public LaunchProfile selecting() {return getProfilesMap().get(getSelectedProfile());}

//	public void renameProfile(String profile, String newName)
//	{
//		Objects.requireNonNull(profile);
//		Objects.requireNonNull(newName);
//		Logger.trace("start to rename the profile " + profile + " into " + newName);
//
//		if (profile.equals(newName)) return;
//		if (!map.containsKey(profile)) throw new IllegalArgumentException("profile.rename.exist");
//		if (map.containsKey(newName)) throw new IllegalArgumentException("profile.rename.duplicate");
//		renameConsumer.accept(profile, newName);
//		Logger.trace("renaming the profile " + profile + " into " + newName);
//		this.map.put(newName, this.map.remove(profile));
//	}
}
