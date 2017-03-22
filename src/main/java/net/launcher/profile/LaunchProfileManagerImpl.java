package net.launcher.profile;

import api.launcher.LaunchProfile;
import api.launcher.event.ProfileEvent;
import api.launcher.setting.SettingManager;
import api.launcher.setting.SettingMinecraft;
import api.launcher.setting.SettingMods;
import api.launcher.setting.SettingType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.launcher.LaunchProfileImpl;

import java.util.*;

/**
 * @author ci010
 */
public class LaunchProfileManagerImpl implements LaunchProfileManager, SettingManager
{
	private ObservableMap<String, LaunchProfile> map, view;

	private ObservableList<LaunchProfile> profiles = FXCollections.observableArrayList();
	private StringProperty selectedProfile = new SimpleStringProperty();
	private Map<Class<? extends SettingType>, SettingType> settingTypeMap = new HashMap<>();
	private List<SettingType> settingTypes = new ArrayList<>();

	LaunchProfileManagerImpl(List<LaunchProfile> profiles, List<SettingType> settingTypes,
							 Map<Class<? extends SettingType>, SettingType> settingTypeMap)
	{
		this.settingTypeMap = settingTypeMap;
		this.settingTypes = settingTypes;

		this.profiles.addAll(profiles);
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
		LaunchProfile profile = new LaunchProfileImpl();
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

	@Override
	public SettingMinecraft getSettingMinecraft()
	{
		return (SettingMinecraft) settingTypeMap.get(SettingMinecraft.class);
	}

	@Override
	public SettingMods getSettingMods()
	{
		return (SettingMods) settingTypeMap.get(SettingMods.class);
	}

	@Override
	public <T extends SettingType> Optional<T> find(Class<T> clz)
	{
		return Optional.ofNullable((T) settingTypeMap.get(clz));
	}

	@Override
	public List<SettingType> getAllSettingType()
	{
		return settingTypes;
	}
}
