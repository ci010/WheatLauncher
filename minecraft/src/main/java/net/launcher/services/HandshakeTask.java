package net.launcher.services;

import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;
import net.launcher.utils.MessageUtils;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import static net.launcher.utils.MessageUtils.*;

/**
 * @author ci010
 */
class HandshakeTask implements Callable<ServerStatus>
{
	private ServerInfo info;
	private SocketChannel channel;

	HandshakeTask(ServerInfo info, SocketChannel channel)
	{
		this.info = info;
		this.channel = channel;
	}

	@Override
	public ServerStatus call() throws Exception
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

		if (favicon.startsWith("data:image/png;base64,"))
			info.setServerIcon(favicon.substring("data:image/png;base64,".length()));

		ServerStatus.ModInfo modInfo = ServerStatus.pinging().getModInfo();

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
			modInfo = new ServerStatus.ModInfo(type, Collections.unmodifiableMap(modVersions), moddedClientAllowed);
		}

		return new ServerStatus(versionName, description, protocol, online, max, profiles, modInfo);
	}

	private String handshake(ServerInfo info) throws IOException
	{
		InetSocketAddress address = MessageUtils.getAddress(info.getHostName());

		if (channel == null)
			channel = SocketChannel.open(address);
		if (channel == null || !channel.isConnected())
			throw new IOException("Cannot channel channel to " + info.getHostName());

		ByteBuffer buffer = ByteBuffer.allocate(256); //handshake
		buffer.put((byte) 0x00);//handshake packet 0x00
		writeVarInt(buffer, 210);//write protocol version
		writeString(buffer, address.getHostName());//write host name
		buffer.putShort((short) (address.getPort() & 0xffff));//write port
		writeVarInt(buffer, 1);//write next state(1 ping 2 login)
		buffer.flip();

		ByteBuffer handshake = ByteBuffer.allocate(buffer.limit() + 8); //wrap handshake with it size
		writeVarInt(handshake, buffer.limit());//write packet size
		handshake.put(buffer);
		handshake.flip();

		ByteBuffer serverStatus = ByteBuffer.wrap(new byte[]{1, 0x00}); //server info query {length, id}

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
