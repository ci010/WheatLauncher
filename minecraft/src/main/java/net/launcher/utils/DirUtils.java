package net.launcher.utils;

import org.to2mbn.jmccc.util.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.function.Predicate;

import static java.nio.file.StandardCopyOption.*;

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

	public static void copy(File dir, File target) throws IOException
	{
		copy(dir, target, null);
	}

	public static void copy(File src, File target, Predicate<Path> filter) throws IOException
	{
		if (!src.isDirectory())
			throw new IllegalArgumentException();
		Path path = src.toPath();
		Path targetPath = target.toPath();
		Files.walkFileTree(path, new CopyVisitor(path, targetPath, null, filter));
	}

	public static void move(File dir, File target) throws IOException
	{
		if (!dir.isDirectory())
			throw new IllegalArgumentException();
		Path path = dir.toPath();
		Files.walkFileTree(path, new MoveVisitor(path, target.toPath()));
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

	public static class MoveVisitor extends SimpleFileVisitor<Path>
	{
		private final Path moveFrom;
		private final Path moveTo;
		static FileTime time = null;

		public MoveVisitor(Path moveFrom, Path moveTo)
		{
			this.moveFrom = moveFrom;
			this.moveTo = moveTo;
		}

		static void moveSubTree(Path moveFrom, Path moveTo) throws IOException
		{
			try
			{
				Files.move(moveFrom, moveTo, REPLACE_EXISTING, ATOMIC_MOVE);
			}
			catch (IOException e)
			{
				System.err.println("Unable to move " + moveFrom + " [" + e + "]");
			}
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc)
				throws IOException
		{
			Path newdir = moveTo.resolve(moveFrom.relativize((Path) dir));
			try
			{
				Files.setLastModifiedTime(newdir, time);
				Files.delete(dir);
			}
			catch (IOException e)
			{
				System.err.println("Unable to copy all attributes to: " + newdir + " [" + e + "]");
			}

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
				throws IOException
		{
//			System.out.println("Move directory: " + (Path) dir);
			Path newdir = moveTo.resolve(moveFrom.relativize((Path) dir));
			try
			{
				Files.copy(dir, newdir, REPLACE_EXISTING, COPY_ATTRIBUTES);
				time = Files.getLastModifiedTime(dir);
			}
			catch (IOException e)
			{
				System.err.println("Unable to move " + newdir + " [" + e + "]");
				return FileVisitResult.SKIP_SUBTREE;
			}

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException
		{
//			System.out.println("Move file: " + (Path) file);
			moveSubTree(file, moveTo.resolve(moveFrom.relativize((Path) file)));
			return FileVisitResult.CONTINUE;
		}
	}

	public static class CopyVisitor extends SimpleFileVisitor<Path>
	{
		private final Path fromPath;
		private final Path toPath;
		private final CopyOption copyOption;
		private Predicate<Path> filter;

		public CopyVisitor(Path fromPath, Path toPath, CopyOption copyOption, Predicate<Path> filter)
		{
			this.fromPath = fromPath;
			this.toPath = toPath;
			this.copyOption = copyOption;
			this.filter = filter;
		}

		public CopyVisitor(Path fromPath, Path toPath, CopyOption copyOption)
		{
			this.fromPath = fromPath;
			this.toPath = toPath;
			this.copyOption = copyOption;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
		{
			super.preVisitDirectory(dir, attrs);
			Path targetPath = toPath.resolve(fromPath.relativize(dir));
			if (!Files.exists(targetPath))
				Files.createDirectory(targetPath);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
		{
			super.visitFile(file, attrs);
			Path relativize = fromPath.relativize(file);
			if (filter == null || filter.test(relativize))
				Files.copy(file, toPath.resolve(relativize), copyOption);
			return FileVisitResult.CONTINUE;
		}
	}
}
