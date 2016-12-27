package net.launcher.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

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
}
