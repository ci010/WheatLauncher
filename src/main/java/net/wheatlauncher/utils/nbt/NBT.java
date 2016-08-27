package net.wheatlauncher.utils.nbt;

/**
 * @author ci010
 */
public abstract class NBT
{
	NBT(NBTType type)
	{
		this.type = type;
	}

	private NBTType type;

	public NBTType getType() {return type;}

	public boolean isType(NBTType type) { return this.type == type;}

	public boolean isPrimitive() {return this instanceof NBTPrimitive;}

	public boolean isCompound() {return this instanceof NBTCompound;}

	public NBTPrimitive getAsPrimitive() {throw new UnsupportedOperationException();}

	public NBTCompound getAsCompound() {throw new UnsupportedOperationException();}

	public NBTList getAsList() {throw new UnsupportedOperationException();}

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
}
