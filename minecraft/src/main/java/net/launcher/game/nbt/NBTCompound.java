package net.launcher.game.nbt;

import java.util.*;
import java.util.stream.Collectors;

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
	public NBTCompound asCompound() {return this;}

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

	public NBTCompound put(String s, NBT tag)
	{
		map.put(s, tag);
		return this;
	}

	public NBTCompound put(String s, String v)
	{
		map.put(s, NBT.string(v));
		return this;

	}

	public NBTCompound put(String s, Number n)
	{
		map.put(s, NBT.number(n));
		return this;
	}

	public NBTCompound put(String s, boolean b)
	{
		map.put(s, NBT.bool(b));
		return this;
	}

	public NBTCompound option(String s, NBT nbt)
	{
		if (s == null || nbt == null || nbt == NBT.empty()) return this;
		put(s, nbt);
		return this;
	}

	public NBTCompound option(String s, String v)
	{
		if (s == null || v == null) return this;
		put(s, v);
		return this;
	}

	public NBTCompound option(String s, Number n)
	{
		if (s == null || n == null) return this;
		put(s, n);
		return this;
	}

	public NBTCompound option(String s, boolean b)
	{
		if (s == null) return this;
		put(s, b);
		return this;
	}

	public NBT get(String s)
	{
		NBT nbt = map.get(s);
		if (nbt == null)
			nbt = NBT.empty();
		return nbt;
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

	@Override
	public Object asRaw()
	{
		return map.entrySet().stream().map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), e.getValue().asRaw()
		)).collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));
	}

	@Override
	public String toString()
	{
		return "NBTCompound{" +
				"map=" + map +
				'}';
	}
}
