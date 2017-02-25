package api.launcher;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;

import java.nio.file.Path;

/**
 * @author ci010
 */
public interface MinecraftServerManager
{
	ObservableList<ServerInfo> getAllServers();

	Task<ServerInfo[]> importServerInfos(Path dataFile);

	Task<Void> exportServerInfos();

	Task<ServerStatus> fetchInfo(ServerInfo info);

	Task<ServerStatus> fetchInfoAndWaitPing(ServerInfo info);
}
