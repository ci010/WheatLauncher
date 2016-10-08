package net.launcher.utils;

import javax.naming.directory.InitialDirContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Hashtable;

/**
 * @author ci010
 */
public class MessageUtils
{
	public static InetSocketAddress getAddress(String host)
	{
		if (host == null) return null;
		String[] split = host.split(":");
		if (host.startsWith("["))
		{
			int end = host.indexOf("]");
			if (end > 0)
			{
				String ip = host.substring(1, end);
				String port = host.substring(end + 1).trim();
				if (port.startsWith(":") && !port.isEmpty()) split = new String[]{ip, port.substring(1)};
				else split = new String[]{ip};
			}
		}

		if (split.length > 2) split = new String[]{host};

		String ip = split[0];
		int port = 25565;
		try
		{
			port = Integer.parseInt(split[1]);
		}
		catch (Exception ignored) {}

		if (port == 25565)
		{
			String[] result = getServerAddress(ip);
			ip = result[0];
			try
			{
				port = Integer.parseInt(result[1]);
			}
			catch (Exception ignored) {}
		}

		return new InetSocketAddress(ip, port);
	}

	private static String[] getServerAddress(String hostString)
	{
		try
		{
			Class.forName("com.sun.jndi.dns.DnsContextFactory");
			Hashtable<String, String> hashtable = new Hashtable<>();
			hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
			hashtable.put("java.naming.provider.url", "dns:");
			hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
			String[] arr = new InitialDirContext(hashtable).getAttributes("_minecraft._tcp." + hostString,
					new String[]{"SRV"}).get("srv").get().toString().split(" ", 4);
			return new String[]{arr[3], arr[2]};
		}
		catch (Throwable throwable)
		{
			return new String[]{hostString, Integer.toString(25565)};
		}
	}

	public static void writeVarInt(ByteBuffer byteBuffer, int input)
	{
		while ((input & -128) != 0)
		{
			byte b = (byte) (input & 127 | 128);
			byteBuffer.put(b);
			input >>>= 7;
		}
		byteBuffer.put((byte) input);
	}

	public static int readVarInt(ByteBuffer byteBuffer) throws IOException
	{
		int i = 0;
		int j = 0;
		for (; ; )
		{
			int k = byteBuffer.get();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5)
				throw new IOException("VarInt too big: " + j);
			if ((k & 0x80) != 128)
				break;
		}
		return i;
	}

	public static void writeString(ByteBuffer byteBuffer, String s)
	{
		byte[] bytes = s.getBytes(Charset.forName("UTF-8"));
		writeVarInt(byteBuffer, bytes.length);
		byteBuffer.put(bytes);
	}
}
