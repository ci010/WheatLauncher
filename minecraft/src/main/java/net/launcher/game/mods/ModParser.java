package net.launcher.game.mods;

import java.nio.file.Path;

/**
 * @author ci010
 */
public interface ModParser
{
	ModContainer<?>[] parse(Path path) throws Exception;
}
