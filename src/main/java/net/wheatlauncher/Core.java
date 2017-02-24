package net.wheatlauncher;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import net.launcher.DownloadCenter;
import net.launcher.DownloadCenterImpl;
import net.launcher.LaunchCore;
import net.launcher.api.*;
import net.launcher.assets.IOGuardMinecraftAssetsManager;
import net.launcher.assets.IOGuardMinecraftWorldManager;
import net.launcher.assets.MinecraftAssetsManager;
import net.launcher.assets.MinecraftWorldManager;
import net.launcher.auth.AuthManager;
import net.launcher.auth.IOGuardAuth;
import net.launcher.mod.ModManager;
import net.launcher.mod.ModManagerBuilder;
import net.launcher.profile.LaunchProfile;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.resourcepack.ResourcePackManager;
import net.launcher.resourcepack.ResourcePackMangerBuilder;
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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import static net.launcher.api.LauncherInitEvent.LAUNCHER_INIT;

/**
 * @author ci010
 */
class Core implements LauncherContext, TaskCenter, LaunchCore
{
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

	private Path root;
	private LaunchProfileManager profileManager;
	private AuthManager authProfile;
	private MinecraftAssetsManager assetsManager;
	private ModManager modManager;
	private ResourcePackManager resourcePackManager;
	private MinecraftWorldManager worldManager;
	private DownloadCenter downloadCenter;

	private IOGuardContext ioContext;

	private List<Function<LaunchOption, LaunchOption>> launchOptionChain;

	public Path getRoot()
	{
		return root;
	}

	public Path getProfilesRoot()
	{
		return root.resolve("profiles");
	}

	@Override
	public TaskCenter getTaskCenter() {return this;}

	@Override
	public LaunchProfileManager getProfileManager()
	{
		return this.profileManager;
	}

	@Override
	public AuthManager getAuthManager()
	{
		return authProfile;
	}

	@Override
	public MinecraftAssetsManager getAssetsManager()
	{
		return assetsManager;
	}

	@Override
	public MinecraftWorldManager getWorldManager() {return worldManager;}

	@Override
	public ModManager getModManager()
	{
		return modManager;
	}

	@Override
	public DownloadCenter getDownloadCenter() {return downloadCenter;}

	@Override
	public ResourcePackManager getResourcePackManager()
	{
		return resourcePackManager;
	}

	@Override
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

	private LaunchOption buildOption(LaunchProfile selected) throws IOException
	{
		LaunchOption option = null;
		for (Function<LaunchOption, LaunchOption> chain : launchOptionChain)
			option = chain.apply(option);
		return option;
	}

	private Launcher buildLauncher()
	{
		return LauncherBuilder.create().nativeFastCheck(true).printDebugCommandline
				(true).useDaemonThreads(true).build();
	}

	private ProcessListener getProcessListener()
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
	public void init(Path root) throws Exception
	{
		ARML.logger().info("Start to init");

		this.root = root;
		Files.createDirectories(getProfilesRoot());

		this.downloadCenter = new DownloadCenterImpl();

		IOGuardContextScheduled.Builder builder = IOGuardContextScheduled.Builder.create(this.root, executorService)
				.register(LaunchProfileManager.class, new IOGuardProfile())
				.register(AuthManager.class, new IOGuardAuth())
				.register(MinecraftAssetsManager.class, new IOGuardMinecraftAssetsManager())
				.register(MinecraftWorldManager.class, new IOGuardMinecraftWorldManager());

		ARML.bus().postEvent(new LauncherInitEvent(LAUNCHER_INIT)).getRegisteredIO().forEach(builder::register);

		this.ioContext = builder.build();
		this.ioContext.loadAll();

		this.authProfile = ioContext.load(AuthManager.class);
		this.profileManager = ioContext.load(LaunchProfileManager.class);
		this.assetsManager = ioContext.load(MinecraftAssetsManager.class);
		this.worldManager = ioContext.load(MinecraftWorldManager.class);

		Path mods = this.getRoot().resolve("mods");
		Files.createDirectories(mods);
		this.modManager = ModManagerBuilder.create(mods).build();
		ARML.bus().postEvent(new ModuleLoadedEvent<>(modManager));

		Path resPacks = this.getRoot().resolve("resourcepacks");
		Files.createDirectories(resPacks);
		this.resourcePackManager = ResourcePackMangerBuilder.create(resPacks, this.executorService).build();
		ARML.bus().postEvent(new ModuleLoadedEvent<>(resourcePackManager));

		assetsManager.getRepository().refreshVersion().run();
		resourcePackManager.update().run();
		modManager.update().run();

		//main module io end
		assert profileManager.getSelectedProfile() != null;
		assert profileManager.selecting() != null;
		ARML.logger().info("Complete init");
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
		ARML.logger().info("Shutdown");
	}

	private ObservableList<Worker<?>> workers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
	private ObservableList<Throwable> errors = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	@Override
	public Task<?> runTask(Task<?> tTask)
	{
		if (tTask == null) return tTask;
		workers.add(tTask);
		tTask.workDoneProperty().addListener(workerListener);
		tTask.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> reportError(event.getSource().getException()));
		executorService.submit(tTask);
		return tTask;
	}

	@Override
	public void runTasks(Collection<Task<?>> tasks)
	{
		if (tasks == null || tasks.isEmpty()) return;
		workers.addAll(tasks);
		for (Task<?> task : tasks)
		{
			task.workDoneProperty().addListener(workerListener);
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> reportError(event.getSource().getException()));
		}
		for (Task<?> task : tasks) executorService.submit(task);
	}

	@Override
	public void reportError(Throwable throwable)
	{
		Objects.requireNonNull(throwable);
		errors.add(throwable);
	}

	private InvalidationListener workerListener = new InvalidationListener()
	{
		@Override
		public void invalidated(javafx.beans.Observable observable)
		{
			Worker worker = (Worker) ((Property) observable).getBean();
			if (worker == null) return;
			if (workers.remove(worker))
			{
				Throwable exception = worker.getException();
				if (exception != null) reportError(exception);
				observable.removeListener(this);
			}
		}
	};

	@Override
	public ObservableList<Throwable> getAllErrors()
	{
		return errors;
	}

	@Override
	public ObservableList<Worker<?>> getAllRunningWorkers()
	{
		return workers;
	}
}
