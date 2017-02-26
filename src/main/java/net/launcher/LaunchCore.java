package net.launcher;

import java.nio.file.Path;

/**
 * @author ci010
 */
public interface LaunchCore
{
	void launch() throws Exception;

	void init(Path root) throws Exception;

	void destroy() throws Exception;
}
