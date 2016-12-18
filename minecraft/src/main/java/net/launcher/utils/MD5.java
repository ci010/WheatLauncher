package net.launcher.utils;

import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author ci010
 */
public class MD5
{
	private byte[] bytes;

	private MD5(byte[] bytes) {this.bytes = bytes;}

	public byte[] getBytes() {return Arrays.copyOf(bytes, bytes.length);}

	@Override
	public String toString() {return MD5.toString(bytes);}

	public static MD5 digest(Path path) throws IOException
	{
		if (Files.isRegularFile(path))
			return digest(NIOUtils.mapToBytes(path));
		else if (Files.isDirectory(path))
		{
			MessageDigest md5 = getMD5();
			Files.walkFileTree(path, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					FileVisitResult result = super.visitFile(file, attrs);
					if (result == FileVisitResult.CONTINUE)
						NIOUtils.mapToConsumer(file, md5::update);
					return result;
				}
			});
			return new MD5(md5.digest());
		}
		throw new IOException();
	}

	public static MD5 digest(File file) throws IOException {return digest(file.toPath());}

	public static MD5 digest(InputStream stream) throws IOException {return digest(IOUtils.toByteArray(stream));}

	public static MD5 digest(byte[] bytes) {return new MD5(getMD5(bytes));}

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

	public static byte[] getMD5(byte[] bytes) {return getMD5().digest(bytes);}

	private static MessageDigest getMD5()
	{
		try
		{
			return MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Not expect to happen!");
		}
	}
}
