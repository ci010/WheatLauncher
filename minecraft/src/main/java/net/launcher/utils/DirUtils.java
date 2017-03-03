package net.launcher.utils;

import org.to2mbn.jmccc.util.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author ci010
 */
public class DirUtils
{
	public static File getAvailableWorkDir()

	{
		File root;
		switch (Platform.CURRENT)
		{
			case WINDOWS:
				String appdata = System.getenv("APPDATA");
				root = new File(appdata == null ? System.getProperty("user.home", ".") : appdata);
				break;
			case LINUX:
				root = new File(System.getProperty("user.home", "."));
				break;
			case OSX:
				root = new File("Library/Application Support/");
				break;
			default:
				root = new File(System.getProperty("user.home", ".") + "/");
		}
		return root;
	}

	public static void deleteContent(Path dir) throws IOException
	{
		if (!Files.isDirectory(dir))
			throw new IllegalArgumentException("need to be a directory! " + dir.toAbsolutePath());
		Files.walkFileTree(dir, new DeleteVisitor());
	}

	public static class DeleteVisitor extends SimpleFileVisitor<Path>
	{
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
		{
			Files.delete(file);
			return super.visitFile(file, attrs);
		}
	}
}
