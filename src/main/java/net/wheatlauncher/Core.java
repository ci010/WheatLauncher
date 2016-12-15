package net.wheatlauncher;

import net.launcher.AuthModule;
import net.launcher.Bootstrap;
import net.launcher.LaunchCore;
import net.launcher.LaunchElementManager;
import net.launcher.game.ResourcePack;
import net.launcher.game.mod.Mod;
import net.launcher.mod.ModManagerBuilder;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.profile.LaunchProfileManagerBuilder;
import net.launcher.resourcepack.ResourcePackMangerBuilder;
import net.launcher.utils.Logger;
import net.wheatlauncher.internal.io.AuthIOGuard;
import net.wheatlauncher.internal.io.IOGuard;
import net.wheatlauncher.internal.io.IOGuardManger;
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

	//////////
	//Thread//
	//////////

	public Timer getTimer()
	{
		return timer;
	}

	////////
	//File//
	////////
	private Path root;
	private LaunchProfileManager profileManager;

	private ProfileIOGuard profileIOGuard;
	private WorldSaveMaintainer maintainer;

	private Map<Class, LaunchElementManager> managers;

	public Path getRoot() {return root;}

	public Path getArchivesRoot() {return root.resolve("archives");}

	public Path getBackupRoot() {return root.resolve("backup");}

	public Path getProfilesRoot() {return root.resolve("profiles");}

	@Override
	public Collection<LaunchElementManager> getAllElementsManagers()
	{
		return managers.values();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<LaunchElementManager<T>> getElementManager(Class<T> clz)
	{
		return Optional.ofNullable(managers.get(clz));
	}

	@Override
	public LaunchProfileManager getProfileManager()
	{
		return this.profileManager;
	}

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

	private IOGuardManger ioGuardManger;
	private IOGuard<AuthModule> authModuleIOGuard = new AuthIOGuard(this.getRoot(), new);

	@Override
	public void init() throws Exception
	{
		Logger.trace("Start to init");

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
		Files.createDirectories(getArchivesRoot());
		Files.createDirectories(getBackupRoot());
		Files.createDirectories(getProfilesRoot());
		this.maintainer = new WorldSaveMaintainer(root.resolve("saves"));
		this.profileManager = LaunchProfileManagerBuilder.buildDefault();
		this.setAuthModule(new AuthModule());
		profileManager.newProfile("default");
		this.getProfileManager().setSelectedProfile("default");
		this.managers = new HashMap<>();
		this.managers.put(Mod.class, ModManagerBuilder.create(this.getArchivesRoot().resolve("mods"),
				this.executorService).build());
		this.managers.put(ResourcePack.class, ResourcePackMangerBuilder.create(this.getArchivesRoot().resolve
				("resourcepacks"), this.executorService).build());

//		io = ProfileMangerIO.newService(root.toFile(), profileManager);
//		io.loadAll();
//		io.saveAll();
		Logger.trace("Start init");
	}

	@Override
	public void destroy() throws IOException
	{
		executorService.shutdown();
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
