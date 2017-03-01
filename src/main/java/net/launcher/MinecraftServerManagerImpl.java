package net.launcher;

import api.launcher.ARML;
import api.launcher.MinecraftServerManager;
import api.launcher.event.ServerEvent;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.services.HandshakeTask;
import net.launcher.services.HandshakeTaskLegacy;
import net.launcher.services.PingTask;
import net.launcher.utils.MessageUtils;

import java.nio.channels.SocketChannel;
import java.nio.file.Path;

/**
 * @author ci010
 */
public class MinecraftServerManagerImpl implements MinecraftServerManager
{
	private ObservableList<ServerInfo> infos;

	public MinecraftServerManagerImpl(ObservableList<ServerInfo> infos)
	{
		this.infos = infos;
		infos.addListener((ListChangeListener<ServerInfo>) c ->
		{
			while (c.next())
			{
				for (ServerInfo serverInfo : c.getAddedSubList())
					ARML.bus().postEvent(new ServerEvent(ServerEvent.ADD, serverInfo));
				for (ServerInfo serverInfo : c.getRemoved())
					ARML.bus().postEvent(new ServerEvent(ServerEvent.REMOVE, serverInfo));
			}
		});
	}

	public MinecraftServerManagerImpl()
	{
		this(FXCollections.observableArrayList());
	}

	@Override
	public ObservableList<ServerInfo> getAllServers() {return infos;}

	@Override
	public Task<ServerInfo[]> importServerInfos(Path dataFile)
	{
		return new Task<ServerInfo[]>()
		{
			@Override
			protected ServerInfo[] call() throws Exception
			{
				return new ServerInfo[0];
			}
		};
	}

	@Override
	public Task<Void> exportServerInfos()
	{
		return new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				return null;
			}
		};
	}

	@Override
	public Task<ServerStatus> fetchInfo(ServerInfo info)
	{
		return new Task<ServerStatus>()
		{
			@Override
			protected ServerStatus call() throws Exception
			{
				updateTitle("PingServer");
				updateMessage("fetch server info");
				SocketChannel open = SocketChannel.open(MessageUtils.getAddress(info.getHostName()));
				try {return new HandshakeTask(info, open).call();}
				catch (Exception e)
				{
					try
					{
						return new HandshakeTaskLegacy(info, open).call();
					}
					catch (Exception e1)
					{
						e1.addSuppressed(e);
						throw e1;
					}
				}
			}
		};
	}

	@Override
	public Task<ServerStatus> fetchInfoAndWaitPing(ServerInfo info)
	{
		return new Task<ServerStatus>()
		{
			@Override
			protected ServerStatus call() throws Exception
			{
				updateTitle("PingServer");
				updateMessage("fetch server info and wait");
				updateProgress(0, 2);
				SocketChannel open = SocketChannel.open(MessageUtils.getAddress(info.getHostName()));
				updateProgress(1, 2);
				try {return new PingTask(info, new HandshakeTask(info, open).call(), open).call();}
				catch (Exception e)
				{
					try
					{
						return new PingTask(info, new HandshakeTaskLegacy(info, open).call(), open).call();
					}
					catch (Exception e1)
					{
						e1.addSuppressed(e);
						throw e1;
					}
				}
			}
		};
	}
}
