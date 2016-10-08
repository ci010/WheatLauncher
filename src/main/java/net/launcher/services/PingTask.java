package net.launcher.services;

import net.launcher.game.ServerInfo;
import net.launcher.utils.MessageUtils;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ci010
 */
class PingTask implements Runnable
{
	private ServerInfo info;
	private Callback<ServerInfo> callback;
	private SocketChannel channel;

	PingTask(ServerInfo info, Callback<ServerInfo> callable, SocketChannel channel)
	{
		this.info = info;
		this.callback = callable;
		this.channel = channel;
	}

	@Override
	public void run()
	{
		String ip = info.getHostName();
		if (ip == null) return;
		long l = System.currentTimeMillis();
		try
		{
			ByteBuffer byteBuffer = ByteBuffer.allocate(16);
			byteBuffer.put((byte) 9);
			byteBuffer.put((byte) 0x01);
			byteBuffer.putLong(l);
			byteBuffer.flip();
			if (channel == null || !channel.isConnected()) channel = SocketChannel.open(MessageUtils.getAddress(ip));
			if (channel == null || !channel.isConnected())
			{
				if (callback != null)
					callback.failed(new IOException());
				return;
			}

			channel.write(byteBuffer);
			byteBuffer.clear();
			channel.read(byteBuffer);
			byteBuffer.flip();

			byteBuffer.get(); //length
			byteBuffer.get(); //id
			long startTime = byteBuffer.getLong();
			info.setPingToServer(System.currentTimeMillis() - startTime);
			if (callback != null)
				callback.done(info);
			channel.finishConnect();
			channel.close();
		}
		catch (IOException e)
		{
			if (callback != null)
				callback.failed(new IOException());
		}
	}
}
