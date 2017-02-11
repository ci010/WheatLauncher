package net.launcher.setting;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author ci010
 */
public class OptionString extends GameSettingType.Option<String>
{
	private String string;

	public OptionString(GameSettingType parent, String name, String string)
	{
		super(parent, name);
		this.string = string;
	}

	@Override
	public GameSettingProperty<String> getDefaultValue(GameSetting gameSetting)
	{
		return new PropertyString(gameSetting, this.getName(), string, this);
	}

	@Override
	public String deserialize(GameSetting gameSetting, String string)
	{
		return string;
	}

	public static class PropertyString extends SimpleStringProperty implements GameSettingProperty<String>
	{
		private GameSettingType.Option<String> option;

		public PropertyString(GameSetting bean, String name, String initialValue, GameSettingType.Option<String> option)
		{
			super(bean, name, initialValue);
			this.option = option;
		}

		@Override
		public GameSetting getBean()
		{
			return (GameSetting) super.getBean();
		}

		@Override
		public GameSettingType.Option<String> getOption()
		{
			return option;
		}
	}
}
