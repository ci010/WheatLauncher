package net.launcher.game;

import javafx.scene.image.Image;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.auth.yggdrasil.core.GameProfile;
import org.to2mbn.jmccc.auth.yggdrasil.core.util.Base64;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * @author ci010
 */
public class ServerInfo
{
	private String name, hostName, gameVersion, serverMOTD, serverIcon;
	private long pingToServer;
	private int version, onlinePlayers, capability;
	private boolean lanServer, pinged;
	private ResourceMode resourceMode;

	private GameProfile[] playerList;

	private ModInfo modInfo;

	public ServerInfo(String name, String hostName, ResourceMode resourceMode)
	{
		this.name = name;
		this.hostName = hostName;
		this.resourceMode = resourceMode;
	}

	public ServerInfo(String name, String hostName, String serverIcon, ResourceMode resourceMode)
	{
		this.name = name;
		this.hostName = hostName;
		this.serverIcon = serverIcon;
		this.resourceMode = resourceMode;
	}

	public void setServerMOTD(String serverMOTD)
	{
		this.serverMOTD = serverMOTD;
	}

	public void setPingToServer(long pingToServer)
	{
		this.pingToServer = pingToServer;
	}

	public void setOnlinePlayersInfo(int onlinePlayers, int capability)
	{
		this.onlinePlayers = onlinePlayers;
		this.capability = capability;
	}

	public void setGameVersion(String gameVersion)
	{
		this.gameVersion = gameVersion;
	}

	public void setProtocolVersion(int version)
	{
		this.version = version;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	public void setServerIcon(String serverIcon)
	{
		this.serverIcon = serverIcon;
	}

	public void setLanServer(boolean lanServer)
	{
		this.lanServer = lanServer;
	}

	public int getOnlinePlayers()
	{
		return onlinePlayers;
	}

	public int getCapability()
	{
		return capability;
	}

	public void setResourceMode(ResourceMode resourceMode)
	{
		this.resourceMode = resourceMode;
	}

	public GameProfile[] getPlayerList()
	{
		return playerList;
	}

	public ModInfo getModInfo()
	{
		return modInfo;
	}

	public void setModInfo(ModInfo modInfo)
	{
		this.modInfo = modInfo;
	}

	public void setPlayerList(GameProfile[] playerList)
	{
		this.playerList = playerList;
	}

	public String getName()
	{
		return name;
	}

	public String getHostName()
	{
		return hostName;
	}

	public String getGameVersion()
	{
		return gameVersion;
	}

	public String getServerMOTD()
	{
		return serverMOTD;
	}

	public String getServerIcon()
	{
		return serverIcon;
	}

	public long getPingToServer()
	{
		return pingToServer;
	}

	public int getProtocolVersion()
	{
		return version;
	}

	public boolean isLanServer()
	{
		return lanServer;
	}

	public boolean isPinged()
	{
		return pinged;
	}

	public ResourceMode getResourceMode()
	{
		return resourceMode;
	}

	public static Image createServerIcon(ServerInfo info)
	{
		String serverIcon = info.getServerIcon();
		byte[] decode = Base64.decode(serverIcon.toCharArray());
		return new Image(new ByteArrayInputStream(decode));
	}

	public enum ResourceMode
	{
		ENABLED,
		DISABLED,
		PROMPT
	}

	@Override
	public String toString()
	{
		return "ServerInfo{" +
				"name='" + name + '\'' +
				", hostName='" + hostName + '\'' +
				", gameVersion='" + gameVersion + '\'' +
				", serverMOTD='" + serverMOTD + '\'' +
				", serverIcon='" + serverIcon + '\'' +
				", pingToServer=" + pingToServer +
				", version=" + version +
				", onlinePlayers=" + onlinePlayers +
				", capability=" + capability +
				", lanServer=" + lanServer +
				", pinged=" + pinged +
				", resourceMode=" + resourceMode +
				", playerList=" + Arrays.toString(playerList) +
				", modInfo=" + modInfo +
				'}';
	}

	public static final BiSerializer<ServerInfo, NBTCompound> SERIALIZER = BiSerializer.combine((info, context) ->
	{
		NBTCompound compound = NBT.compound().put("name", info.getName()).put("hostName", info.getHostName())
				.option("icon", info.getServerIcon());
		if (info.getResourceMode() == ResourceMode.ENABLED)
			compound.put("acceptTextures", true);
		else if (info.getResourceMode() == ResourceMode.DISABLED)
			compound.put("acceptTextures", false);
		return compound;
	}, (serialized, context) ->
			new ServerInfo(serialized.get("name").asString(), serialized.get("hostName").asString(),
					serialized.get("icon").asString(""),
					serialized.option("acceptTextures").map(nbt -> nbt.asBool() ? ResourceMode.ENABLED : ResourceMode.DISABLED).orElse(ResourceMode.PROMPT)));

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
