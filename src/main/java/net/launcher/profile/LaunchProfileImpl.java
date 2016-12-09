package net.launcher.profile;

import javafx.beans.property.*;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingInstance;
import net.launcher.utils.StringUtils;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.util.*;

/**
 * @author ci010
 */
class LaunchProfileImpl implements LaunchProfile
{
	private StringProperty version = new SimpleStringProperty(StringUtils.EMPTY);
	private ObjectProperty<WindowSize> resolution = new SimpleObjectProperty<>(WindowSize.window(856, 482));
	private IntegerProperty memory = new SimpleIntegerProperty(512);
	private ObjectProperty<MinecraftDirectory> minecraftLocation = new SimpleObjectProperty<>(new MinecraftDirectory());
	private ObjectProperty<JavaEnvironment> javaEnvironment = new SimpleObjectProperty<>(JavaEnvironment.current());

	private Map<String, GameSettingInstance> gameSettingInstanceMap = new TreeMap<>();

	LaunchProfileImpl() {}

	@Override
	public String getVersion() {return version.get();}

	@Override
	public void setVersion(String version)
	{
		Objects.requireNonNull(version);
		if (minecraftLocation.get().getVersion(version).exists())
			this.version.set(version);
		else throw new IllegalArgumentException("invalid.version");
	}

	@Override
	public ReadOnlyStringProperty versionProperty()
	{
		return version;
	}

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
	public void setMemory(int memory)
	{
		if (memory > Runtime.getRuntime().totalMemory())
			throw new IllegalArgumentException("memory.excesses");
		this.memory.set(memory);
	}

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
	public Optional<GameSettingInstance> getGameSetting(GameSetting setting)
	{
		Objects.requireNonNull(setting);
		Objects.requireNonNull(setting.getClass().getAnnotation(GameSetting.ID.class));
		String id = setting.getClass().getAnnotation(GameSetting.ID.class).value();
		return Optional.ofNullable(gameSettingInstanceMap.get(id));
	}

	@Override
	public void addGameSetting(GameSettingInstance instance)
	{
		Objects.requireNonNull(instance);
		GameSetting.ID annotation = instance.getGameSettingType().getClass().getAnnotation(GameSetting.ID.class);
		Objects.requireNonNull(annotation);
		String id = annotation.value();
		gameSettingInstanceMap.put(id, instance);
	}

	@Override
	public Collection<GameSettingInstance> getAllGameSettings()
	{
		return gameSettingInstanceMap.values();
	}
}
