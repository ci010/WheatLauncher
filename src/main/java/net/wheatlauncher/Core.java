package net.wheatlauncher;

import api.launcher.*;
import api.launcher.event.ErrorEvent;
import api.launcher.event.LaunchEvent;
import api.launcher.event.LauncherInitEvent;
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
import net.launcher.game.ServerInfo;
import net.launcher.mod.ModManagerBuilder;
import net.launcher.resourcepack.IOGuardResourcePackManager;
import net.launcher.setting.SettingMinecraft;
import org.to2mbn.jmccc.launch.Launcher;
import org.to2mbn.jmccc.launch.LauncherBuilder;
import org.to2mbn.jmccc.launch.ProcessListener;
import org.to2mbn.jmccc.option.LaunchOption;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Version;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

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

	public Path getRoot()
	{
		return root;
	}

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
	public void launchServer(ServerInfo info)
	{

	}

	@Override
	public void launch() throws Exception
	{
		LaunchProfile selected = getProfileManager().selecting();
		Version version = getAssetsManager().getRepository().buildVersion(selected.getMcVersion());

		LaunchOption option = new LaunchOption(version, () -> authProfile.getCache(),
				new MinecraftDirectory(root.toFile()));
		option.setRuntimeDirectory(new MinecraftDirectory(root.resolve("profiles").resolve(selected.getId()).toFile()));
		option.setWindowSize(selected.getResolution());

		ARML.bus().postEvent(LaunchEvent.launch(option, selected));
		ioContext.saveAll();

		Launcher launcher = LauncherBuilder.create().nativeFastCheck(true).printDebugCommandline
				(true).useDaemonThreads(true).build();
		Process launch = launcher.launch(option, new ProcessListener()
		{
			@Override
			public void onLog(String log) {ARML.logger().info(log);}

			@Override
			public void onErrorLog(String log) {ARML.logger().warning(log);}

			@Override
			public void onExit(int code)
			{
				ARML.bus().postEvent(LaunchEvent.exit(option, selected, code));
			}
		});
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
				.register(ResourcePackManager.class, new IOGuardResourcePackManager())
				.register(MinecraftServerManager.class, new IOGuardMinecraftServerManager());

		ARML.bus().postEvent(new LauncherInitEvent.Register()).getRegisteredIO().forEach(builder::register);

		this.ioContext = builder.build();
		this.ioContext.loadAll();

		this.serverManager = ioContext.load(MinecraftServerManager.class);
		this.authProfile = ioContext.load(AuthManager.class);
		this.profileManager = ioContext.load(LaunchProfileManager.class);
		this.assetsManager = ioContext.load(MinecraftAssetsManager.class);
		this.worldManager = ioContext.load(MinecraftWorldManager.class);
		this.resourcePackManager = ioContext.load(ResourcePackManager.class);

		Path mods = this.getRoot().resolve("mods");
		Files.createDirectories(mods);
		this.modManager = ModManagerBuilder.create(mods).build();
		ARML.bus().postEvent(new ModuleLoadedEvent<>(modManager));
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
	public <T> Task<T> runTask(Task<T> tTask)
	{
		if (tTask == null) return null;
		listenTask(tTask);
		ARML.async().submit(tTask);
		return tTask;
	}

	@Override
	public void runTasks(Collection<Task<?>> tasks)
	{
		if (tasks == null || tasks.isEmpty()) return;
		history.addAll(0, tasks);
		for (Task<?> task : tasks) listenTask(task);
		for (Task<?> task : tasks) ARML.async().submit(task);
	}

	@Override
	public <T> Task<T> listenTask(Task<T> task)
	{
		if (task == null) return null;
		//create delegate to keep away from memory leak... hopefully...
		history.add(0, new WorkerDelegate<>(task.exceptionProperty(), task.titleProperty(),
				task.messageProperty(), task.stateProperty(), task.progressProperty(), task.totalWorkProperty(),
				task.runningProperty(),
				task.valueProperty()));
		task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> event.getSource().getException().printStackTrace());
		return task;
	}

	@Override
	public void reportError(String title, Throwable throwable)
	{
		Objects.requireNonNull(throwable);
		Objects.requireNonNull(title);
		history.add(new DummyWorker(throwable, title));
		ARML.bus().postEvent(new ErrorEvent(throwable));
		errors.add(throwable);
	}

	@Override
	public ObservableList<Worker<?>> getAllWorkerHistory() {return history;}

	//@formatter:off
	private static ReadOnlyObjectProperty<Worker.State> dummyState = new SimpleObjectProperty<>(Worker.State.FAILED);
	private static ReadOnlyDoubleProperty dummyDouble = new SimpleDoubleProperty(1);
	private static ReadOnlyBooleanProperty dummyBoolean = new SimpleBooleanProperty();
	private static ReadOnlyObjectProperty dummyObj = new SimpleObjectProperty();

	private class DummyWorker extends WorkerDelegate<Object>
	{
		DummyWorker(Throwable throwable, String title)
		{
			super(new SimpleObjectProperty<>(throwable),new SimpleStringProperty(title), new SimpleStringProperty(),
					dummyState, dummyDouble, dummyDouble, dummyBoolean, dummyObj);
			((StringProperty) message).bind(Bindings.createStringBinding(throwable::getMessage));
		}
	}

	private class WorkerDelegate<T> implements Worker<T>
	{
		private ReadOnlyObjectProperty<Throwable> exceptionObjectProperty;
		private ReadOnlyStringProperty title;
		protected ReadOnlyStringProperty message;
		private ReadOnlyObjectProperty<Worker.State> state;
		private ReadOnlyDoubleProperty progress, total;
		private ReadOnlyBooleanProperty isRunning;
		private ReadOnlyObjectProperty<T> dummyObj;
		public WorkerDelegate(ReadOnlyObjectProperty<Throwable> exceptionObjectProperty, ReadOnlyStringProperty title,
							  ReadOnlyStringProperty message, ReadOnlyObjectProperty<State> dummyState,
							  ReadOnlyDoubleProperty progress, ReadOnlyDoubleProperty total, ReadOnlyBooleanProperty dummyBoolean,
							  ReadOnlyObjectProperty<T> dummyObj)
		{
			this.exceptionObjectProperty = exceptionObjectProperty;
			this.title = title;
			this.message = message;
			this.state = dummyState;
			this.progress = progress;
			this.total = total;
			this.isRunning = dummyBoolean;
			this.dummyObj = dummyObj;
		}
		public State getState() {return state.get();}
		public ReadOnlyObjectProperty<State> stateProperty() {return state;}
		public T getValue() {return dummyObj.get();}
		public ReadOnlyObjectProperty<T> valueProperty() {return dummyObj;}
		public Throwable getException() {return exceptionObjectProperty.get();}
		public ReadOnlyObjectProperty<Throwable> exceptionProperty() {return exceptionObjectProperty;}
		public double getWorkDone() {return progress.get();}
		public ReadOnlyDoubleProperty workDoneProperty() {return progress;}
		public double getTotalWork() {return total.get();}
		public ReadOnlyDoubleProperty totalWorkProperty() {return total;}
		public double getProgress() {return progress.get();}
		public ReadOnlyDoubleProperty progressProperty() {return progress;}
		public boolean isRunning() {return isRunning.get();}
		public ReadOnlyBooleanProperty runningProperty() {return isRunning;}
		public String getMessage() {return message.get();}
		public ReadOnlyStringProperty messageProperty() {return message;}
		public String getTitle() {return title.get();}
		public ReadOnlyStringProperty titleProperty() {return title;}
		public boolean cancel() {return false;}
	}
	//@formatter:on

}
