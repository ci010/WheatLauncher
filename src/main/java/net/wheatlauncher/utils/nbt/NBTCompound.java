package net.wheatlauncher.utils.nbt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ci010
 */
public class NBTCompound extends NBT
{
	private Map<String, NBT> map;

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
	public boolean isCompound() {return true;}

	public void put(String s, NBT tag)
	{
		if (map == null)
			map = new HashMap<>();
		map.put(s, tag);
	}

	public NBT get(String s)
	{
		if (map == null)
			map = new HashMap<>();
		return map.get(s);
	}

	public NBT remove(String s)
	{
		if (isEmpty()) return null;
		return map.remove(s);
	}

	public boolean isEmpty()
	{
		return map == null || map.isEmpty();
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
