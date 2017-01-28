package net.launcher.game.forge.config;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author ci010
 */
public interface ConfigIO
{
	ForgeConfig read(Path path) throws IOException;

	void write(ForgeConfig config) throws IOException;
}
