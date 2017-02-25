package api.launcher.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author ci010
 */
public class MinecraftExitEvent extends Event
{
	public static final EventType<MinecraftExitEvent> TYPE = new EventType<>(EventType.ROOT, "MINECRAFT_EXIT");

	private int code;

	public MinecraftExitEvent(int code)
	{
		super(TYPE);
		this.code = code;
	}

	public int getExitCode() {return code;}
}
