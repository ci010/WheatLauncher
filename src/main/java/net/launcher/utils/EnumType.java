package net.launcher.utils;

/**
 * @author ci010
 */
public abstract class EnumType<T extends EnumType<T>>
{
	private int ordinate;
	private String name;

	public int ordinal()
	{
		return ordinate;
	}

	public String name()
	{
		return name;
	}

	protected EnumType(String name)
	{
		EnumTypeClass<T> type = getEnumType();
		this.ordinate = type.size();
		this.name = name;
		type.grow(this);
	}

	protected EnumType()
	{
		EnumTypeClass<T> type = getEnumType();
		this.ordinate = type.size();
		this.name = this.getClass().getSimpleName();
		type.grow(this);
	}

	public abstract EnumTypeClass<T> getEnumType();

	public T getRealType()
	{
		return (T) this;
	}
}
