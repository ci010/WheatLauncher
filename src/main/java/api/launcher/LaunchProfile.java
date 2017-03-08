package api.launcher;

import api.launcher.setting.Setting;
import api.launcher.setting.SettingType;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;
import net.launcher.assets.MinecraftVersion;
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
	String getDisplayName();

	StringProperty displayNameProperty();

	void setDisplayName(String displayName);

	String getVersion();

	StringProperty versionProperty();

	void setVersion(String version);

	MinecraftVersion getMcVersion();

	ObjectBinding<MinecraftVersion> versionBinding();

	ReadOnlyObjectProperty<WindowSize> resolutionProperty();

	ReadOnlyIntegerProperty memoryProperty();

	ReadOnlyObjectProperty<JavaEnvironment> javaEnvironmentProperty();

	WindowSize getResolution();

	void setResolution(WindowSize resolution);

	int getMemory();

	void setMemory(int memory);

	ReadOnlyObjectProperty<MinecraftDirectory> minecraftLocationProperty();

	MinecraftDirectory getMinecraftLocation();

	void setMinecraftLocation(MinecraftDirectory minecraftLocation);

	JavaEnvironment getJavaEnvironment();

	void setJavaEnvironment(JavaEnvironment javaEnvironment);

	ObservableMap<String, Setting> gameSettingsProperty();

	Optional<Setting> getGameSetting(SettingType setting);

	void addGameSetting(Setting setting);

	Collection<Setting> getAllGameSettings();

	String getId();

	long getCreatedDate();

	Source getSource();

	enum Source
	{
		CREATED, IMPORTED
	}
}
