package net.wheatlauncher;

import api.launcher.*;
import api.launcher.event.LaunchEvent;
import api.launcher.event.LauncherInitEvent;
import api.launcher.event.MinecraftExitEvent;
import api.launcher.event.ModuleLoadedEvent;
import api.launcher.io.IOGuardContext;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import net.launcher.LaunchCore;
import net.launcher.assets.IOGuardMinecraftAssetsManager;
import net.launcher.assets.IOGuardMinecraftWorldManager;
import net.launcher.auth.IOGuardAuth;
import net.launcher.mod.ModManagerBuilder;
import net.launcher.resourcepack.ResourcePackMangerBuilder;
import net.launcher.setting.SettingMinecraft;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author ci010
 */
class Core implements LauncherContext, TaskCenter, LaunchCore
{
	private Path root;
	private LaunchProfileManager profileManager;
	private AuthManager authProfile;
	private MinecraftAssetsManager assetsManager;
	private ModManager modManager;
	private ResourcePackManager resourcePackManager;
	private MinecraftWorldManager worldManager;
	private MinecraftServerManager serverManager;
	private IOGuardContext ioContext;

	private List<Function<LaunchOption, LaunchOption>> launchOptionChain;

	public Path getRoot()
	{
		return root;
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
	public MinecraftServerManager getServerManager()
	{
		return serverManager;
	}

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

		ARML.bus().postEvent(new LaunchEvent(option, selected));

		Launcher launcher = buildLauncher();
		Process launch = launcher.launch(option, new ProcessListener()
		{
			@Override
			public void onLog(String log)
			{
			}

			@Override
			public void onErrorLog(String log)
			{
			}

			@Override
			public void onExit(int code) {ARML.bus().postEvent(new MinecraftExitEvent(code));}
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

	@Override
	public void init(Path root) throws Exception
	{
		ARML.logger().info("Start to init");

		this.root = root;

		SettingMinecraft.INSTANCE.getID();

		IOGuardContextScheduled.Builder builder = IOGuardContextScheduled.Builder.create(this.root, ARML.async())
				.register(LaunchProfileManager.class, new IOGuardProfile())
				.register(AuthManager.class, new IOGuardAuth())
				.register(MinecraftAssetsManager.class, new IOGuardMinecraftAssetsManager())
				.register(MinecraftWorldManager.class, new IOGuardMinecraftWorldManager())
				.register(MinecraftServerManager.class, new IOGuardMinecraftServerManager());

		ARML.bus().postEvent(new LauncherInitEvent.Register()).getRegisteredIO().forEach(builder::register);

		this.ioContext = builder.build();
		this.ioContext.loadAll();

		this.serverManager = ioContext.load(MinecraftServerManager.class);
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
		this.resourcePackManager = ResourcePackMangerBuilder.create(resPacks, ARML.async()).build();
		ARML.bus().postEvent(new ModuleLoadedEvent<>(resourcePackManager));

		resourcePackManager.update().run();
		modManager.update().run();

		ARML.logger().info("Complete init");
	}

	@Override
	public void destroy() throws Exception
	{
		ioContext.destroy();
		ARML.logger().info("Shutdown");
	}

	private ObservableList<Throwable> errors = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
	private ObservableList<Worker<?>> history = FXCollections.synchronizedObservableList(FXCollections
			.observableList(new LinkedList<>()));

	@Override
	public Task<?> runTask(Task<?> tTask)
	{
		if (tTask == null) return null;
		history.add(0, tTask);
		tTask.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> event.getSource().getException().printStackTrace());
		ARML.async().submit(tTask);
		return tTask;
	}

	@Override
	public void runTasks(Collection<Task<?>> tasks)
	{
		if (tasks == null || tasks.isEmpty()) return;
		history.addAll(0, tasks);
		for (Task<?> task : tasks)
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> event.getSource().getException().printStackTrace());
		for (Task<?> task : tasks) ARML.async().submit(task);
	}

	@Override
	public void reportError(String title, Throwable throwable)
	{
		Objects.requireNonNull(throwable);
		Objects.requireNonNull(title);
		history.add(new DummyWorker(throwable, title));
		errors.add(throwable);
	}

	@Override
	public ObservableList<Worker<?>> getAllWorkerHistory() {return history;}

	//@formatter:off
	private static ReadOnlyObjectProperty<Worker.State> dummyState = new SimpleObjectProperty<>(Worker.State.FAILED);
	private static ReadOnlyDoubleProperty dummyDouble = new SimpleDoubleProperty(1);
	private static ReadOnlyBooleanProperty dummyBoolean = new SimpleBooleanProperty();
	private static ReadOnlyObjectProperty dummyObj = new SimpleObjectProperty();

	private class DummyWorker implements Worker<Object>
	{
		private ObjectProperty<Throwable> exceptionObjectProperty = new SimpleObjectProperty<>();
		private StringProperty title = new SimpleStringProperty();
		private StringProperty message = new SimpleStringProperty();
		DummyWorker(Throwable throwable, String title)
		{
			exceptionObjectProperty.set(throwable);
			this.title.set(title);
			message.bind(Bindings.createStringBinding(throwable::getMessage));
		}
		public State getState() {return dummyState.get();}
		public ReadOnlyObjectProperty<State> stateProperty() {return dummyState;}
		public Object getValue() {return dummyObj.get();}
		public ReadOnlyObjectProperty<Object> valueProperty() {return dummyObj;}
		public Throwable getException() {return exceptionObjectProperty.get();}
		public ReadOnlyObjectProperty<Throwable> exceptionProperty() {return exceptionObjectProperty;}
		public double getWorkDone() {return dummyDouble.get();}
		public ReadOnlyDoubleProperty workDoneProperty() {return dummyDouble;}
		public double getTotalWork() {return dummyDouble.get();}
		public ReadOnlyDoubleProperty totalWorkProperty() {return dummyDouble;}
		public double getProgress() {return dummyDouble.get();}
		public ReadOnlyDoubleProperty progressProperty() {return dummyDouble;}
		public boolean isRunning() {return dummyBoolean.get();}
		public ReadOnlyBooleanProperty runningProperty() {return dummyBoolean;}
		public String getMessage() {return exceptionObjectProperty.get().getMessage();}
		public ReadOnlyStringProperty messageProperty() {return message;}
		public String getTitle() {return title.get();}
		public ReadOnlyStringProperty titleProperty() {return title;}
		public boolean cancel() {return false;}
	}
	//@formatter:on

}
