package net.launcher.utils.nbt;


import java.util.Arrays;

/**
 * @author ci010
 */
public class NBTPrimitive extends NBT
{
	private Object v;

	NBTPrimitive(Object o, NBTType type)
	{
		super(type);
		v = o;
	}

	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	@Override
	public NBTPrimitive getAsPrimitive() {return this;}

	@Override
	public NBT clone()
	{
		if (this.isType(NBTType.INT_ARR))
		{
			int[] array = getAsIntArray();
			return new NBTPrimitive(Arrays.copyOf(array, array.length), NBTType.INT_ARR);
		}
		if (this.isType(NBTType.BYTE_ARR))
		{
			byte[] array = getAsByteArray();
			return new NBTPrimitive(Arrays.copyOf(array, array.length), NBTType.BYTE_ARR);
		}
		return new NBTPrimitive(v, this.getType());
	}

	public String getAsString() {return (String) v;}

	public int getAsInt() {return (int) v;}

	public byte getAsByte() {return (byte) v;}

	public long getAsLong() {return (long) v;}

	public short getAsShort() {return (short) v;}

	public float getAsFloat() {return (float) v;}

	public double getAsDouble() {return (double) v;}

	public byte[] getAsByteArray() {return (byte[]) v;}

	public int[] getAsIntArray() {return (int[]) v;}

	public boolean getAsBoolean() {return getAsByte() != 0;}
}
