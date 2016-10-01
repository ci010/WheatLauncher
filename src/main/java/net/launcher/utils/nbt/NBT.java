package net.launcher.utils.nbt;

import java.io.*;

/**
 * @author ci010
 */
public abstract class NBT implements Cloneable
{
	NBT(NBTType type)
	{
		this.type = type;
	}

	private NBTType type;

	public NBTType getType() {return type;}

	public boolean isType(NBTType type) { return this.type == type;}

	public boolean isPrimitive() {return false;}

	public boolean isCompound() {return false;}

	public boolean isList() {return false;}

	public NBTPrimitive getAsPrimitive() {throw new UnsupportedOperationException();}

	public NBTCompound getAsCompound() {throw new UnsupportedOperationException();}

	public NBTList getAsList() {throw new UnsupportedOperationException();}

	public abstract NBT clone();

	public static NBTPrimitive number(Number number)
	{
		try
		{
			NBTType nbtType = NBTType.valueOf(number.getClass().getSimpleName());
			return new NBTPrimitive(number, nbtType);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Unknown type of number for NBT!", e);
		}
	}

	public static NBTPrimitive bool(boolean bool)
	{
		return number((byte) (bool ? 1 : 0));
	}

	public static NBTPrimitive string(String s)
	{
		return new NBTPrimitive(s, NBTType.STRING);
	}

	public static NBTPrimitive intArr(int[] arr)
	{
		return new NBTPrimitive(arr, NBTType.INT_ARR);
	}

	public static NBTPrimitive byteArr(byte[] arr)
	{
		return new NBTPrimitive(arr, NBTType.BYTE_ARR);
	}

	public static NBTCompound compound()
	{
		return new NBTCompound();
	}

	public static NBTList list() {return new NBTList();}

	public static NBT read(InputStream stream, boolean isCompressed) throws IOException
	{
		return NBTType.readTag(stream, isCompressed);
	}

	public static NBT read(File file, boolean isCompressed) throws IOException
	{
		if (!file.exists()) return null;
		return read(new FileInputStream(file), isCompressed);
	}

	public static void write(File file, NBTCompound compound, boolean isCompressed) throws IOException
	{
		write(new FileOutputStream(file), compound, isCompressed);
	}

	public static void write(OutputStream stream, NBTCompound compound, boolean isCompressed) throws IOException
	{
		NBTType.writeTag(stream, compound, isCompressed);
	}
}
