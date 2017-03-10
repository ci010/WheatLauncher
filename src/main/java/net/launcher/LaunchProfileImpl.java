package net.launcher;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import api.launcher.MinecraftAssetsManager;
import api.launcher.event.ProfileEvent;
import api.launcher.setting.Setting;
import api.launcher.setting.SettingType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.launcher.assets.MinecraftVersion;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ci010
 */
public class LaunchProfileImpl implements LaunchProfile
{
	private final String id;
	private final long createdDate;
	private final Source source;

	private StringProperty parent = new SimpleStringProperty();

	private StringProperty displayName = new SimpleStringProperty("");
	private StringProperty version = new SimpleStringProperty();

	//preLaunch arg
	private ObjectProperty<WindowSize> resolution = new SimpleObjectProperty<>(WindowSize.window(856, 482));
	private ObjectProperty<JavaEnvironment> javaEnvironment = new SimpleObjectProperty<>(JavaEnvironment.current());
	private IntegerProperty memory = new SimpleIntegerProperty(512);

	private ObjectProperty<MinecraftDirectory> minecraftLocation = new SimpleObjectProperty<>(new MinecraftDirectory());

	private ObservableMap<String, Setting> gameSettingInstanceMap = FXCollections.observableHashMap();

	public LaunchProfileImpl(String id, long createdDate, Source source)
	{
		this.id = id;
		this.createdDate = createdDate;
		this.source = source;
	}

	public LaunchProfileImpl(String id)
	{
		this(id, Calendar.getInstance().getTimeInMillis(), Source.CREATED);
	}

	public LaunchProfileImpl()
	{
		this(Long.toString(System.currentTimeMillis()), Calendar.getInstance().getTimeInMillis(),
				Source.CREATED);
	}

	public LaunchProfileImpl(Source source)
	{
		this(Long.toString(System.currentTimeMillis()), Calendar.getInstance().getTimeInMillis(),
				source);
	}

	public String getParent() {return parent.get();}

	public StringProperty parentProperty() {return parent;}

	public void setParent(String parent) {this.parent.set(parent);}

	@Override
	public String getDisplayName() {return displayName.get();}

	@Override
	public StringProperty displayNameProperty() {return displayName;}

	@Override
	public void setDisplayName(String displayName) {this.displayName.set(displayName);}

	@Override
	public String getVersion() {return version.get();}

	@Override
	public StringProperty versionProperty() {return version;}

	@Override
	public void setVersion(String version)
	{
		this.version.set(version);
		ARML.bus().postEvent(new ProfileEvent(this, ProfileEvent.VERSION_CHANGE));
	}

	private ObjectBinding<MinecraftVersion> mcVersion = Bindings.createObjectBinding(() ->
	{
		MinecraftAssetsManager assetsManager = ARML.core().getAssetsManager();
		return assetsManager.getVersion(this.getVersion());
	}, this.versionProperty());

	@Override
	public MinecraftVersion getMcVersion() {return mcVersion.get();}

	@Override
	public ObjectBinding<MinecraftVersion> versionBinding() {return mcVersion;}

	@Override
	public ReadOnlyObjectProperty<WindowSize> resolutionProperty()
	{
		return resolution;
	}

	@Override
	public ReadOnlyIntegerProperty memoryProperty()
	{
		return memory;
	}

	@Override
	public ReadOnlyObjectProperty<MinecraftDirectory> minecraftLocationProperty()
	{
		return minecraftLocation;
	}

	@Override
	public ReadOnlyObjectProperty<JavaEnvironment> javaEnvironmentProperty()
	{
		return javaEnvironment;
	}

	@Override
	public WindowSize getResolution() {return resolution.get();}

	@Override
	public void setResolution(WindowSize resolution)
	{
		Objects.requireNonNull(resolution);
		this.resolution.set(resolution);
	}

	@Override
	public int getMemory() {return memory.get();}

	@Override
	public void setMemory(int memory) {this.memory.set(memory);}

	@Override
	public MinecraftDirectory getMinecraftLocation()
	{
		return minecraftLocation.get();
	}

	@Override
	public void setMinecraftLocation(MinecraftDirectory minecraftLocation)
	{
		Objects.requireNonNull(minecraftLocation);
		this.minecraftLocation.set(minecraftLocation);
	}

	@Override
	public JavaEnvironment getJavaEnvironment() {return javaEnvironment.get();}

	@Override
	public void setJavaEnvironment(JavaEnvironment javaEnvironment)
	{
		Objects.requireNonNull(javaEnvironment);
		if (!javaEnvironment.getJavaPath().getName().equals("java.exe"))
			throw new IllegalArgumentException("java.invalid");
		this.javaEnvironment.set(javaEnvironment);
	}

	@Override
	public ObservableMap<String, Setting> gameSettingsProperty()
	{
		return gameSettingInstanceMap;
	}

	@Override
	public Optional<Setting> getGameSetting(SettingType setting)
	{
		Objects.requireNonNull(setting);
		String id = setting.getClass().getName();
		return Optional.ofNullable(gameSettingInstanceMap.get(id));
	}

	@Override
	public void addGameSetting(Setting setting)
	{
		Objects.requireNonNull(setting);
		String id = setting.getGameSettingType().getClass().getName();
		gameSettingInstanceMap.put(id, setting);
	}

	@Override
	public Collection<Setting> getAllGameSettings()
	{
		return gameSettingInstanceMap.values();
	}

	@Override
	public String getId() {return id;}

	@Override
	public long getCreatedDate() {return createdDate;}

	@Override
	public Source getSource() {return source;}

	@Override
	public String toString()
	{
		return "LaunchProfile{" +
				"id='" + id + '\'' +
				", createdDate=" + createdDate +
				", source=" + source +
				", displayName=" + displayName +
				", version=" + version +
				'}';
	}
}
