package net.launcher;

import javafx.stage.Stage;
import net.launcher.api.TaskCenter;
import net.launcher.assets.MinecraftAssetsManager;
import net.launcher.assets.MinecraftWorldManager;
import net.launcher.auth.AuthManager;
import net.launcher.mod.ModManager;
import net.launcher.profile.LaunchProfile;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.resourcepack.ResourcePackManager;
import org.to2mbn.jmccc.launch.Launcher;
import org.to2mbn.jmccc.launch.ProcessListener;
import org.to2mbn.jmccc.option.LaunchOption;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author ci010
 */
public abstract class LaunchCore
{
	public abstract TaskCenter getTaskCenter();

	public abstract LaunchProfileManager getProfileManager();

	public abstract AuthManager getAuthManager();

	public abstract MinecraftAssetsManager getAssetsManager();

	public abstract MinecraftWorldManager getWorldManager();

	public abstract ResourcePackManager getResourcePackManager();

	public abstract DownloadCenter getDownloadCenter();

	public abstract ModManager getModManager();

	public void launch() throws Exception
	{
		final LaunchProfile selected = getProfileManager().selecting();
		LaunchOption option = buildOption(selected);
//		for (LaunchElementManager manager : getAllElementsManagers()) manager.onLaunch(option, selected);
		Launcher launcher = buildLauncher();
		ProcessListener listener = getProcessListener();
		Process launch = launcher.launch(option, new ProcessListener()
		{
			@Override
			public void onLog(String log)
			{
				listener.onLog(log);
			}

			@Override
			public void onErrorLog(String log)
			{
				listener.onErrorLog(log);
			}

			@Override
			public void onExit(int code)
			{
				listener.onExit(code);
//				for (LaunchElementManager manager : getAllElementsManagers())
//					manager.onClose(option, selected);
			}
		});
	}

	protected abstract LaunchOption buildOption(LaunchProfile selected) throws IOException;

	protected abstract Launcher buildLauncher();

	protected abstract ProcessListener getProcessListener();

	public abstract void init(Path root, Stage stage) throws Exception;

	public abstract void destroy() throws IOException;

	public abstract <T> Optional<T> getComponent(Class<T> tClass);

	public abstract <T> Optional<T> getComponent(Class<T> tClass, String id);

	public abstract <T> void registerComponent(Class<? super T> clz, T o);

	public abstract <T> void registerComponent(Class<? super T> clz, T o, String id);

	public abstract ScheduledExecutorService getService();

	public abstract ScheduledExecutorService getService(String id);
}
