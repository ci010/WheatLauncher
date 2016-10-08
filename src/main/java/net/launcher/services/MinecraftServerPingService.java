package net.launcher.services;

import net.launcher.game.ServerInfo;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Shutdownable;

import java.util.concurrent.Future;

/**
 * @author ci010
 */
public interface MinecraftServerPingService extends Shutdownable
{
	Future<ServerInfo> fetchInfo(ServerInfo info, Callback<ServerInfo> callback);

	Future<ServerInfo> fetchInfoAndWaitPing(ServerInfo info, Callback<ServerInfo> callback);
}
