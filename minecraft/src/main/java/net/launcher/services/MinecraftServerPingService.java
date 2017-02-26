package net.launcher.services;

import net.launcher.game.ServerInfo;
import net.launcher.game.ServerInfoBase;
import net.launcher.game.ServerStatus;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Shutdownable;

import java.util.concurrent.Future;

/**
 * The service used to png Minecraft server.
 * <p>
 * Instantiated by {@link MinecraftServerPingServiceBuilder}.
 *
 * @author ci010
 * @see ServerInfoBase
 * @see ServerStatus
 * @see MinecraftServerPingServiceBuilder
 */
public interface MinecraftServerPingService extends Shutdownable
{
	/**
	 * Ping and fetch the information from a server async.
	 * <p>
	 * This method doesn't wait the pong return. So the {@link ServerStatus#pingToServer} might be not available when
	 * callback.
	 *
	 * @param info     The server info, and the container of the server status.
	 * @param callback The job callback.
	 * @return The future of the {@link ServerStatus}.
	 */
	Future<ServerStatus> fetchInfo(ServerInfo info, Callback<ServerInfo> callback);

	/**
	 * Ping and fetch the information from a server async.
	 * <p>
	 * Notice that this method will wait the pong return. It will make sure the {@link ServerStatus#pingToServer} is
	 * set.
	 *
	 * @param info     The server info, and the container of the server status.
	 * @param callback The job callback.
	 * @return The future of the {@link ServerStatus}.
	 */
	Future<ServerStatus> fetchInfoAndWaitPing(ServerInfo info, Callback<ServerInfo> callback);
}
