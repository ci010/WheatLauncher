package net.launcher.api;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

/**
 * The singleton
 *
 * @author ci010
 */
public class ARML
{
	private static final ARML INST = new ARML();

	private final LauncherContext context = null;
	private final EventBus bus = null;
	private final Logger logger = null;
	private final ScheduledExecutorService scheduledExecutorService = null;

	private Map<String, Object> components = new TreeMap<>();

	public static ARML instance() {return INST;}

	public static LauncherContext core() {return INST.getContext();}

	public static Logger logger() {return INST.getLogger();}

	public static EventBus bus() {return INST.getBus();}

	public static ScheduledExecutorService async() {return INST.getService();}

	public LauncherContext getContext() {return context;}

	public EventBus getBus() {return bus;}

	public Logger getLogger() {return logger;}

	public <T> Optional<T> getComponent(Class<T> tClass) {return Optional.ofNullable((T) components.get(tClass.getName()));}

	public <T> Optional<T> getComponent(Class<T> tClass, String id) {return Optional.ofNullable((T) components.get(id));}

	public <T> boolean registerComponent(Class<? super T> clz, T o)
	{
		if (components.containsKey(clz.getName())) return false;
		components.put(clz.getName(), o);
		return true;
	}

	public <T> boolean registerComponent(String id, T o)
	{
		if (components.containsKey(id)) return false;
		components.put(id, o);
		return true;
	}

	public ScheduledExecutorService getService() {return scheduledExecutorService;}
}
