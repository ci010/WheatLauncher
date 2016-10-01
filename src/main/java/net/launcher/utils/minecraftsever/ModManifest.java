package net.launcher.utils.minecraftsever;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author ci010
 */
public class ModManifest
{
	public void test() throws IOException
	{
		Socket test = new Socket("test", 25565);
//		test.getInputStream()
	}

	public String[] pingSever(String severIp)
	{
		try
		{
			Socket sock = new Socket(severIp, 25565);
			sock.setSoTimeout(3000);
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			DataInputStream in = new DataInputStream(sock.getInputStream());

			out.write(0xFE);

			int b;
			StringBuilder str = new StringBuilder();
			while ((b = in.read()) != -1)
				if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24)
					str.append((char) b);
			return str.toString().split("§");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
