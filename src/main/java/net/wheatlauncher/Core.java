package net.wheatlauncher;

import net.launcher.AuthProfile;
import net.launcher.Bootstrap;
import net.launcher.LaunchCore;
import net.launcher.LaunchElementManager;
import net.launcher.game.ResourcePack;
import net.launcher.game.forge.ForgeMod;
import net.launcher.mod.ModManagerBuilder;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.resourcepack.ResourcePackMangerBuilder;
import net.launcher.utils.Logger;
import net.wheatlauncher.internal.io.AuthIOGuard;
import net.wheatlauncher.internal.io.IOGuardContext;
import net.wheatlauncher.internal.io.ProfileIOGuard;
import org.to2mbn.jmccc.launch.Launcher;
import org.to2mbn.jmccc.launch.LauncherBuilder;
import org.to2mbn.jmccc.launch.ProcessListener;
import org.to2mbn.jmccc.option.LaunchOption;
import org.to2mbn.jmccc.util.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author ci010
 */
public class Core extends LaunchCore
{
	public static Core getInstance()
	{
		return (Core) Bootstrap.getCore();
	}

	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	private Timer timer = new Timer(true);

	public Timer getTimer()
	{
		return timer;
	}

	private Path root;
	private LaunchProfileManager profileManager;
	private AuthProfile authProfile;

	private IOGuardContext ioContext;

	private WorldSaveMaintainer maintainer;

	private Map<Class, LaunchElementManager> managers;

	public Path getRoot() {return root;}

	public Path getProfilesRoot() {return root.resolve("profiles");}

	@Override
	public Collection<LaunchElementManager> getAllElementsManagers()
	{
		return managers.values();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<LaunchElementManager<T>> getElementManager(Class<T> clz) {return Optional.ofNullable(managers.get(clz));}

	@Override
	public LaunchProfileManager getProfileManager() {return this.profileManager;}

	@Override
	public AuthProfile getAuthProfile() {return authProfile;}

	@Override
	protected LaunchOption buildOption()
	{
		return null;
	}

	@Override
	protected Launcher buildLauncher()
	{
		return LauncherBuilder.create().nativeFastCheck(true).printDebugCommandline
				(true).useDaemonThreads(true).build();
	}

	@Override
	protected ProcessListener getProcessListener()
	{
		return new ProcessListener()
		{
			@Override
			public void onLog(String log)
			{
				System.out.println(log);
			}

			@Override
			public void onErrorLog(String log)
			{
				System.out.println(log);
			}

			@Override
			public void onExit(int code)
			{
				System.out.println("exit " + code);
			}
		};
	}

	@Override
	public void init() throws Exception
	{
		Logger.trace("Start to init");

		this.initRoot();

		this.maintainer = new WorldSaveMaintainer(root.resolve("saves"));

		this.managers = new HashMap<>();
		this.managers.put(ForgeMod.class, ModManagerBuilder.create(
				this.getRoot().resolve("mods"),
				this.executorService).build());
		this.managers.put(ResourcePack.class, ResourcePackMangerBuilder.create(
				this.getRoot().resolve("resourcepacks"),
				this.executorService).build());

		//main module io start
		this.ioContext = IOGuardContext.Builder.create(this.root)
				.register(LaunchProfileManager.class, new ProfileIOGuard())
				.register(AuthProfile.class, new AuthIOGuard())
				.setTaskExecutor(t ->
				{
				}).build();
//				.setTaskExecutor(ioTask ->
//						executorService.submit(() ->
//						{
//							ioTask.performance(this.root);
//							return null;
//						})).build();
		this.profileManager = ioContext.load(LaunchProfileManager.class);
		this.authProfile = ioContext.load(AuthProfile.class);

		//main module io end
		assert profileManager.getSelectedProfile() != null;
		assert profileManager.selecting() != null;

		Logger.trace("Complete init");
	}

	private void initRoot() throws IOException
	{
		Path root;
		switch (Platform.CURRENT)
		{
			case WINDOWS:
				String appdata = System.getenv("APPDATA");
				root = Paths.get(appdata == null ? System.getProperty("user.home", ".") : appdata, ".launcher/");
				break;
			case LINUX:
				root = Paths.get(System.getProperty("user.home", "."), ".launcher/");
				break;
			case OSX:
				root = Paths.get("Library/Application Support/launcher/");
				break;
			default:
				root = Paths.get(System.getProperty("user.home", ".") + "/");
		}
		if (!Files.exists(root))
			Files.createDirectories(root);
		this.root = root;
		Files.createDirectories(getProfilesRoot());
	}

	@Override
	public void destroy() throws IOException
	{
		executorService.shutdown();
		ioContext.saveAll();
		timer.cancel();
		timer.purge();
		Logger.trace("Shutdown");
	}

	@Override
	public ScheduledExecutorService getService()
	{
		return executorService;
	}

	@Override
	public ScheduledExecutorService getService(String id)
	{
		return executorService;
	}
}
