package api.launcher;

import api.launcher.setting.SettingManager;

/**
 * @author ci010
 */
public interface LauncherContext
{
	SettingManager getProfileSettingManager();

	LaunchProfileManager getProfileManager();

	AuthManager getAuthManager();

	MinecraftAssetsManager getAssetsManager();

	MinecraftWorldManager getWorldManager();

	MinecraftServerManager getServerManager();

	ResourcePackManager getResourcePackManager();

	ModManager getModManager();
}
