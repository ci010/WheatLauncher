package net.wheatlauncher;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.stage.Stage;
import net.launcher.DownloadCenter;
import net.launcher.DownloadCenterImpl;
import net.launcher.LaunchCore;
import net.launcher.LaunchElementManager;
import net.launcher.api.ARML;
import net.launcher.api.EventBus;
import net.launcher.api.LauncherContext;
import net.launcher.api.TaskCenter;
import net.launcher.assets.IOGuardMinecraftAssetsManager;
import net.launcher.assets.IOGuardMinecraftWorldManager;
import net.launcher.assets.MinecraftAssetsManager;
import net.launcher.assets.MinecraftWorldManager;
import net.launcher.auth.AuthManager;
import net.launcher.auth.IOGuardAuth;
import net.launcher.game.ResourcePack;
import net.launcher.game.forge.ForgeMod;
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
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author ci010
 */
public class Core extends LaunchCore implements LauncherContext, TaskCenter, ARML
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
	protected LaunchOption buildOption(LaunchProfile selected) throws IOException
	{
		LaunchOption option = null;
		for (Function<LaunchOption, LaunchOption> chain : launchOptionChain)
			option = chain.apply(option);
		return option;
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
		ARML.logger().info("Start to init");

		if (!Files.exists(root))
			Files.createDirectories(root);
		this.root = root;
		Files.createDirectories(getProfilesRoot());

		this.downloadCenter = new DownloadCenterImpl();
		this.managers = new HashMap<>();

		Path mods = this.getRoot().resolve("mods");
		Files.createDirectories(mods);
		this.managers.put(ForgeMod.class, modManager = ModManagerBuilder.create(mods).build());
		Path resPacks = this.getRoot().resolve("resourcepacks");

		Files.createDirectories(resPacks);
		this.managers.put(ResourcePack.class, this.resourcePackManager =
				ResourcePackMangerBuilder.create(resPacks, this.executorService)
						.build());

		//main module io start
		this.ioContext = IOGuardContextScheduled.Builder.create(this.root, executorService)
				.register(LaunchProfileManager.class, new IOGuardProfile())
				.register(AuthManager.class, new IOGuardAuth())
				.register(MinecraftAssetsManager.class, new IOGuardMinecraftAssetsManager())
				.register(MinecraftWorldManager.class, new IOGuardMinecraftWorldManager())
				.build();
		this.authProfile = ioContext.load(AuthManager.class);
		this.profileManager = ioContext.load(LaunchProfileManager.class);
		this.assetsManager = ioContext.load(MinecraftAssetsManager.class);
		this.worldManager = ioContext.load(MinecraftWorldManager.class);

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

	@Override
	public LauncherContext getContext()
	{
		return this;
	}

	@Override
	public EventBus getBus()
	{
		return null;
	}

	@Override
	public Logger getLogger()
	{
		return null;
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
