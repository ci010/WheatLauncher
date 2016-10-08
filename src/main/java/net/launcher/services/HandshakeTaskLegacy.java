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
class HandshakeTaskLegacy implements Runnable
{
	private ServerInfo info;
	private Callback<ServerInfo> callback;
	private SocketChannel channel;

	HandshakeTaskLegacy(ServerInfo info, Callback<ServerInfo> callback, SocketChannel channel)
	{
		this.info = info;
		this.callback = callback;
		this.channel = channel;
	}

	@Override
	public void run()
	{
		try
		{
			String[] strings = pingServerLegacy0(info.getHostName(), 25565);
			String gameVersion = strings[0], MOTO = strings[1];

			int players = -1, capability = -1;
			try
			{
				players = Integer.parseInt(strings[2]);
				capability = Integer.parseInt(strings[3]);
			}
			catch (Exception ignored) {}
			info.setProtocolVersion(-1);
			info.setServerMOTD(MOTO);
			info.setGameVersion(gameVersion);
			info.setOnlinePlayersInfo(players, capability);
		}
		catch (IOException e)
		{
			callback.failed(e);
		}
	}

	private String[] pingServerLegacy0(String serverIp, int port) throws IOException
	{
		if (channel == null)
			channel = SocketChannel.open(MessageUtils.getAddress(info.getHostName()));
		if (channel == null || !channel.isConnected())
			throw new IOException("Cannot open channel to " + info.getHostName());

		ByteBuffer buffer = ByteBuffer.allocate(256);
		buffer.put((byte) 254);
		buffer.put((byte) 1);
		buffer.put((byte) 250);
		char[] achar = "MC|PingHost".toCharArray();
		buffer.putShort((short) achar.length);

		for (char c0 : achar) buffer.putChar(c0);

		buffer.putShort((short) (7 + 2 * serverIp.length()));
		buffer.put((byte) 127);
		achar = serverIp.toCharArray();
		buffer.putShort((short) achar.length);

		for (char c1 : achar) buffer.putChar(c1);

		buffer.putInt(25565);
		buffer.flip();

		channel.write(buffer);

		buffer.clear();

		channel.read(buffer);
		buffer.flip();

		for (int i = 0; i < 18; i++)
			buffer.get();

		byte[] bytes1 = new byte[buffer.limit() - 18];
		buffer.get(bytes1);

		StringBuilder builder = new StringBuilder();

		boolean isEven = false;
		for (byte b : bytes1)
		{
			if (!isEven)
				if (b == 0) builder.append("\0");
				else builder.append((char) b);
			isEven = !isEven;
		}
		return builder.toString().split("\0");
	}
}
