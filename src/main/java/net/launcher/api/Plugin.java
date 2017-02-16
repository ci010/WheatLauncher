package net.launcher.api;

import net.launcher.Logger;

/**
 * @author ci010
 */
public interface Plugin
{
	void preload(EventBus bus, Logger logger) throws Exception;

	void load(EventBus bus, LauncherContext context, Logger logger) throws Exception;

	void postLoad(EventBus bus, LauncherContext context, Logger logger) throws Exception;
}
