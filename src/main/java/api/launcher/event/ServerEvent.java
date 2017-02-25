package api.launcher.event;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;
import net.launcher.game.ServerInfo;

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

	public ServerEvent(@NamedArg("eventType") EventType<? extends ServerEvent> eventType, ServerInfo info)
	{
		super(eventType);
		this.info = info;
	}

	public ServerInfo getInfo() {return info;}
}
