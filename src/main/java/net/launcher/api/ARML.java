package net.launcher.api;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

/**
 * The singleton
 *
 * @author ci010
 */
public interface ARML
{
	ARML INSTANCE = null;

	static LauncherContext core() {return INSTANCE.getContext();}

	static Logger logger() {return INSTANCE.getLogger();}

	static EventBus bus() {return INSTANCE.getBus();}

	LauncherContext getContext();

	EventBus getBus();

	Logger getLogger();

	<T> Optional<T> getComponent(Class<T> tClass);

	<T> Optional<T> getComponent(Class<T> tClass, String id);

	<T> void registerComponent(Class<? super T> clz, T o);

	<T> void registerComponent(Class<? super T> clz, T o, String id);

	ScheduledExecutorService getService();

	ScheduledExecutorService getService(String id);
}
