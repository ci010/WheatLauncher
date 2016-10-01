package net.launcher.utils.nbt;

import java.util.*;

/**
 * @author ci010
 */
public class NBTCompound extends NBT
{
	private Map<String, NBT> map = new LinkedHashMap<>();

	NBTCompound(Map<String, NBT> map)
	{
		this();
		this.map = map;
	}

	NBTCompound()
	{
		super(NBTType.COMPOUND);
	}

	@Override
	public NBTCompound getAsCompound() {return this;}

	@Override
	public NBT clone()
	{
		NBTCompound copy = new NBTCompound();
		for (Map.Entry<String, NBT> tag : map.entrySet())
			copy.put(tag.getKey(), tag.getValue().clone());
		return copy;
	}

	@Override
	public boolean isCompound() {return true;}

	public void put(String s, NBT tag)
	{
		map.put(s, tag);
	}

	public NBT get(String s)
	{
		return map.get(s);
	}

	public Optional<NBT> option(String s)
	{
		return Optional.ofNullable(map.get(s));
	}

	public NBT remove(String s)
	{
		if (isEmpty()) return null;
		return map.remove(s);
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Set<Map.Entry<String, NBT>> entrySet() {return map.entrySet();}

	public int size() {return map.size();}

	public Collection<NBT> values() {return map.values();}

	public void clear() {map.clear();}

	public void putAll(Map<? extends String, ? extends NBT> m) {map.putAll(m);}

	public boolean containsKey(String key) {return map.containsKey(key);}

	public Set<String> keySet()
	{
		return map.keySet();
	}
}
