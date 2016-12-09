package net.launcher.setting;

/**
 * @author ci010
 */
public class StringOption extends GameSetting.Option<String>
{
	private String string;

	public StringOption(GameSetting parent, String name, String string)
	{
		super(parent, name);
		this.string = string;
	}

	@Override
	public String deserialize(String s)
	{
		return s;
	}

	@Override
	public String getDefaultValue()
	{
		return string;
	}
}
