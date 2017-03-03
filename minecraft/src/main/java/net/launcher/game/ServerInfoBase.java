package net.launcher.game;

import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.serial.BiSerializer;

/**
 * @author ci010
 */
public class ServerInfoBase implements ServerInfo
{
	private String name, hostName, serverIcon;

	private boolean lanServer;
	private ResourceMode resourceMode;

	public ServerInfoBase(String name, String hostName)
	{
		this(name, hostName, null, ResourceMode.PROMPT);
	}

	public ServerInfoBase(String name, String hostName, String serverIcon, ResourceMode resourceMode)
	{
		this.name = name;
		this.hostName = hostName;
		this.serverIcon = serverIcon;
		this.resourceMode = resourceMode;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	@Override
	public void setServerIcon(String serverIcon)
	{
		this.serverIcon = serverIcon;
	}

	@Override
	public void setLanServer(boolean lanServer)
	{
		this.lanServer = lanServer;
	}

	@Override
	public void setResourceMode(ResourceMode resourceMode)
	{
		this.resourceMode = resourceMode;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getHostName()
	{
		return hostName;
	}

	@Override
	public String getServerIcon()
	{
		return serverIcon;
	}

	@Override
	public boolean isLanServer()
	{
		return lanServer;
	}

	@Override
	public ResourceMode getResourceMode()
	{
		return resourceMode;
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
				", serverIcon='" + serverIcon + '\'' +
				", lanServer=" + lanServer +
				", resourceMode=" + resourceMode +
				'}';
	}

	public static BiSerializer<ServerInfo, NBTCompound> serializer()
	{
		return BiSerializer.combine((info, context) ->
		{
			NBTCompound compound = NBT.compound().put("name", info.getName()).put("ip", info.getHostName())
					.option("icon", info.getServerIcon());
			if (info.getResourceMode() == ResourceMode.ENABLED)
				compound.put("acceptTextures", true);
			else if (info.getResourceMode() == ResourceMode.DISABLED)
				compound.put("acceptTextures", false);
			return compound;
		}, (serialized, context) ->
				new ServerInfoBase(serialized.get("name").asString(), serialized.get("ip").asString(),
						serialized.get("icon").asString(""),
						serialized.option("acceptTextures").map(nbt -> nbt.asBool() ? ResourceMode.ENABLED : ResourceMode.DISABLED).orElse(ResourceMode.PROMPT)));
	}
}
