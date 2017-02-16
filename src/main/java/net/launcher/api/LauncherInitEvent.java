package net.launcher.api;

import javafx.event.Event;
import javafx.event.EventType;
import net.launcher.auth.Authorize;
import net.wheatlauncher.internal.io.IOGuard;

import java.lang.reflect.InvocationTargetException;
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
	private Map<String, Authorize> authorizeMap = new HashMap<>();

	public LauncherInitEvent(EventType<? extends LauncherInitEvent> eventType) {super(eventType);}

	public boolean registerAuth(Class<? extends Authorize> authClass)
	{
		Authorize.ID annotation = authClass.getAnnotation(Authorize.ID.class);
		if (annotation == null)
			return false;
		if (authorizeMap.containsKey(annotation.value()))
			return false;
		try
		{
			authorizeMap.put(annotation.value(), authClass.getDeclaredConstructor().newInstance());
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			return false;
		}
		return true;
	}

	public <T> boolean registerIO(Class<T> clz, IOGuard<T> guard)
	{
		Objects.requireNonNull(clz);
		Objects.requireNonNull(guard);
		if (registeredIO.containsKey(clz)) return false;
		registeredIO.put(clz, guard);
		return true;
	}
}
