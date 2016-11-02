package net.launcher.game;

import net.launcher.LaunchElement;
import net.launcher.LaunchElementManager;
import net.launcher.LaunchProfileManager;

import java.util.Optional;

/**
 * @author ci010
 */
public abstract class LaunchCore
{
	public abstract <T extends LaunchElement> Optional<LaunchElementManager<T>> getElementManager(Class<T> clz);

	public abstract LaunchProfileManager getProfileMananger();
}
