package net.launcher.io;

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

/**
 * @author ci010
 */
public class NIOUtils
{
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
		try (SeekableByteChannel channel = Files.newByteChannel(path))
		{
			ByteBuffer buffer = ByteBuffer.allocate((int) Files.size(path));
			channel.read(buffer);
			buffer.flip();
			return buffer;
		}
	}

	public static byte[] readToBytes(Path path) throws IOException
	{
		ByteBuffer buffer = readToBuffer(path);
		byte[] bytes = new byte[buffer.capacity()];
		buffer.get(bytes);
		return bytes;
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
