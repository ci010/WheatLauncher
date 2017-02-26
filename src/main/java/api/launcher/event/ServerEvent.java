package api.launcher.event;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerStatus;

import java.util.Optional;

/**
 * @author ci010
 */
public class ServerEvent extends Event
{
	public static EventType<ServerEvent> TYPE = new EventType<>(EventType.ROOT, "SERVER_EVENT");
	public static EventType<ServerEvent> ADD = new EventType<>(TYPE, "SERVER_ADD");
	public static EventType<ServerEvent> REMOVE = new EventType<>(TYPE, "SERVER_REMOVE");
	public static EventType<ServerEvent> MODIFY = new EventType<>(TYPE, "SERVER_MODIFY");

	private ServerInfo info;
	private ServerStatus status;

	public ServerEvent(@NamedArg("eventType") EventType<? extends ServerEvent> eventType, ServerInfo info)
	{
		super(eventType);
		this.info = info;
	}

	public ServerEvent(@NamedArg("eventType") EventType<? extends ServerEvent> eventType, ServerInfo info,
					   ServerStatus status)
	{
		super(eventType);
		this.info = info;
		this.status = status;
	}

	public ServerInfo getInfo() {return info;}

	public Optional<ServerStatus> getStatus() {return Optional.ofNullable(status);}
}
