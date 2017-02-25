package net.launcher.services;

import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.utils.MessageUtils;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

/**
 * @author ci010
 */
public class PingTask implements Runnable, Callable<ServerStatus>
{
	private ServerInfo info;
	private ServerStatus status;
	private Callback<ServerInfo> callback;
	private SocketChannel channel;

	public PingTask(ServerInfo info, Callback<ServerInfo> callable, SocketChannel channel)
	{
		this.info = info;
		this.callback = callable;
		this.channel = channel;
	}

	public PingTask(ServerInfo info, ServerStatus status, SocketChannel channel)
	{
		this.info = info;
		this.channel = channel;
		this.status = status;
	}

	@Override
	public void run()
	{
		String ip = info.getHostName();
		if (ip == null)
		{
			callback.failed(new IllegalStateException("The server info's host name is empty!"));
			return;
		}
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
			status.setPingToServer(System.currentTimeMillis() - startTime);
			if (callback != null)
				callback.done(info);
		}
		catch (IOException e)
		{
			if (callback != null)
				callback.failed(new IOException());
		}
	}

	@Override
	public ServerStatus call() throws Exception
	{
		String ip = info.getHostName();
		if (ip == null) throw new IllegalStateException("The server info's host name is empty!");
		long l = System.currentTimeMillis();
		ByteBuffer byteBuffer = ByteBuffer.allocate(16);
		byteBuffer.put((byte) 9);
		byteBuffer.put((byte) 0x01);
		byteBuffer.putLong(l);
		byteBuffer.flip();
		if (channel == null || !channel.isConnected()) channel = SocketChannel.open(MessageUtils.getAddress(ip));
		if (channel == null || !channel.isConnected()) throw new IOException("Cannot connect to the channel");

		channel.write(byteBuffer);
		byteBuffer.clear();
		channel.read(byteBuffer);
		byteBuffer.flip();

		byteBuffer.get(); //length
		byteBuffer.get(); //id
		long startTime = byteBuffer.getLong();
		status.setPingToServer(System.currentTimeMillis() - startTime);
		return this.status;
	}
}
