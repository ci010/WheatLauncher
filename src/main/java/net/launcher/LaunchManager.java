package net.launcher;

import net.launcher.profile.LaunchProfile;
import org.to2mbn.jmccc.option.LaunchOption;

/**
 * @author ci010
 */
public interface LaunchManager
{
	void onLaunch(LaunchOption option, LaunchProfile profile) throws Exception;

	void onClose(LaunchOption option, LaunchProfile profile);
}
