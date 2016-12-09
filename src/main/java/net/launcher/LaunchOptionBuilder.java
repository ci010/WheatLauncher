package net.launcher;

import net.launcher.profile.LaunchProfile;
import org.to2mbn.jmccc.option.LaunchOption;

/**
 * @author ci010
 */
public interface LaunchOptionBuilder
{
	LaunchOption build(LaunchProfile launchProfile, AuthProfile authProfile);
}
