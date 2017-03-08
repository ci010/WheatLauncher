package api.launcher.setting;

import javafx.beans.property.SimpleObjectProperty;
import org.to2mbn.jmccc.internal.org.json.JSONArray;

/**
 * @author ci010
 */
public class OptionJSONArray extends SettingType.Option<String[]>
{
	public OptionJSONArray(SettingType parent, String name)
	{
		super(parent, name);
	}

	@Override
	public String[] deserialize(String s)
	{
		JSONArray objects = new JSONArray(s);
		String[] strings = new String[objects.length()];
		for (int i = 0; i < objects.length(); i++)
			strings[i] = objects.getString(i);
		return strings;
	}

	@Override
	public String serialize(Object tValue)
	{
		return new JSONArray(tValue).toString();
	}

	@Override
	public SettingProperty<String[]> getDefaultValue(Setting setting)
	{
		return new PropertyString(setting, this.getName(), new String[0], this);
	}

	public static class PropertyString extends SimpleObjectProperty<String[]> implements SettingProperty<String[]>
	{
		private SettingType.Option<String[]> option;

		public PropertyString(Setting bean, String name, SettingType.Option<String[]> option)
		{
			super(bean, name);
			this.option = option;
		}

		public PropertyString(Setting bean, String name, String[] initialValue, SettingType.Option<String[]> option)
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
		public SettingType.Option<String[]> getOption()
		{
			return option;
		}
	}
}
