package net.launcher.game;

import net.launcher.game.text.Style;
import net.launcher.game.text.TextComponent;
import net.launcher.game.text.TextFormatting;
import net.launcher.game.text.components.TextComponentString;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class ServerStatus
{
	public static ServerStatus pinging()
	{
		return new ServerStatus(new TextComponentString("unknown"), new TextComponentString("Pinging..."), -1, -1, -1);
	}

	public static ServerStatus unknownHost()
	{
		return new ServerStatus(new TextComponentString("unknown"),
				new TextComponentString("Can\'t resolve hostname.").style(new Style().setColor(TextFormatting.DARK_RED))
				, -1, -1, -1);
	}

	public static ServerStatus error()
	{
		return new ServerStatus(new TextComponentString("unknown"),
				new TextComponentString("Can\'t connect to server.").style(new Style().setColor(TextFormatting
						.DARK_RED))
				, -1, -1, -1);
	}

	private long pingToServer;
	private TextComponent gameVersion;
	private int protocolVersion;
	private int onlinePlayers, capability;

	private TextComponent serverMOTD;

	private GameProfile[] playerList = new GameProfile[0];
	private ModInfo modInfo = new ModInfo("", Collections.emptyMap(), false);

	public ServerStatus(TextComponent gameVersion, TextComponent serverMOTD, int protocolVersion, int onlinePlayers, int
			capability)
	{
		this.gameVersion = gameVersion;
		this.serverMOTD = serverMOTD;
		this.protocolVersion = protocolVersion;
		this.onlinePlayers = onlinePlayers;
		this.capability = capability;
	}

	public ServerStatus(TextComponent gameVersion, TextComponent serverMOTD, int protocolVersion, int onlinePlayers, int
			capability,
						GameProfile[] playerList, ModInfo modInfo)
	{
		this.gameVersion = gameVersion;
		this.serverMOTD = serverMOTD;
		this.protocolVersion = protocolVersion;
		this.onlinePlayers = onlinePlayers;
		this.capability = capability;
		this.playerList = playerList;
		this.modInfo = modInfo;
	}

	public void setPingToServer(long pingToServer)
	{
		this.pingToServer = pingToServer;
	}

	public long getPingToServer()
	{
		return pingToServer;
	}

	public TextComponent getGameVersion()
	{
		return gameVersion;
	}

	public TextComponent getServerMOTD()
	{
		return serverMOTD;
	}

	public int getProtocolVersion()
	{
		return protocolVersion;
	}

	public int getOnlinePlayers()
	{
		return onlinePlayers;
	}

	public int getCapability()
	{
		return capability;
	}

	public GameProfile[] getPlayerList()
	{
		return playerList;
	}

	public ModInfo getModInfo()
	{
		return modInfo;
	}

	@Override
	public String toString()
	{
		return "ServerStatus{" +
				"pingToServer=" + pingToServer +
				", gameVersion='" + gameVersion + '\'' +
				", serverMOTD='" + serverMOTD + '\'' +
				", protocolVersion=" + protocolVersion +
				", onlinePlayers=" + onlinePlayers +
				", capability=" + capability +
				", playerList=" + Arrays.toString(playerList) +
				", modInfo=" + modInfo +
				'}';
	}

	public static class ModInfo
	{
		private String type;
		private Map<String, String> modIdVersions;
		private boolean isBlocked;

		public ModInfo(String type, Map<String, String> modIdVersions, boolean isBlocked)
		{
			this.type = type;
			System.out.println(modIdVersions);
			this.modIdVersions = modIdVersions;
			this.isBlocked = isBlocked;
		}

		public String getType()
		{
			return type;
		}

		public Map<String, String> getModIdVersions()
		{
			return modIdVersions;
		}

		public boolean isBlocked()
		{
			return isBlocked;
		}

		@Override
		public String toString()
		{
			return "ModInfo{" +
					"type='" + type + '\'' +
					", modIdVersions=" + modIdVersions +
					", isBlocked=" + isBlocked +
					'}';
		}
	}

	public static BiSerializer<ModInfo, JSONObject> modInfoSerializer()
	{
		return BiSerializer.combine((info, context) ->
		{
			JSONObject modinfo = new JSONObject();
			modinfo.put("type", info.getType());
			JSONArray array = new JSONArray();
			for (Map.Entry<String, String> entry : info.getModIdVersions().entrySet())
			{
				JSONObject obj = new JSONObject();
				obj.put("modid", entry.getKey());
				obj.put("version", entry.getValue());
				array.put(obj);
			}
			return modinfo;
		}, (serialized, context) ->
		{
			JSONObject modinfo = serialized.optJSONObject("modinfo");
			if (modinfo == null) return new ModInfo("", Collections.emptyMap(), false);
			String type = modinfo.optString("type");
			JSONArray array = modinfo.getJSONArray("modList");
			boolean moddedClientAllowed = !modinfo.has("clientModsAllowed") || modinfo.getBoolean("clientModsAllowed");
			Map<String, String> modVersions = new TreeMap<>();
			for (int i = 0; i < array.length(); i++)
			{
				JSONObject mod = array.getJSONObject(i);
				modVersions.put(mod.getString("modid"), mod.getString("version"));
			}
			return new ServerStatus.ModInfo(type, Collections.unmodifiableMap(modVersions), moddedClientAllowed);
		});
	}
}
