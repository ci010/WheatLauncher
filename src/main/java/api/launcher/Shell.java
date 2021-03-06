package api.launcher;

import api.launcher.auth.Authorize;
import api.launcher.auth.AuthorizeProxy;
import api.launcher.profile.Profile;
import api.launcher.profile.ProfileProxy;
import api.launcher.version.MinecraftVersion;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.launcher.game.ResourcePack;
import net.launcher.game.ServerInfo;
import net.launcher.game.WorldInfo;
import net.launcher.game.mods.ModContainer;
import net.launcher.utils.resource.Resource;

import java.util.ResourceBundle;
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

	ResourceBundle getLanguageBundle();

	default <T> T buildAndExecuteImmediately(String id, String... args) {return executeImmediately(buildTask(id, args));}

	default <T> Task<T> buildAndExecute(String id, String... args) {return execute(buildTask(id, args));}

	@SuppressWarnings("unchecked")
	default <T> Task<T> buildTask(String id, String... args)
	{
		TaskProvider taskProvider = getAllTaskProviders().getByKey(id);
		if (taskProvider == null) return null;
		return (Task<T>) taskProvider.createTask(this, args);
	}

	default AuthorizeProxy getAuthorizeProxy() {return getInstance(AuthorizeProxy.class);}

	default ProfileProxy getProfileProxy() {return getInstance(ProfileProxy.class);}

	default View<MinecraftVersion> getAllVersions() {return getView(MinecraftVersion.class);}

	default View<Authorize> getAllAuthorizes() {return getView(Authorize.class);}

	default View<Profile> getAllProfile() {return getView(Profile.class);}

	default View<WorldInfo> getAllWorlds() {return getView(WorldInfo.class);}

	default View<ServerInfo> getAllServers() {return getView(ServerInfo.class);}

	default View<ResourcePack> getAllResourcePacks() {return getView(ResourcePack.class);}

	default View<Resource<ResourcePack>> getAllResourcePacksResources()
	{
		View resourceView = getView("resourcepacks", Resource.class);
		return (View<Resource<ResourcePack>>) resourceView;
	}

	default View<ModContainer> getAllMods() {return getView(ModContainer.class);}

	default View<Resource<ModContainer<?>>> getAllModsResources()
	{
		View resourceView = getView("mods", Resource.class);
		return (View<Resource<ModContainer<?>>>) resourceView;
	}
}
