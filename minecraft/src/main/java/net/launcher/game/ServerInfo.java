package net.launcher.game;

import javafx.scene.image.Image;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.auth.yggdrasil.core.util.Base64;

import java.io.ByteArrayInputStream;

/**
 * @author ci010
 */
public class ServerInfo
{
	private String name, hostName, serverIcon;

	private boolean lanServer;
	private ResourceMode resourceMode;

	private ServerStatus status;

	public ServerInfo(String name, String hostName, String serverIcon, ResourceMode resourceMode)
	{
		this.name = name;
		this.hostName = hostName;
		this.serverIcon = serverIcon;
		this.resourceMode = resourceMode;
	}

	public void setStatus(ServerStatus status)
	{
		this.status = status;
	}

	public ServerStatus getStatus()
	{
		return status;
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

	public void setResourceMode(ResourceMode resourceMode)
	{
		this.resourceMode = resourceMode;
	}

	public String getName()
	{
		return name;
	}

	public String getHostName()
	{
		return hostName;
	}

	public String getServerIcon()
	{
		return serverIcon;
	}

	public boolean isLanServer()
	{
		return lanServer;
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
				", serverIcon='" + serverIcon + '\'' +
				", lanServer=" + lanServer +
				", resourceMode=" + resourceMode +
				", status=" + status +
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

}
