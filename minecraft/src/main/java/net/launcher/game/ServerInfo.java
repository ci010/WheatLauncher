package net.launcher.game;

import javafx.scene.image.Image;
import org.to2mbn.jmccc.auth.yggdrasil.core.util.Base64;

import java.io.ByteArrayInputStream;

/**
 * @author ci010
 */
public interface ServerInfo
{
	static Image createServerIcon(ServerInfo info)
	{
		String serverIcon = info.getServerIcon();
		if (serverIcon == null) return null;
		byte[] decode = Base64.decode(serverIcon.toCharArray());
		return new Image(new ByteArrayInputStream(decode));
	}

	void setName(String name);

	void setHostName(String hostName);

	void setServerIcon(String serverIcon);

	void setLanServer(boolean lanServer);

	void setResourceMode(ServerInfoBase.ResourceMode resourceMode);

	String getName();

	String getHostName();

	String getServerIcon();

	boolean isLanServer();

	ServerInfoBase.ResourceMode getResourceMode();
}
