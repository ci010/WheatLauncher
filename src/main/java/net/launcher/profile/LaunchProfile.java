package net.launcher.profile;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingInstance;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ci010
 */
public class LaunchProfile
{
	private StringProperty version = new SimpleStringProperty();
	private ObjectProperty<WindowSize> resolution = new SimpleObjectProperty<>(WindowSize.window(856, 482));
	private IntegerProperty memory = new SimpleIntegerProperty(512);
	private ObjectProperty<MinecraftDirectory> minecraftLocation = new SimpleObjectProperty<>(new MinecraftDirectory());
	private ObjectProperty<JavaEnvironment> javaEnvironment = new SimpleObjectProperty<>(JavaEnvironment.current());

	private ObservableMap<String, GameSettingInstance> gameSettingInstanceMap = FXCollections.observableHashMap();

	private StringProperty displayName = new SimpleStringProperty("");

	private final String id;

	public LaunchProfile(String id) {this.id = id;}

	public LaunchProfile() {this.id = Long.toBinaryString(System.currentTimeMillis());}

	public String getId() {return id;}

	public String getDisplayName() {return displayName.get();}

	public StringProperty displayNameProperty() {return displayName;}

	public void setDisplayName(String displayName) {this.displayName.set(displayName);}

	public String getVersion() {return version.get();}

	public void setVersion(String version)
	{
		Objects.requireNonNull(version);
//		if (minecraftLocation.get().getVersion(version.getVersion()).exists())
		this.version.set(version);
//		else throw new IllegalArgumentException("invalid.version");
	}

	public ReadOnlyStringProperty versionProperty()
	{
		return version;
	}

	public ReadOnlyObjectProperty<WindowSize> resolutionProperty()
	{
		return resolution;
	}

	public ReadOnlyIntegerProperty memoryProperty()
	{
		return memory;
	}

	public ReadOnlyObjectProperty<MinecraftDirectory> minecraftLocationProperty()
	{
		return minecraftLocation;
	}

	public ReadOnlyObjectProperty<JavaEnvironment> javaEnvironmentProperty()
	{
		return javaEnvironment;
	}

	public WindowSize getResolution() {return resolution.get();}

	public void setResolution(WindowSize resolution)
	{
		Objects.requireNonNull(resolution);
		this.resolution.set(resolution);
	}

	public int getMemory() {return memory.get();}

	public void setMemory(int memory)
	{
		if (memory > Runtime.getRuntime().totalMemory())
			throw new IllegalArgumentException("memory.excesses");
		this.memory.set(memory);
	}

	public MinecraftDirectory getMinecraftLocation()
	{
		return minecraftLocation.get();
	}

	public void setMinecraftLocation(MinecraftDirectory minecraftLocation)
	{
		Objects.requireNonNull(minecraftLocation);
		this.minecraftLocation.set(minecraftLocation);
	}

	public JavaEnvironment getJavaEnvironment() {return javaEnvironment.get();}

	public void setJavaEnvironment(JavaEnvironment javaEnvironment)
	{
		Objects.requireNonNull(javaEnvironment);
		if (!javaEnvironment.getJavaPath().getName().equals("java.exe"))
			throw new IllegalArgumentException("java.invalid");
		this.javaEnvironment.set(javaEnvironment);
	}

	public ObservableMap<String, GameSettingInstance> gameSettingsProperty()
	{
		return gameSettingInstanceMap;
	}

	public Optional<GameSettingInstance> getGameSetting(GameSetting setting)
	{
		Objects.requireNonNull(setting);
		Objects.requireNonNull(setting.getClass().getAnnotation(GameSetting.ID.class));
		String id = setting.getClass().getAnnotation(GameSetting.ID.class).value();
		return Optional.ofNullable(gameSettingInstanceMap.get(id));
	}

	public void addGameSetting(GameSettingInstance instance)
	{
		Objects.requireNonNull(instance);
		GameSetting.ID annotation = instance.getGameSettingType().getClass().getAnnotation(GameSetting.ID.class);
		Objects.requireNonNull(annotation);
		String id = annotation.value();
		gameSettingInstanceMap.put(id, instance);
	}

	public Collection<GameSettingInstance> getAllGameSettings()
	{
		return gameSettingInstanceMap.values();
	}
}
