package net.launcher.fx;

import api.launcher.EventBus;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.launcher.fx.auth.Authorizer;
import net.launcher.fx.profile.LaunchProfiler;
import net.launcher.game.ResourcePack;
import net.launcher.game.ServerInfo;
import net.launcher.game.WorldInfo;
import net.launcher.game.mods.ModContainer;
import net.launcher.model.Authorize;
import net.launcher.model.Profile;

import java.util.concurrent.Callable;

/**
 * @author ci010
 */
public interface Shell extends Context
{
	static Shell instance() {return ShellBoost.getInstance();}

	static EventBus bus() {return instance().getEventBus();}

	View<TaskProvider> getAllTaskProviders();

	<T> Task<T> execute(Task<T> task);

	<T> T executeImmediately(Task<T> task);

	<T> Task<T> execute(String title, Callable<T> task);

	<T> T executeImmediately(String title, Callable<T> task);

	ObservableList<Task<?>> getTaskRecords();

	EventBus getEventBus();

	default Task<?> buildTask(String id, String... args)
	{
		TaskProvider taskProvider = getAllTaskProviders().getByKey(id);
		if (taskProvider == null) return null;
		return taskProvider.createTask(this, args);
	}

	default Authorizer getAuthorizer() {return getInstance(Authorizer.class);}

	default LaunchProfiler getLaunchProfiler() {return getInstance(LaunchProfiler.class);}

	default View<Authorize> getAllAuthorizes() {return getView(Authorize.class);}

	default View<Profile> getAllProfile() {return getView(Profile.class);}

	default View<WorldInfo> getAllWorlds() {return getView(WorldInfo.class);}

	default View<ServerInfo> getAllServers() {return getView(ServerInfo.class);}

	default View<ResourcePack> getAllResourcePacks() {return getView(ResourcePack.class);}

	default View<ModContainer> getAllMods() {return getView(ModContainer.class);}
}
