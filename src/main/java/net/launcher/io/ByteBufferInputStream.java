package net.launcher.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author ci010
 */
public class ByteBufferInputStream extends InputStream
{
	private ByteBuffer buffer;

	public ByteBufferInputStream(ByteBuffer byteBuffer)
	{
		this.buffer = byteBuffer;
	}

	@Override
	public int read() throws IOException
	{
		if (this.buffer == null)
			throw new IOException("read on a closed InputStream");
		else
			return this.buffer.remaining() == 0 ? -1 : this.buffer.get() & 255;
	}

	@Override
	public int read(byte[] bytes) throws IOException
	{
		if (this.buffer == null)
			throw new IOException("read on a closed InputStream");
		else
			return this.read(bytes, 0, bytes.length);
	}

	@Override
	public int read(byte[] bytes, int off, int len) throws IOException
	{
		if (this.buffer == null)
			throw new IOException("read on a closed InputStream");
		else if (bytes == null)
			throw new NullPointerException();
		else if (off >= 0 && len >= 0 && len <= bytes.length - off)
			if (len == 0)
				return 0;
			else
			{
				int var4 = Math.min(this.buffer.remaining(), len);
				if (var4 == 0)
					return -1;
				else
				{
					this.buffer.get(bytes, off, var4);
					return var4;
				}
			}
		else
			throw new IndexOutOfBoundsException();
	}

	@Override
	public long skip(long n) throws IOException
	{
		if (this.buffer == null)
			throw new IOException("skip on a closed InputStream");
		else if (n <= 0L)
			return 0L;
		else
		{
			int var3 = (int) n;
			int var4 = Math.min(this.buffer.remaining(), var3);
			this.buffer.position(this.buffer.position() + var4);
			return (long) var3;
		}
	}

	@Override
	public int available() throws IOException
	{
		if (this.buffer == null)
			throw new IOException("available on a closed InputStream");
		else
			return this.buffer.remaining();
	}

	@Override
	public void close() throws IOException
	{
		this.buffer = null;
	}

	@Override
	public synchronized void mark(int var1)
	{}

	@Override
	public synchronized void reset() throws IOException
	{
		throw new IOException("mark/reset not supported");
	}

	@Override
	public boolean markSupported()
	{
		return false;
	}
}
