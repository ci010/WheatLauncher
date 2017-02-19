package net.launcher.profile;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.launcher.assets.MinecraftAssetsManager;
import net.launcher.assets.MinecraftVersion;
import net.launcher.setting.Setting;
import net.launcher.setting.SettingType;
import net.wheatlauncher.MainApplication;
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
public class LaunchProfile
{
	public enum Source
	{
		CREATED, IMPORTED
	}

	private final String id;
	private final long createdDate;
	private final Source source;

	private StringProperty parent = new SimpleStringProperty();

	private StringProperty displayName = new SimpleStringProperty("");
	private StringProperty version = new SimpleStringProperty();

	//launch arg
	private ObjectProperty<WindowSize> resolution = new SimpleObjectProperty<>(WindowSize.window(856, 482));
	private ObjectProperty<JavaEnvironment> javaEnvironment = new SimpleObjectProperty<>(JavaEnvironment.current());
	private IntegerProperty memory = new SimpleIntegerProperty(512);

	private ObjectProperty<MinecraftDirectory> minecraftLocation = new SimpleObjectProperty<>(new MinecraftDirectory());

	private ObservableMap<String, Setting> gameSettingInstanceMap = FXCollections.observableHashMap();

	public LaunchProfile(String id, long createdDate, Source source)
	{
		this.id = id;
		this.createdDate = createdDate;
		this.source = source;
	}

	public LaunchProfile(String id)
	{
		this(id, Calendar.getInstance().getTimeInMillis(), Source.CREATED);
	}

	public LaunchProfile()
	{
		this(Long.toString(System.currentTimeMillis()), Calendar.getInstance().getTimeInMillis(),
				Source.CREATED);
	}

	public LaunchProfile(Source source)
	{
		this(Long.toString(System.currentTimeMillis()), Calendar.getInstance().getTimeInMillis(),
				source);
	}

	public String getParent() {return parent.get();}

	public StringProperty parentProperty() {return parent;}

	public void setParent(String parent) {this.parent.set(parent);}

	public String getDisplayName() {return displayName.get();}

	public StringProperty displayNameProperty() {return displayName;}

	public void setDisplayName(String displayName) {this.displayName.set(displayName);}

	public String getVersion() {return version.get();}

	public StringProperty versionProperty() {return version;}

	public void setVersion(String version) {this.version.set(version);}

	private ObjectBinding<MinecraftVersion> mcVersion = Bindings.createObjectBinding(() ->
	{
		MinecraftAssetsManager assetsManager = MainApplication.getCore().getAssetsManager();
		return assetsManager.getVersion(this.getVersion());
	}, this.versionProperty());

	public MinecraftVersion getMcVersion() {return mcVersion.get();}

	public ObjectBinding<MinecraftVersion> versionBinding() {return mcVersion;}

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

	public void setMemory(int memory) {this.memory.set(memory);}

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

	public ObservableMap<String, Setting> gameSettingsProperty()
	{
		return gameSettingInstanceMap;
	}

	public Optional<Setting> getGameSetting(SettingType setting)
	{
		Objects.requireNonNull(setting);
		String id = setting.getID();
		return Optional.ofNullable(gameSettingInstanceMap.get(id));
	}

	public void addGameSetting(Setting setting)
	{
		Objects.requireNonNull(setting);
		String id = setting.getGameSettingType().getID();
		gameSettingInstanceMap.put(id, setting);
	}

	public Collection<Setting> getAllGameSettings()
	{
		return gameSettingInstanceMap.values();
	}

	public String getId() {return id;}

	public long getCreatedDate() {return createdDate;}

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
