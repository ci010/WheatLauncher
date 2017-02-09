package net.launcher;

import net.launcher.profile.LaunchProfile;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.version.MinecraftVersionManager;
import org.to2mbn.jmccc.launch.Launcher;
import org.to2mbn.jmccc.launch.ProcessListener;
import org.to2mbn.jmccc.option.LaunchOption;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author ci010
 */
public abstract class LaunchCore
{
	public abstract Collection<LaunchElementManager> getAllElementsManagers();

	public abstract <T> Optional<LaunchElementManager<T>> getElementManager(Class<T> clz);

	public abstract LaunchProfileManager getProfileManager();

	public abstract AuthProfile getAuthProfile();

	public abstract MinecraftVersionManager getVersionManager();

	public void launch() throws Exception
	{
		final LaunchProfile selected = getProfileManager().selecting();
		LaunchOption option = buildOption();
		for (LaunchElementManager manager : getAllElementsManagers())
			manager.onLaunch(option, selected);
		Launcher launcher = buildLauncher();
		ProcessListener listener = getProcessListener();
		launcher.launch(option, new ProcessListener()
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
				for (LaunchElementManager manager : getAllElementsManagers())
					manager.onClose(option, selected);
			}
		});
	}

	protected abstract LaunchOption buildOption();

	protected abstract Launcher buildLauncher();

	protected abstract ProcessListener getProcessListener();

	public abstract void init() throws Exception;

	public abstract void destroy() throws IOException;

	public abstract ScheduledExecutorService getService();

	public abstract ScheduledExecutorService getService(String id);
}
