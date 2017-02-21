package net.launcher.api;

import net.launcher.DownloadCenter;
import net.launcher.LaunchElementManager;
import net.launcher.TaskCenter;
import net.launcher.assets.MinecraftAssetsManager;
import net.launcher.auth.AuthManager;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.resourcepack.ResourcePackManager;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author ci010
 */
public interface LauncherContext
{
	Collection<LaunchElementManager> getAllElementsManagers();

	<T> Optional<LaunchElementManager<T>> getElementManager(Class<T> clz);

	TaskCenter getTaskCenter();

	LaunchProfileManager getProfileManager();

	AuthManager getAuthManager();

	MinecraftAssetsManager getAssetsManager();

	DownloadCenter getDownloadCenter();

	ResourcePackManager getResourcePackManager();

	<T> Optional<T> getComponent(Class<T> tClass);

	<T> Optional<T> getComponent(Class<T> tClass, String id);

	<T> void registerComponent(Class<? super T> clz, T o);

	<T> void registerComponent(Class<? super T> clz, T o, String id);

	ScheduledExecutorService getService();

	ScheduledExecutorService getService(String id);
}
