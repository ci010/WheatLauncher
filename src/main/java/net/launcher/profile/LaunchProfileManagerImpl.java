package net.launcher.profile;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ci010
 */
class LaunchProfileManagerImpl implements LaunchProfileManager
{
	private ObservableMap<String, LaunchProfile> map = FXCollections.observableHashMap(),
			view = FXCollections.unmodifiableObservableMap(map);
	private Function<String, LaunchProfile> factory;
	private BiConsumer<String, String> renameConsumer;
	private Consumer<String> deleteConsumer;

	private StringProperty selectedProfile = new SimpleStringProperty();

	LaunchProfileManagerImpl(Function<String, LaunchProfile> factory, BiConsumer<String, String> renameConsumer, Consumer<String> deleteConsumer)
	{
		this.factory = factory;
		this.renameConsumer = renameConsumer;
		this.deleteConsumer = deleteConsumer;
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
	public void setSelectedProfile(String selectedProperty)
	{
		Objects.requireNonNull(selectedProperty);
		if (!map.containsKey(selectedProperty)) throw new IllegalArgumentException("profile.select.exist");
		this.selectedProfile.set(selectedProperty);
	}

	@Override
	public LaunchProfile newProfile(String name)
	{
		Objects.requireNonNull(name);
		if (getProfile(name).isPresent())
			throw new IllegalArgumentException("profile.duplicate");
		LaunchProfile la = factory.apply(name);
		map.put(name, la);
		return la;
	}

	@Override
	public void deleteProfile(String name)
	{
		Objects.requireNonNull(name);
		if (!map.containsKey(name)) throw new IllegalArgumentException("profile.delete.exist");
		deleteConsumer.accept(name);
		map.remove(name);
	}

	@Override
	public void renameProfile(String profile, String newName)
	{
		Objects.requireNonNull(profile);
		Objects.requireNonNull(newName);

		if (profile.equals(newName)) return;
		if (!map.containsKey(profile)) throw new IllegalArgumentException("profile.rename.exist");
		if (map.containsKey(newName)) throw new IllegalArgumentException("profile.rename.duplicate");
		renameConsumer.accept(profile, newName);
		this.map.put(newName, this.map.remove(profile));
	}

	@Override
	public Optional<LaunchProfile> getProfile(String name)
	{
		return Optional.ofNullable(map.get(name));
	}

	@Override
	public ObservableMap<String, LaunchProfile> getAllProfiles() {return view;}
}
