package test.wheatlauncher;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author ci010
 */
public class TestPing
{
	public void writeVarInt(ByteBuffer byteBuffer, int input)
	{
		System.out.println("Start to write var int " + input);
		while ((input & -128) != 0)
		{
			byte b = (byte) (input & 127 | 128);
			System.out.println("loop " + b);
			byteBuffer.put(b);
			input >>>= 7;
		}
		System.out.println((byte) input);
		byteBuffer.put((byte) input);
	}

	void writeString(ByteBuffer byteBuffer, String s)
	{
		byte[] bytes = s.getBytes(Charset.forName("UTF-8"));
		writeVarInt(byteBuffer, bytes.length);
		byteBuffer.put(bytes);
	}


	@Test
	public void pingLegacy() throws IOException
	{
//		char s = '\u0000';
//		System.out.println((int) s);
//		System.out.println("ﾧ");
//		System.out.println((int) 'ﾧ');
//		StringBuilder str = new StringBuilder();
//
//		SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 25565));
//		ByteBuffer read = ByteBuffer.allocateDirect(1024);
//
//		ByteBuffer legacy = ByteBuffer.allocate(1);
//		legacy.put((byte) 0xFE);
//		legacy.flip();
//		channel.write(legacy);
//
//		channel.read(read);
//		read.flip();
//
//		byte[] b = new byte[read.limit()];
//		read.get(b);
//		for (byte b1 : b)
//			if (b1 != -1 && b1 != 0 && b1 != 23)
//				System.out.print(b1 + " ");
//		System.out.println();
//		for (byte b1 : b)
//			if (b1 != -1 && b1 != 0 && b1 != 23)
//				str.append((char) b1);
//		System.out.println(str);
	}

	@Test
	public void pingA() throws IOException
	{
//		SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 25565));
//
//		String ip = "127.0.0.1";
//		ByteBuffer bytebuf = ByteBuffer.allocate(256);
//		bytebuf.put((byte) 254);
//		bytebuf.put((byte) 1);
//		bytebuf.put((byte) 250);
//		char[] achar = "MC|PingHost".toCharArray();
//		bytebuf.putShort((short) achar.length);
//
//		for (char c0 : achar)
//		{
//			bytebuf.putChar(c0);
//		}
//
//		bytebuf.putShort((short) (7 + 2 * ip.length()));
//		bytebuf.put((byte) 127);
//		achar = ip.toCharArray();
//		bytebuf.putShort((short) achar.length);
//
//		for (char c1 : achar)
//		{
//			bytebuf.putChar(c1);
//		}
//
//		bytebuf.putInt(25565);
//		bytebuf.flip();
//
//		channel.write(bytebuf);
//
//		bytebuf.clear();
//
//		channel.read(bytebuf);
//		bytebuf.flip();
//
//		int head = Byte.toUnsignedInt(bytebuf.get());// size
//		int siz = bytebuf.getShort();
//		System.out.println(head);
//		System.out.println(siz);
//		byte[] bytes = new byte[siz * 2];
//		bytebuf.get(bytes);


//		boolean isEven = false;
//		for (byte b : bytes1)
//		{
//			if (!isEven)
//				if (b == 0) builder.append("\0");
//				else builder.append((char) b);
//			isEven = !isEven;
//		}
//		String[] split = builder.toString().split("\0");
//		for (String s : split)
//			System.out.println(s);
//		System.out.println(builder);
//		if (short1 == 255)
		{
//			short aShort = (short) (bytebuf.getShort());
//
//			byte[] bytes = new byte[aShort];
//			bytebuf.get(bytes);
//			String s = new String(bytes, Charset.forName("UTF_16BE"));
//			String[] astring = s.split("\u0000");
//
//			for (String s1 : astring)
//			{
//				System.out.println(s1);
//			}
//			if ("\u00a71".equals(astring[0]))
//			{
//
//				int i = MathHelper.parseIntWithDefault(astring[1], 0);
//				String gameVersion = astring[2];
//				String MOTD = astring[3];
//				System.out.println(s);
//				int online = MathHelper.parseIntWithDefault(astring[4], -1);
//				int capability = MathHelper.parseIntWithDefault(astring[5], -1);
//				server.populationInfo = TextFormatting.GRAY + "" + j + "" + TextFormatting.DARK_GRAY + "/" + TextFormatting.GRAY + k;
//			}
		}
	}

	@Test
	public void pingL() throws IOException
	{
//		StringBuilder str = new StringBuilder();
//
//		Socket socket = new Socket("127.0.0.1", 25565);
//
//		socket.setSoTimeout(3000);
//		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//		DataInputStream in = new DataInputStream(socket.getInputStream());
//
//
//		int b;
//		while ((b = in.read()) != -1)
//			if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24)
//				str.append((char) b);
//		System.out.println(str);
	}


}
