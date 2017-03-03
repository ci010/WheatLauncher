package net.launcher;

import net.launcher.game.ServerInfo;

import java.nio.file.Path;

/**
 * @author ci010
 */
public interface LaunchCore
{
	void launchServer(ServerInfo info);

	void launch() throws Exception;

	void init(Path root) throws Exception;

	void destroy() throws Exception;
}
