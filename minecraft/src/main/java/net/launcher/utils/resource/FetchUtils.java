package net.launcher.utils.resource;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ci010
 */
public class FetchUtils
{
	private static boolean supportSymbolicLink(Path path)
	{
		FileSystem fileSystem = path.getFileSystem();
//		fileSystem.supportedFileAttributeViews().
//		fileSystem.provider().createSymbolicLink();
		return false;
	}

	public static void fetch(Path from, Path to, FetchOption option) throws IOException
	{
		if (option == null)
			option = FetchOption.COPY;
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
