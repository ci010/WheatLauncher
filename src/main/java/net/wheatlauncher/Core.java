package net.wheatlauncher;

import javafx.stage.Stage;
import net.launcher.*;
import net.launcher.api.LauncherContext;
import net.launcher.game.ResourcePack;
import net.launcher.game.forge.ForgeMod;
import net.launcher.mod.ModManagerBuilder;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.resourcepack.ResourcePackManager;
import net.launcher.resourcepack.ResourcePackMangerBuilder;
import net.launcher.version.IOGuardMinecraftAssetsManager;
import net.launcher.version.MinecraftAssetsManager;
import net.wheatlauncher.internal.io.IOGuardAuth;
import net.wheatlauncher.internal.io.IOGuardContext;
import net.wheatlauncher.internal.io.IOGuardContextScheduled;
import net.wheatlauncher.internal.io.IOGuardProfile;
import org.to2mbn.jmccc.launch.Launcher;
import org.to2mbn.jmccc.launch.LauncherBuilder;
import org.to2mbn.jmccc.launch.ProcessListener;
import org.to2mbn.jmccc.option.LaunchOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author ci010
 */
public class Core extends LaunchCore implements LauncherContext
{
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

	private Path root;
	private LaunchProfileManager profileManager;
	private AuthProfile authProfile;
	private MinecraftAssetsManager versionManager;
	private ResourcePackManager resourcePackManager;

	private IOGuardContext ioContext;
	private DownloadCenter downloadCenter;

	private WorldSaveMaintainer maintainer;

	private Map<Class, LaunchElementManager> managers;

	public Path getRoot()
	{
		return root;
	}

	public Path getProfilesRoot()
	{
		return root.resolve("profiles");
	}

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
	public TaskCenter getTaskCenter()
	{
		return null;
	}

	@Override
	public LaunchProfileManager getProfileManager()
	{
		return this.profileManager;
	}

	@Override
	public AuthProfile getAuthProfile()
	{
		return authProfile;
	}

	@Override
	public MinecraftAssetsManager getAssetsManager()
	{
		return versionManager;
	}

	@Override
	public DownloadCenter getDownloadCenter()
	{
		return downloadCenter;
	}

	@Override
	public ResourcePackManager getResourcePackManager()
	{
		return resourcePackManager;
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

	@Override
	public void init(Path root, Stage stage) throws Exception
	{
		Logger.trace("Start to init");

		if (!Files.exists(root))
			Files.createDirectories(root);
		this.root = root;
		Files.createDirectories(getProfilesRoot());

		this.maintainer = new WorldSaveMaintainer(this.root.resolve("saves"));

		this.downloadCenter = new DownloadCenterImpl();
		this.managers = new HashMap<>();

		Path mods = this.getRoot().resolve("mods");
		Files.createDirectories(mods);
		this.managers.put(ForgeMod.class, ModManagerBuilder.create(mods, this.executorService).build());
		Path resourcepacks = this.getRoot().resolve("resourcepacks");

		Files.createDirectories(resourcepacks);
		this.managers.put(ResourcePack.class, this.resourcePackManager = ResourcePackMangerBuilder.create(
				resourcepacks,
				this.executorService).build());

		//main module io start
		this.ioContext = IOGuardContextScheduled.Builder.create(this.root, executorService)
				.register(LaunchProfileManager.class, new IOGuardProfile())
				.register(AuthProfile.class, new IOGuardAuth())
				.register(MinecraftAssetsManager.class, new IOGuardMinecraftAssetsManager())
				.build();
		this.authProfile = ioContext.load(AuthProfile.class);
		this.versionManager = ioContext.load(MinecraftAssetsManager.class);
		this.profileManager = ioContext.load(LaunchProfileManager.class);
		this.versionManager.getRepository().update();

		resourcePackManager.update();
		//main module io end
		assert profileManager.getSelectedProfile() != null;
		assert profileManager.selecting() != null;

		Logger.trace("Complete init");
	}

	@Override
	public void destroy() throws IOException
	{
		try {ioContext.saveAll();}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		executorService.shutdown();
		Logger.trace("Shutdown");
	}

	@Override
	public <T> Optional<T> getComponent(Class<T> tClass)
	{
		return null;
	}

	@Override
	public <T> Optional<T> getComponent(Class<T> tClass, String id)
	{
		return null;
	}

	@Override
	public <T> void registerComponent(Class<? super T> clz, T o)
	{

	}

	@Override
	public <T> void registerComponent(Class<? super T> clz, T o, String id)
	{
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
