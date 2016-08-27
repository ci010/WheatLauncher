package net.wheatlauncher.utils.nbt;

/**
 * @author ci010
 */
public class NBTEnd extends NBT
{
	NBTEnd()
	{
		super(NBTType.NULL);
	}

	@Override
	public NBT clone()
	{
		return new NBTEnd();
	}
}
