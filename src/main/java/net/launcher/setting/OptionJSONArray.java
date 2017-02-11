package net.launcher.setting;

import javafx.beans.property.SimpleObjectProperty;
import org.to2mbn.jmccc.internal.org.json.JSONArray;

/**
 * @author ci010
 */
public class OptionJSONArray extends GameSettingType.Option<String[]>
{
	public OptionJSONArray(GameSettingType parent, String name)
	{
		super(parent, name);
	}

	@Override
	public String[] deserialize(GameSetting gameSetting, String s)
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
	public GameSettingProperty<String[]> getDefaultValue(GameSetting gameSetting)
	{
		return new PropertyString(gameSetting, this.getName(), new String[0], this);
	}

	public static class PropertyString extends SimpleObjectProperty<String[]> implements GameSettingProperty<String[]>
	{
		private GameSettingType.Option<String[]> option;

		public PropertyString(GameSetting bean, String name, GameSettingType.Option<String[]> option)
		{
			super(bean, name);
			this.option = option;
		}

		public PropertyString(GameSetting bean, String name, String[] initialValue, GameSettingType.Option<String[]> option)
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
		public GameSettingType.Option<String[]> getOption()
		{
			return option;
		}
	}
}
