package api.launcher.event;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author ci010
 */
public class LaunchServerEvent extends Event
{
	public LaunchServerEvent(@NamedArg("eventType") EventType<? extends Event> eventType)
	{
		super(eventType);
	}
}
