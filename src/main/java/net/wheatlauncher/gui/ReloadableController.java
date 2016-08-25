package net.wheatlauncher.gui;

import net.wheatlauncher.launch.LaunchProfile;

/**
 * @author ci010
 */
public interface ReloadableController
{
	void reload();

	void onProfileChange(LaunchProfile profile);
}
