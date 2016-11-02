package net.launcher.game.nbt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class NBTList extends NBT implements Iterable<NBT>
{
	private NBTType type;
	private List<NBT> lst = new ArrayList<>();

	NBTList(List<NBT> list)
	{
		this();
		this.lst = list;
	}

	NBTList()
	{
		super(NBTType.LIST);
	}

	public int size()
	{
		return lst.size();
	}

	public boolean add(NBT base)
	{
		if (base.getType() == NBTType.NULL) return false;
		return validate(base) && lst.add(base);
	}

	public NBT get(int i)
	{
		return lst.get(i);
	}

	public NBT remove(int i)
	{
		return lst.remove(i);
	}

	public boolean isEmpty()
	{
		return lst.isEmpty();
	}

	public NBT set(int i, NBT base)
	{
		if (base.getType() == NBTType.NULL) return null;
		if (validate(base))
			return lst.set(i, base);
		return null;
	}

	private boolean validate(NBT base)
	{
		if (this.type == null) type = base.getType();
		return this.type == base.getType();
	}

	public NBTType getContentType()
	{
		return type;
	}

	@Override
	public Iterator<NBT> iterator()
	{
		return lst.iterator();
	}

	@Override
	public NBTList asList()
	{
		return this;
	}

	@Override
	public NBT clone()
	{
		NBTList copy = new NBTList();
		for (NBT nbt : this)
			copy.add(nbt.clone());
		return copy;
	}

	@Override
	public Object asRaw()
	{
		return this.lst.stream().map(NBT::asRaw).collect(Collectors.toList());
	}

	@Override
	public boolean isList()
	{
		return true;
	}
}
