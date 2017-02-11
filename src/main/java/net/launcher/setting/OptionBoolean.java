package net.launcher.setting;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author ci010
 */
public class OptionBoolean extends GameSettingType.Option<Boolean>
{
	private boolean defValue;

	public OptionBoolean(GameSettingType parent, String name, boolean defValue)
	{
		super(parent, name);
		this.defValue = defValue;
	}

	@Override
	public Boolean deserialize(GameSetting gameSetting, String s)
	{
		return Boolean.valueOf(s);
	}

	@Override
	public GameSettingProperty<Boolean> getDefaultValue(GameSetting gameSetting)
	{
		return new PropertyBool(gameSetting, this.getName(), defValue, this);
	}

	public static class PropertyBool extends SimpleBooleanProperty implements GameSettingProperty<Boolean>
	{
		private GameSettingType.Option<Boolean> booleanOption;

		public PropertyBool(GameSetting bean, String name, boolean initialValue, GameSettingType.Option<Boolean> booleanOption)
		{
			super(bean, name, initialValue);
			this.booleanOption = booleanOption;
		}

		@Override
		public GameSetting getBean() {return (GameSetting) super.getBean();}

		@Override
		public GameSettingType.Option<Boolean> getOption()
		{
			return booleanOption;
		}
	}
}
