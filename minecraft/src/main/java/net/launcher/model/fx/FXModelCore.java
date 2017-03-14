package net.launcher.model.fx;

import net.launcher.game.ResourcePack;
import net.launcher.game.ServerStatus;
import net.launcher.game.WorldInfo;
import net.launcher.game.mods.ModContainer;
import net.launcher.model.Authorize;
import net.launcher.model.Profile;
import net.launcher.model.fx.auth.Authorizer;
import net.launcher.model.fx.profile.LaunchProfiler;

/**
 * @author ci010
 */
public interface FXModelCore
{
	ObservableView<Authorize> getAllAuthorizes();

	Authorizer getAuthorizer();

	ObservableView<Profile> getAllProfile();

	LaunchProfiler getLaunchProfiler();

	ObservableView<WorldInfo> getAllWorlds();

	ObservableView<ServerStatus> getAllServers();

	ObservableView<ResourcePack> getAllResourcePacks();

	ObservableView<ModContainer<?>> getAllMods();

	<T> ObservableView<T> getAllElements(Class<T> tClass);
}
