package net.launcher.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ci010
 */
public final class EnumTypeClass<T extends EnumType<T>>
{
	private static Map<String, EnumTypeClass> registerMap = new ConcurrentHashMap<>();

	public static <T extends EnumType<T>> EnumTypeClass<T> valueOf(Class<T> type, String className)
	{
		EnumTypeClass enumLikeClass = registerMap.get(className);
		if (enumLikeClass.getRealType() != type)
			throw new IllegalArgumentException("The type mismatch " + type + " with " + enumLikeClass.getRealType());
		return enumLikeClass;
	}

	public static <T extends EnumType<T>> EnumTypeClass<T> valueOf(Class<T> type)
	{
		return valueOf(type, type.getSimpleName());
	}

	public static <T extends EnumType<T>> EnumTypeClass<T> define(Class<T> type, String className)
	{
		EnumTypeClass<T> likeClass = new EnumTypeClass<T>(type, className);
		registerMap.put(className, likeClass);
		return likeClass;
	}

	public static <T extends EnumType<T>> EnumTypeClass<T> define(Class<T> type)
	{
		return define(type, type.getSimpleName());
	}

	private java.lang.Class<T> realClass;
	private ArrayList<EnumType<T>> instances;
	private TreeMap<String, Byte> map;
	private String name;

	private EnumTypeClass(Class<T> realClass, String className)
	{
		this.realClass = realClass;
		instances = new ArrayList<>();
		map = new TreeMap<>();
		this.name = className;
	}

	public String name()
	{
		return name;
	}

	public EnumType<T> valueOf(String name)
	{
		return instances.get(map.get(name));
	}

	public EnumType<T>[] values()
	{
		EnumType<T>[] enumTypes = new EnumType[instances.size()];
		return instances.toArray(enumTypes);
	}

	public int size()
	{
		return instances.size();
	}

	public java.lang.Class<T> getRealType()
	{
		return realClass;
	}

	void grow(EnumType<T> instance)
	{
		if (map.containsKey(instance.name()))
			throw new IllegalArgumentException("Duplicated enum instance getId!");
		map.put(instance.name(), (byte) instances.size());
		instances.add(instance);
	}
}
