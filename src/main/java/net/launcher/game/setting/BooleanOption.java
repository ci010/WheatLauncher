package net.launcher.game.setting;

import net.launcher.io.SourceObject;

/**
 * @author ci010
 */
public class BooleanOption extends Option<Boolean>
{
	public BooleanOption(SourceObject.Prototype src, String name, boolean defaultV)
	{
		super(src, name, defaultV);
	}

	@Override
	public Boolean deserialize(String s)
	{
		return Boolean.valueOf(s);
	}
}
