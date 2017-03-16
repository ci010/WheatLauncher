package net.launcher.fx.module;

/**
 * @author ci010
 */
public abstract class GlobalModule extends Module
{
	public GlobalModule() {}

	public ComponentProvider createComponentProvider() {return null;}

	public InstanceProvider createInstanceProvider() {return null;}
}
