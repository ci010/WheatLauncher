package net.launcher.setting;

import net.launcher.io.SourceObject;

/**
 * @author ci010
 */
public class StringOption extends Option<String>
{
	public StringOption(SourceObject.Prototype type, String name, String defaultV)
	{
		super(type, name, defaultV);
	}

	@Override
	public String deserialize(String s)
	{
		return s;
	}
}
