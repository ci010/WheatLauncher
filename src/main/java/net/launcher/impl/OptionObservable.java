package net.launcher.impl;

import net.launcher.game.GameSettings;

/**
 * @author ci010
 */
public class OptionObservable implements GameSettings.Option
{
	private String id;
	private GameSettings.OptionType type;

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public GameSettings.OptionType getType()
	{
		return type;
	}

	@Override
	public boolean setValue(Object o)
	{
		return false;
	}

	@Override
	public Object getValue()
	{
		return null;
	}

	@Override
	public String getStringValue()
	{
		return null;
	}
}
