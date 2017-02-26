package net.launcher;

import api.launcher.ARML;
import api.launcher.event.ServerEvent;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerInfoBase;
import net.launcher.game.ServerStatus;

/**
 * @author ci010
 */
public class FXServerInfo implements ServerInfo, InvalidationListener
{
	private StringProperty name = new SimpleStringProperty(), hostName = new SimpleStringProperty(), serverIcon = new SimpleStringProperty();
	private ObjectProperty<ServerInfoBase.ResourceMode> resourceMode = new SimpleObjectProperty<>();
	private BooleanProperty lanServer = new SimpleBooleanProperty();

	private ObjectProperty<ServerStatus> status = new SimpleObjectProperty<>();

	@Override
	public String toString()
	{
		return "FXServerInfo{" +
				"name=" + name.get() +
				", hostName=" + hostName.get() +
				", resourceMode=" + resourceMode.get() +
				", lanServer=" + lanServer.get() +
				", status=" + status.get() +
				'}';
	}

	public FXServerInfo(ServerInfo info)
	{
		this.name.set(info.getName());
		this.hostName.set(info.getHostName());
		this.serverIcon.set(info.getServerIcon());
		this.resourceMode.set(info.getResourceMode());
		this.lanServer.set(info.isLanServer());

//		name.addListener(this);
//		hostName.addListener(this);
//		serverIcon.addListener(this);
//		resourceMode.addListener(this);
//		lanServer.addListener(this);
//		status.addListener(this);
	}

	public ServerStatus getStatus()
	{
		return status.get();
	}

	public ObjectProperty<ServerStatus> statusProperty()
	{
		return status;
	}

	public void setStatus(ServerStatus status)
	{
		this.status.set(status);
	}

	@Override
	public String getName()
	{
		return name.get();
	}

	public StringProperty nameProperty()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name.set(name);
	}

	@Override
	public String getHostName()
	{
		return hostName.get();
	}

	public StringProperty hostNameProperty()
	{
		return hostName;
	}

	public void setHostName(String hostName)
	{
		this.hostName.set(hostName);
	}

	@Override
	public String getServerIcon()
	{
		return serverIcon.get();
	}

	public StringProperty serverIconProperty()
	{
		return serverIcon;
	}

	public void setServerIcon(String serverIcon)
	{
		this.serverIcon.set(serverIcon);
	}

	@Override
	public ServerInfoBase.ResourceMode getResourceMode()
	{
		return resourceMode.get();
	}

	public ObjectProperty<ServerInfoBase.ResourceMode> resourceModeProperty()
	{
		return resourceMode;
	}

	public void setResourceMode(ServerInfoBase.ResourceMode resourceMode)
	{
		this.resourceMode.set(resourceMode);
	}

	public boolean isLanServer()
	{
		return lanServer.get();
	}

	public BooleanProperty lanServerProperty()
	{
		return lanServer;
	}

	public void setLanServer(boolean lanServer)
	{
		this.lanServer.set(lanServer);
	}

	@Override
	public void invalidated(Observable observable)
	{
		ARML.bus().postEvent(new ServerEvent(ServerEvent.MODIFY, this, status.get()));
	}
}
