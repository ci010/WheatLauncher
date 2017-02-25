package api.launcher;

/**
 * @author ci010
 */
public interface LauncherContext
{
	TaskCenter getTaskCenter();

	LaunchProfileManager getProfileManager();

	AuthManager getAuthManager();

	MinecraftAssetsManager getAssetsManager();

	ResourcePackManager getResourcePackManager();

	MinecraftWorldManager getWorldManager();

	ModManager getModManager();

	MinecraftServerManager getServerManager();
}
