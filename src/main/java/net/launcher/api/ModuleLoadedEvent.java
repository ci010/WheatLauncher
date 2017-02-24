package net.launcher.api;

import javafx.event.EventType;

/**
 * @author ci010
 */
public class ModuleLoadedEvent<T> extends LauncherInitEvent
{
	public static EventType<ModuleLoadedEvent<?>> MODULE_LOADED = new EventType<>(LAUNCHER_INIT, "MODULE_LOAD");

//	public static EventType<ModuleLoadedEvent<ModManager>> MOD_MANAGER = new EventType<>(MODULE_LOADED, "MOD_MANAGER");
//	public static EventType<ModuleLoadedEvent<ResourcePackManager>> RESOURCE_PACK_MANAGER = new EventType<>
//			(MODULE_LOADED, "RESOURCE_PACK_MANAGER");
//	public static EventType<ModuleLoadedEvent<LaunchProfileManager>> LAUNCH_PROFILE_MANAGER = new EventType<>
//			(MODULE_LOADED, "LAUNCH_PROFILE_MANAGER");
//	public static EventType<ModuleLoadedEvent<AuthManager>> AUTH_PROFILE = new EventType<>
//			(MODULE_LOADED, "AUTH_PROFILE");
//	public static EventType<ModuleLoadedEvent<MinecraftAssetsManager>> MINECRAFT_ASSETS_MANAGER = new EventType<>
//			(MODULE_LOADED, "MINECRAFT_ASSETS_MANAGER");

	private T module;

	public ModuleLoadedEvent(T module)
	{
		super(MODULE_LOADED);
		this.module = module;
	}

	public T getModule() {return module;}
}
