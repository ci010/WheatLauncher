package net.launcher.profile;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ci010
 */
public class LaunchProfileManager
{
	private ObservableMap<String, LaunchProfile> map = FXCollections.observableHashMap(),
			view = FXCollections.unmodifiableObservableMap(map);
	//	private Map<String, String> nameToId = new TreeMap<>();
	private Function<String, LaunchProfile> factory;
	private BiConsumer<String, String> renameConsumer;
	private Consumer<String> deleteConsumer;

	private StringProperty selectedProfile = new SimpleStringProperty();

	LaunchProfileManager(Map<String, LaunchProfile> profileMap, Function<String, LaunchProfile> factory,
						 BiConsumer<String, String> renameConsumer,
						 Consumer<String> deleteConsumer)
	{
		this.map.putAll(profileMap);
//		this.map.forEach((k, v) -> nameToId.put(v.getDisplayName(), k));
		this.factory = factory;
		this.renameConsumer = renameConsumer;
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

	public void setSelectedProfile(String selectedProperty)
	{
		Objects.requireNonNull(selectedProperty);
		if (!map.containsKey(selectedProperty)) throw new IllegalArgumentException("profile.select.exist");
		this.selectedProfile.set(selectedProperty);
	}

	public LaunchProfile newProfile(String id)
	{
		Objects.requireNonNull(id);
		if (map.containsKey(id))
			throw new IllegalArgumentException("profile.duplicate");
		LaunchProfile la = factory.apply(id);
		map.put(la.getId(), la);
//		nameToId.put(id, la.getId());
		return la;
	}

	public void deleteProfile(String id)
	{
		Objects.requireNonNull(id);
		if (!map.containsKey(id)) throw new IllegalArgumentException("profile.delete.exist");
		deleteConsumer.accept(id);
		map.remove(id);
	}

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

	public Optional<LaunchProfile> getProfile(String id)
	{
		return Optional.ofNullable(map.get(id));
	}

	public ObservableMap<String, LaunchProfile> getAllProfiles() {return view;}

	public LaunchProfile selecting() {return getAllProfiles().get(getSelectedProfile());}
}
