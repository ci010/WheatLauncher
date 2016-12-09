package net.launcher.profile;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingInstance;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.util.Collection;
import java.util.Optional;

/**
 * @author ci010
 */
public interface LaunchProfile
{
	String getVersion();

	void setVersion(String version);

	WindowSize getResolution();

	void setResolution(WindowSize resolution);

	int getMemory();

	void setMemory(int memory);

	MinecraftDirectory getMinecraftLocation();

	void setMinecraftLocation(MinecraftDirectory minecraftLocation);

	JavaEnvironment getJavaEnvironment();

	void setJavaEnvironment(JavaEnvironment javaEnvironment);

	Optional<GameSettingInstance> getGameSetting(GameSetting setting);

	void addGameSetting(GameSettingInstance instance);

	Collection<GameSettingInstance> getAllGameSettings();

	ReadOnlyStringProperty versionProperty();

	ReadOnlyObjectProperty<WindowSize> resolutionProperty();

	ReadOnlyIntegerProperty memoryProperty();

	ReadOnlyObjectProperty<MinecraftDirectory> minecraftLocationProperty();

	ReadOnlyObjectProperty<JavaEnvironment> javaEnvironmentProperty();
}
