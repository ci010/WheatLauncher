package net.launcher.utils.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ci010
 */
class FetchUtils
{
	static void fetch(Path from, Path to, Repository.FetchOption option) throws IOException
	{
		if (option == null)
			option = Repository.FetchOption.SYMBOL_LINK;
		if (Files.exists(to)) return;
		switch (option)
		{
			case COPY:
				Files.copy(from, to);
				break;
			default:
			case SYMBOL_LINK:
				Files.createSymbolicLink(to, from);
				break;
			case HARD_LINK:
				Files.createLink(to, from);
				break;
		}
	}
}
