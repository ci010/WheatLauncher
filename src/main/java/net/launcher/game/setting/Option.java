package net.launcher.game.setting;

import net.launcher.io.SourceObject;

import java.util.function.Function;

/**
 * @author ci010
 */
public abstract class Option<T>
{
	private String name;
	private T defaultV;
	private Function<T, T> function = Function.identity();
	private SourceObject.Prototype sourceType;

	public Option(SourceObject.Prototype type, String name, T defaultV)
	{
		this.sourceType = type;
		this.name = name;
		this.defaultV = defaultV;
	}

	public Option(SourceObject.Prototype sourceType, String name, T defaultV, Function<T, T> function)
	{
		this.name = name;
		this.defaultV = defaultV;
		this.function = function;
		this.sourceType = sourceType;
	}

	public String getName()
	{
		return name;
	}

	public T defaultValue()
	{
		return defaultV;
	}

	public Function<T, T> setFunction() {return function;}

	public abstract T deserialize(String s);

	public String serialize(Object tValue)
	{
		return tValue.toString();
	}

	public SourceObject.Prototype getSourceType()
	{
		return sourceType;
	}
}
