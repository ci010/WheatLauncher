package net.launcher.api;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author ci010
 */
public class ModuleLoadedEvent<T> extends Event
{
	public static EventType<ModuleLoadedEvent<?>> MODULE_LOADED = new EventType<>(ANY, "MODULE_LOAD");

	private T module;

	public ModuleLoadedEvent(T module)
	{
		super(MODULE_LOADED);
		this.module = module;
	}

	public T getModule() {return module;}
}
