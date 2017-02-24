package net.launcher.api;

import javafx.event.Event;
import javafx.event.EventType;
import net.wheatlauncher.internal.io.IOGuard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ci010
 */
public class LauncherInitEvent extends Event
{
	public static EventType<LauncherInitEvent> LAUNCHER_INIT = new EventType<>(EventType.ROOT, "LAUNCHER_INIT");

	private Map<Class, IOGuard> registeredIO = new HashMap<>();

	public LauncherInitEvent(EventType<? extends LauncherInitEvent> eventType) {super(eventType);}

	public Map<Class, IOGuard> getRegisteredIO() {return Collections.unmodifiableMap(registeredIO);}

	public <T> boolean registerIO(Class<T> clz, IOGuard<T> guard)
	{
		Objects.requireNonNull(clz);
		Objects.requireNonNull(guard);
		if (registeredIO.containsKey(clz)) return false;
		registeredIO.put(clz, guard);
		return true;
	}
}
