package api.launcher.module;

import java.nio.file.Path;

/**
 * @author ci010
 */
public abstract class ProfileBaseModule extends Module
{
	public ProfileBaseModule() {}

	public ComponentProvider createComponentProvider(Path profileLocation) {return null;}

	public InstanceProvider createInstanceProvider(Path profileLocation) {return null;}
}
