package net.launcher.impl;

import javafx.concurrent.Task;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.services.HandshakeTask;
import net.launcher.services.HandshakeTaskLegacy;
import net.launcher.utils.MessageUtils;

import java.nio.channels.SocketChannel;

/**
 * @author ci010
 */
public class FetchServerInfoTask extends Task<ServerStatus>
{
	private ServerInfo info;

	public FetchServerInfoTask(ServerInfo info)
	{
		this.info = info;
	}

	@Override
	protected ServerStatus call() throws Exception
	{
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
}
