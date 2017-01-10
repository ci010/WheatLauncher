package test.wheatlauncher;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author ci010
 */
public class PngTest
{

	static Pair<Integer, Integer> wh(String png)
	{
		ByteBuffer allocate = ByteBuffer.wrap(png.getBytes());
		for (int i = 0; i < 4; i++)
			allocate.getInt();
		int w = allocate.getInt();
		int h = allocate.getInt();
		return new Pair<>(w, h);
	}

	static Pair<Integer, Integer> wh(byte[] png)
	{
		ByteBuffer allocate = ByteBuffer.wrap(png);
		for (int i = 0; i < 4; i++)
			allocate.getInt();
		int w = allocate.getInt();
		int h = allocate.getInt();
		return new Pair<>(w, h);
	}


	//	@Test
	public void pngTest() throws IOException
	{
		File file = new File("C:\\Users\\CIJhn\\Downloads\\winterboy.png");
		FileChannel open = FileChannel.open(file.toPath());
		ByteBuffer allocate = ByteBuffer.allocate(1024);
		open.read(allocate);
		allocate.flip();

		for (int i = 0; i < 4; i++)
			allocate.getInt();

		System.out.println(allocate.getInt());
		System.out.println(allocate.getInt());
//		System.out.println(allocate.limit());
//		byte[] bytes = new byte[allocate.limit()];
//		allocate.get(bytes);
//
//		allocate.flip();

//		byte[] arr = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0x00, 0x00, 0x00, 0x0d, 0x49,
//					  0x48, 0x44, 0x52};
//		for (int i = 0; i < arr.length; i++)
//		{
//			if(bytes[i]!=arr[i])
//			{
//				break;
//			}
//		}
//		for (byte aByte : bytes)
//			System.out.print(aByte);
//		System.out.println();

	}
}
