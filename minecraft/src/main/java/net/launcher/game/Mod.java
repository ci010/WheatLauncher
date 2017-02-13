package net.launcher.game;

import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @author ci010
 */
public interface Mod<META>
{
	ArtifactVersion getVersion();

	String getModID();

	String getAuthor();

	String getDescription();

	VersionRange getMinecraftVersion();

	META getMetaData();
}
