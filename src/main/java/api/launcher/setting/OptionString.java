package api.launcher.setting;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author ci010
 */
public class OptionString extends SettingType.Option<String>
{
	private String string;

	public OptionString(SettingType parent, String name, String string)
	{
		super(parent, name);
		this.string = string;
	}

	@Override
	public SettingProperty<String> getDefaultValue(Setting setting)
	{
		return new PropertyString(setting, this.getName(), string, this);
	}

	@Override
	public String deserialize(String string)
	{
		return string;
	}

	public static class PropertyString extends SimpleStringProperty implements SettingProperty<String>
	{
		private SettingType.Option<String> option;

		public PropertyString(Setting bean, String name, String initialValue, SettingType.Option<String> option)
		{
			super(bean, name, initialValue);
			this.option = option;
		}

		@Override
		public Setting getBean()
		{
			return (Setting) super.getBean();
		}

		@Override
		public SettingType.Option<String> getOption()
		{
			return option;
		}
	}
}
