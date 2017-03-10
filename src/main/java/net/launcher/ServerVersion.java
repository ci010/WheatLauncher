package net.launcher;

import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @author ci010
 */
public interface ServerVersion
{
	VersionRange getSupportVersion();
}
