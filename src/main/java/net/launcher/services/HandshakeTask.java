package net.launcher.services;

import net.launcher.game.ServerInfo;
import net.launcher.utils.MessageUtils;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static net.launcher.utils.MessageUtils.*;

/**
 * @author ci010
 */
class HandshakeTask implements Runnable
{
	private ServerInfo info;
	private Callback<ServerInfo> callable;
	private SocketChannel channel;

	HandshakeTask(ServerInfo info, Callback<ServerInfo> callable, SocketChannel channel)
	{
		this.info = info;
		this.callable = callable;
		this.channel = channel;
	}

	@Override
	public void run()
	{
		try
		{
			String s = handshake(info);
			JSONObject object = new JSONObject(s);
			String description = object.getJSONObject("description").getString("text");
			String favicon = object.optString("favicon");

			JSONObject version = object.getJSONObject("version");

			String versionName = version.getString("name");
			int protocol = version.getInt("protocol");

			JSONObject players = object.getJSONObject("players");

			int max = players.getInt("max"), online = players.getInt("online");

			JSONArray sample = players.optJSONArray("sample");

			GameProfile[] profiles = new GameProfile[0];

			if (sample != null)
			{
				profiles = new GameProfile[sample.length()];

				for (int i = 0; i < sample.length(); i++)
				{
					JSONObject player = sample.getJSONObject(1);
					profiles[i] = new GameProfile(UUID.fromString(player.getString("id")), player.getString("name"));
				}
			}

			info.setServerMOTD(description);
			info.setGameVersion(versionName);
			info.setProtocolVersion(protocol);
			info.setOnlinePlayersInfo(online, max);
			info.setPlayerList(profiles);
			if (favicon.startsWith("data:image/png;base64,"))
				info.setServerIcon(favicon.substring("data:image/png;base64,".length()));

			if (object.has("modinfo"))
			{
				JSONObject modinfo = object.getJSONObject("modinfo");
				String type = modinfo.getString("type");
				JSONArray array = modinfo.getJSONArray("modList");
				boolean moddedClientAllowed = !modinfo.has("clientModsAllowed") || modinfo.getBoolean("clientModsAllowed");
				Map<String, String> modVersions = new TreeMap<>();
				for (int i = 0; i < array.length(); i++)
				{
					JSONObject mod = array.getJSONObject(i);
					modVersions.put(mod.getString("modid"), mod.getString("version"));
				}
				info.setModInfo(new ServerInfo.ModInfo(type, Collections.unmodifiableMap(modVersions), moddedClientAllowed));
			}

			if (callable != null)
				callable.done(info);
		}
		catch (IOException e)
		{
			if (callable != null)
				callable.failed(e);
		}
	}

	private String handshake(ServerInfo info) throws IOException
	{
		InetSocketAddress address = MessageUtils.getAddress(info.getHostName());

		if (channel == null)
			channel = SocketChannel.open(address);
		if (channel == null || !channel.isConnected())
			throw new IOException("Cannot open channel to " + info.getHostName());

		ByteBuffer buffer = ByteBuffer.allocate(256); //handshake
		buffer.put((byte) 0x00);
		writeVarInt(buffer, 210);
		writeString(buffer, address.getHostName());
		buffer.putShort((short) (address.getPort() & 0xffff));
		writeVarInt(buffer, 1);
		buffer.flip();

		ByteBuffer handshake = ByteBuffer.allocate(buffer.limit() + 8); //wrap handleshake with it size
		writeVarInt(handshake, buffer.limit());
		handshake.put(buffer);
		handshake.flip();

		ByteBuffer serverStatus = ByteBuffer.wrap(new byte[]{1, 0x00}); //server info query

		channel.write(handshake);
		channel.write(serverStatus);

		buffer.clear();
		channel.read(buffer);
		buffer.flip();

		readVarInt(buffer);// size
		int id = readVarInt(buffer);
		if (id == -1)
			throw new IOException("Premature end of stream.");
		if (id != 0x00)
			throw new IOException("Illegal packet id: " + id);

		int length = readVarInt(buffer);
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return new String(bytes);
	}
}
