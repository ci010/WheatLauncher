package api.launcher.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author ci010
 */
public class ErrorEvent extends Event
{
	public static final EventType<ErrorEvent> TYPE = new EventType<>(EventType.ROOT, "ERROR");

	private Throwable throwable;

	public ErrorEvent(Throwable throwable)
	{
		super(TYPE);
		this.throwable = throwable;
	}

	public Throwable getThrowable() {return throwable;}
}
