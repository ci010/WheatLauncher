package net.launcher.utils;

import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author ci010
 */
public class MD5
{
	public static String toString(byte[] md5)
	{
		char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		char[] resultCharArray = new char[md5.length * 2];
		int index = 0;

		for (byte b : md5)
		{
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		return new String(resultCharArray);
	}

	public static byte[] getMD5(File file) throws IOException
	{
		return getMD5(new FileInputStream(file));
	}

	public static byte[] getMD5(InputStream stream) throws IOException
	{
		return getMD5(IOUtils.toByteArray(stream));
	}

	public static byte[] check(Path file) throws IOException
	{
		if (Files.isRegularFile(file))
			try (FileChannel channel = FileChannel.open(file))
			{
				MessageDigest md5 = getMD5();
				md5.update(channel.map(FileChannel.MapMode.READ_ONLY, 0, Files.size(file)));
				return md5.digest();
			}
		if (Files.isDirectory(file))
		{
			MessageDigest md5 = getMD5();
			Files.walkFileTree(file, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					FileVisitResult result = super.visitFile(file, attrs);
					if (result == FileVisitResult.CONTINUE)
						try (FileChannel channel = FileChannel.open(file))
						{
							md5.update(channel.map(FileChannel.MapMode.READ_ONLY, 0, Files.size(file)));
						}
					return result;
				}
			});
			return md5.digest();
		}

		throw new IOException();
	}


	public static byte[] getMD5Fast(File file) throws IOException
	{
		if (file.isFile())
			try (FileInputStream in = new FileInputStream(file))
			{
				try (FileChannel ch = in.getChannel())
				{
					MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
					byte[] bytes = new byte[byteBuffer.capacity()];
					byteBuffer.get(bytes);
					return getMD5(bytes);
				}
			}
		else if (file.isDirectory())
		{
//			file.
		}
		throw new IOException();
	}

	public static byte[] getMD5(byte[] bytes)
	{
		MessageDigest md5;
		try
		{
			md5 = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException("Not expect to happen!");
		}
		return md5.digest(bytes);
	}

	private static MessageDigest getMD5()
	{
		try
		{
			return MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException("Not expect to happen!");
		}
	}
}
