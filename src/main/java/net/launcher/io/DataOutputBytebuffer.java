package net.launcher.io;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author ci010
 */
public class DataOutputBytebuffer implements DataOutput
{
	private ByteBuffer buffer;

	public DataOutputBytebuffer(ByteBuffer buffer)
	{
		this.buffer = buffer;
	}

	@Override
	public void write(int b) throws IOException
	{
		buffer.putInt(b);
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		buffer.put(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		buffer.put(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {}

	@Override
	public void writeByte(int v) throws IOException
	{
		buffer.put((byte) v);
	}

	@Override
	public void writeShort(int v) throws IOException
	{
		buffer.putShort((short) v);
	}

	@Override
	public void writeChar(int v) throws IOException
	{
		buffer.putChar((char) v);
	}

	@Override
	public void writeInt(int v) throws IOException
	{
		buffer.putInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException
	{
		buffer.putLong(v);
	}

	@Override
	public void writeFloat(float v) throws IOException
	{
		buffer.putFloat(v);
	}

	@Override
	public void writeDouble(double v) throws IOException
	{
		buffer.putDouble(v);
	}

	@Override
	public void writeBytes(String s) throws IOException
	{

	}

	@Override
	public void writeChars(String s) throws IOException
	{
		buffer.putChar(s.charAt(0));
	}

	@Override
	public void writeUTF(String s) throws IOException
	{
		buffer.put(Charset.forName("UTF-8").encode(s).array());
	}
}
