package net.launcher;

/**
 * @author ci010
 */
public interface Plugin
{
	void load(LaunchCore core, Logger logger) throws Exception;
}
