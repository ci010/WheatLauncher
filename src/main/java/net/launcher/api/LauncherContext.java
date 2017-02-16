package net.launcher.api;

import net.launcher.AuthProfile;
import net.launcher.DownloadCenter;
import net.launcher.LaunchElementManager;
import net.launcher.TaskCenter;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.resourcepack.ResourcePackManager;
import net.launcher.version.MinecraftAssetsManager;

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

	AuthProfile getAuthProfile();

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
