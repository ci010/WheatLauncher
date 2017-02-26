package net.launcher.game;

import net.launcher.game.text.Style;
import net.launcher.game.text.TextComponent;
import net.launcher.game.text.TextFormatting;
import net.launcher.game.text.components.TextComponentString;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author ci010
 */
public class ServerStatus
{
	public static ServerStatus pinging()
	{
		return new ServerStatus("unknown", new TextComponentString("Pinging..."), -1, -1, -1);
	}

	public static ServerStatus unknownHost()
	{
		return new ServerStatus("unknown",
				new TextComponentString("Can\'t resolve hostname.").style(new Style().setColor(TextFormatting.DARK_RED))
				, -1, -1, -1);
	}

	public static ServerStatus error()
	{
		return new ServerStatus("unknown",
				new TextComponentString("Can\'t connect to server.").style(new Style().setColor(TextFormatting
						.DARK_RED))
				, -1, -1, -1);
	}

	private long pingToServer;
	private String gameVersion;
	private int protocolVersion;
	private int onlinePlayers, capability;

	private TextComponent serverMOTD;

	private GameProfile[] playerList = new GameProfile[0];
	private ModInfo modInfo = new ModInfo("", Collections.emptyMap(), false);

	public ServerStatus(String gameVersion, TextComponent serverMOTD, int protocolVersion, int onlinePlayers, int capability)
	{
		this.gameVersion = gameVersion;
		this.serverMOTD = serverMOTD;
		this.protocolVersion = protocolVersion;
		this.onlinePlayers = onlinePlayers;
		this.capability = capability;
	}

	public ServerStatus(String gameVersion, TextComponent serverMOTD, int protocolVersion, int onlinePlayers, int capability,
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

	public String getGameVersion()
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
}
