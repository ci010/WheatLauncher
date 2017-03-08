package net.launcher.game;

import net.launcher.game.text.Style;
import net.launcher.game.text.TextComponent;
import net.launcher.game.text.TextFormatting;
import net.launcher.game.text.components.TextComponentString;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;

import java.util.Arrays;
import java.util.Collections;

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
	private ModManifest modInfo = new ModManifest(Collections.emptyMap());

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
						GameProfile[] playerList, ModManifest modInfo)
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

	public ModManifest getModInfo()
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
}
