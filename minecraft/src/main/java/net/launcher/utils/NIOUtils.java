package net.launcher.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author ci010
 */
public class NIOUtils
{
	public static void writeString(Path path, String s) throws IOException
	{
		try (FileChannel open = FileChannel.open(path,
				StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE))
		{
			open.write(Charset.forName("UTF-8").encode(s));
		}
	}

	public static String readToString(Path path) throws IOException
	{
		return toUTF8(readToBuffer(path));
	}

	public static InputStream readToStream(Path path) throws IOException
	{
		return new ByteArrayInputStream(readToBytes(path));
	}

	public static ByteBuffer readToBuffer(Path path) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate((int) Files.size(path));
		readToBuffer(path, buffer);
		buffer.flip();
		return buffer;
	}

	public static void readToBuffer(Path path, ByteBuffer buffer) throws IOException
	{
		try (SeekableByteChannel channel = Files.newByteChannel(path)) {channel.read(buffer);}
	}

	public static byte[] readToBytes(Path path) throws IOException
	{
		ByteBuffer buffer = readToBuffer(path);
		byte[] bytes = new byte[buffer.capacity()];
		buffer.get(bytes);
		return bytes;
	}

	public static void mapToConsumer(Path path, Consumer<MappedByteBuffer> consumer) throws IOException
	{
		try (FileChannel open = FileChannel.open(path, StandardOpenOption.READ))
		{
			consumer.accept(open.map(FileChannel.MapMode.READ_ONLY, 0, Files.size(path)));
		}
	}

	public static MappedByteBuffer mapToBuffer(Path path) throws IOException
	{
		try (FileChannel open = FileChannel.open(path, StandardOpenOption.READ))
		{
			return open.map(FileChannel.MapMode.READ_ONLY, 0, Files.size(path));
		}
	}

	public static byte[] mapToBytes(Path path) throws IOException
	{
		MappedByteBuffer mappedByteBuffer = mapToBuffer(path);
		byte[] bytes = new byte[mappedByteBuffer.capacity()];
		mappedByteBuffer.get(bytes);
		return bytes;
	}

	public static InputStream mapToStream(Path path) throws IOException
	{
		return new ByteArrayInputStream(mapToBytes(path));
	}

	public static String mapToString(Path path) throws IOException
	{
		return toUTF8(mapToBuffer(path));
	}

	public static String toUTF8(ByteBuffer bytes)
	{
		return Charset.forName("UTF-8").decode(bytes).toString();
	}

	public static void copyDirectory(Path from, Path to, Predicate<Path> filter, CopyOption... option) throws IOException
	{
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		if (!Files.isDirectory(from)) throw new IOException(from + " is not a directory!");
		Files.walkFileTree(from, new CopyFromVisitor(from, to, filter, option));
	}

	public static void copyDirectory(Path from, Path to, CopyOption... option) throws IOException {copyDirectory(from, to, null, option);}

	public static void clearDirectory(Path directory) throws IOException
	{
		Objects.requireNonNull(directory);
		if (!Files.isDirectory(directory)) throw new IOException(directory + " is not a directory!");
		Files.walkFileTree(directory, new ClearVisitor());
	}

	public static void deleteDirectory(Path directory) throws IOException
	{
		clearDirectory(directory);
		Files.delete(directory);
	}

	public static class ClearVisitor extends SimpleFileVisitor<Path>
	{
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
		{
			Files.delete(file);
			return super.visitFile(file, attrs);
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
		{
			Files.delete(dir);
			return super.postVisitDirectory(dir, exc);
		}
	}

	public static class CopyFromVisitor extends SimpleFileVisitor<Path>
	{
		private final Path from;
		private final Path to;
		private final CopyOption[] copyOption;
		private Predicate<Path> filter;

		public CopyFromVisitor(Path fromPath, Path toPath, Predicate<Path> filter, CopyOption... copyOption)
		{
			this.from = fromPath;
			this.to = toPath;
			this.copyOption = copyOption;
			this.filter = filter;
		}

		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
		{
			super.preVisitDirectory(dir, attrs);
			Path targetPath = to.resolve(from.relativize(dir));
			if (!Files.exists(targetPath))
				Files.createDirectory(targetPath);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
		{
			super.visitFile(file, attrs);
			Path relativize = from.relativize(file);
			if (filter == null || filter.test(relativize))
				Files.copy(file, to.resolve(relativize), copyOption);
			return FileVisitResult.CONTINUE;
		}
	}
}
