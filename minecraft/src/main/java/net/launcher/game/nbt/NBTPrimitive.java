package net.launcher.game.nbt;


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
	public NBTPrimitive asPrimitive() {return this;}

	@Override
	public NBT clone()
	{
		if (this.isType(NBTType.INT_ARR))
		{
			int[] array = asIntArray();
			return new NBTPrimitive(Arrays.copyOf(array, array.length), NBTType.INT_ARR);
		}
		if (this.isType(NBTType.BYTE_ARR))
		{
			byte[] array = asByteArray();
			return new NBTPrimitive(Arrays.copyOf(array, array.length), NBTType.BYTE_ARR);
		}
		return new NBTPrimitive(v, this.getType());
	}

	public String asString() {return (String) v;}

	public int asInt() {return (int) v;}

	public byte asByte() {return (byte) v;}

	public long asLong() {return (long) v;}

	public short asShort() {return (short) v;}

	public float asFloat() {return (float) v;}

	public double asDouble() {return (double) v;}

	public byte[] asByteArray() {return (byte[]) v;}

	public int[] asIntArray() {return (int[]) v;}

	public boolean asBool() {return asByte() != 0;}

	public Object asRaw()
	{
		return v;
	}

	@Override
	public String toString()
	{
		return String.valueOf(v);
	}
}
