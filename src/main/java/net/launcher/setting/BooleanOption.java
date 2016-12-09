package net.launcher.setting;

/**
 * @author ci010
 */
public class BooleanOption extends GameSetting.Option<Boolean>
{
	private boolean defValue;

	public BooleanOption(GameSetting parent, String name, boolean defValue)
	{
		super(parent, name);
		this.defValue = defValue;
	}

	@Override
	public Boolean deserialize(String s)
	{
		return Boolean.valueOf(s);
	}

	@Override
	public Boolean getDefaultValue() {return defValue;}
}
