package net.launcher.api;

import net.launcher.DownloadCenter;
import net.launcher.assets.MinecraftAssetsManager;
import net.launcher.assets.MinecraftWorldManager;
import net.launcher.auth.AuthManager;
import net.launcher.mod.ModManager;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.resourcepack.ResourcePackManager;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author ci010
 */
public interface LauncherContext
{
	TaskCenter getTaskCenter();

	LaunchProfileManager getProfileManager();

	AuthManager getAuthManager();

	MinecraftAssetsManager getAssetsManager();

	DownloadCenter getDownloadCenter();

	ResourcePackManager getResourcePackManager();

	MinecraftWorldManager getWorldManager();

	ModManager getModManager();

	<T> Optional<T> getComponent(Class<T> tClass);

	<T> Optional<T> getComponent(Class<T> tClass, String id);

	<T> void registerComponent(Class<? super T> clz, T o);

	<T> void registerComponent(Class<? super T> clz, T o, String id);

	ScheduledExecutorService getService();

	ScheduledExecutorService getService(String id);
}
