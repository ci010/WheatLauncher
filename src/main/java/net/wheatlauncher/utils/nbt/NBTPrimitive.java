package net.wheatlauncher.utils.nbt;


/**
 * @author ci010
 */
public class NBTPrimitive extends NBT
{
	Object v;

	NBTPrimitive(Object o, NBTType type)
	{
		super(type);
		v = o;
	}

	@Override
	public NBTPrimitive getAsPrimitive() {return this;}

	public String getAsString() {return (String) v;}

	public int getAsInt() {return (int) v;}

	public byte getAsByte() {return (byte) v;}

	public long getAsLong() {return (long) v;}

	public short getAsShort() {return (short) v;}

	public float getAsFloat() {return (float) v;}

	public double getAsDouble() {return (double) v;}

	public byte[] getAsByteArray() {return (byte[]) v;}

	public int[] getAsIntArray() {return (int[]) v;}


}
