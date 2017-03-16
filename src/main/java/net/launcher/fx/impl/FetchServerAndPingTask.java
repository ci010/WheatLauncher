package net.launcher.fx.impl;

import javafx.concurrent.Task;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.services.HandshakeTask;
import net.launcher.services.HandshakeTaskLegacy;
import net.launcher.services.PingTask;
import net.launcher.utils.MessageUtils;

import java.nio.channels.SocketChannel;

/**
 * @author ci010
 */
public class FetchServerAndPingTask extends Task<ServerStatus>
{
	private ServerInfo info;

	public FetchServerAndPingTask(ServerInfo info)
	{
		this.info = info;
	}

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
}
