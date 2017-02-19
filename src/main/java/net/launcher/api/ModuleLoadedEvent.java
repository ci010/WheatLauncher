package net.launcher.api;

import javafx.event.EventType;
import net.launcher.AuthProfile;
import net.launcher.assets.MinecraftAssetsManager;
import net.launcher.profile.LaunchProfileManager;

/**
 * @author ci010
 */
public class ModuleLoadedEvent<T> extends LauncherInitEvent
{
	public static EventType<ModuleLoadedEvent<?>> MODULE_LOADED = new EventType<>(LAUNCHER_INIT, "MODULE_LOAD");

	public static EventType<ModuleLoadedEvent<LaunchProfileManager>> LAUNCH_PROFILE_MANAGER = new EventType<>
			(MODULE_LOADED, "LAUNCH_PROFILE_MANAGER");
	public static EventType<ModuleLoadedEvent<AuthProfile>> AUTH_PROFILE = new EventType<>
			(MODULE_LOADED, "AUTH_PROFILE");
	public static EventType<ModuleLoadedEvent<MinecraftAssetsManager>> MINECRAFT_ASSETS_MANAGER = new EventType<>
			(MODULE_LOADED, "MINECRAFT_ASSETS_MANAGER");

	private T module;

	public ModuleLoadedEvent(EventType<? extends ModuleLoadedEvent<?>> eventType, T module)
	{
		super(eventType);
		this.module = module;
	}

	public T getModule() {return module;}
}
