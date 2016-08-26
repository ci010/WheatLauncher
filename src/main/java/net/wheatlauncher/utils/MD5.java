package net.wheatlauncher.utils;

import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author ci010
 */
public class MD5
{
	public static class Key
	{
		private byte[] bytes;

		public Key(byte[] bytes)
		{
			this.bytes = Arrays.copyOf(bytes, bytes.length);
		}

		public byte[] getBytes()
		{
			return Arrays.copyOf(bytes, bytes.length);
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Key key = (Key) o;

			return Arrays.equals(bytes, key.bytes);
		}

		@Override
		public int hashCode()
		{
			return Arrays.hashCode(bytes);
		}
	}

	public static Key getMD5Key(File file) throws IOException
	{
		return new Key(getMD5(file));
	}

	public static Key getMD5Key(InputStream stream) throws IOException
	{
		return new Key(getMD5(stream));
	}

	public static Key getMD5Key(byte[] bytes)
	{
		return new Key(bytes);
	}

	public static byte[] getMD5(File file) throws IOException
	{
		return getMD5(new FileInputStream(file));
	}

	public static byte[] getMD5(InputStream stream) throws IOException
	{
		return getMD5(IOUtils.toByteArray(stream));
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
}
