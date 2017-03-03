package api.launcher;

/**
 * @author ci010
 */
public interface LauncherContext
{
	LaunchProfileManager getProfileManager();

	AuthManager getAuthManager();

	MinecraftAssetsManager getAssetsManager();

	ResourcePackManager getResourcePackManager();

	MinecraftWorldManager getWorldManager();

	ModManager getModManager();

	MinecraftServerManager getServerManager();
}
