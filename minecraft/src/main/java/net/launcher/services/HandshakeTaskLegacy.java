package net.launcher.services;

import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.utils.MessageUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

/**
 * @author ci010
 */
class HandshakeTaskLegacy implements Callable<ServerStatus>
{
	private ServerInfo info;
	private SocketChannel channel;

	HandshakeTaskLegacy(ServerInfo info, SocketChannel channel)
	{
		this.info = info;
		this.channel = channel;
	}

	@Override
	public ServerStatus call() throws Exception
	{
		String[] strings = pingServerLegacy0();
		String gameVersion = strings[1], MOTO = strings[2];

		int players = -1, capability = -1, protocol = -1;
		try
		{
			protocol = Integer.parseInt(strings[0]);
			players = Integer.parseInt(strings[3]);
			capability = Integer.parseInt(strings[4]);
		}
		catch (Exception ignored) {}
		ServerStatus status = new ServerStatus(gameVersion, MOTO, protocol, players, capability);
		info.setStatus(status);
		return status;
	}

	private String[] pingServerLegacy0() throws IOException
	{
		InetSocketAddress address = MessageUtils.getAddress(info.getHostName());
		if (channel == null)
			channel = SocketChannel.open(address);
		if (channel == null || !channel.isConnected())
			throw new IOException("Cannot channel channel to " + info.getHostName());

		ByteBuffer buffer = ByteBuffer.allocate(256);
		buffer.put((byte) 254);
		buffer.put((byte) 1);
		buffer.put((byte) 250);
		char[] achar = "MC|PingHost".toCharArray();
		buffer.putShort((short) achar.length);

		for (char c0 : achar) buffer.putChar(c0);

		buffer.putShort((short) (7 + 2 * address.getHostName().length()));
		buffer.put((byte) 127);
		achar = address.getHostName().toCharArray();
		buffer.putShort((short) achar.length);

		for (char c1 : achar) buffer.putChar(c1);

		buffer.putInt(address.getPort());
		buffer.flip();

		channel.write(buffer);

		buffer.clear();

		channel.read(buffer);
		buffer.flip();

		int head = Byte.toUnsignedInt(buffer.get());
		if (head == 255)
		{
			int size = buffer.getShort();

			byte[] bytes = new byte[size * 2];
			buffer.get(bytes);

			String content = new String(bytes, Charset.forName("UTF_16BE"));
			String[] split = content.split("\u0000");

			if (split[0].equals("\u00a71"))
				if (split.length == 6)
					return new String[]{split[1], split[2], split[3], split[4], split[5]};
		}
		throw new IOException("Cannot ping the server");
	}
}
