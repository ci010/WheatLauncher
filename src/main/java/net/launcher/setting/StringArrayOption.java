package net.launcher.setting;

import org.to2mbn.jmccc.internal.org.json.JSONArray;

/**
 * @author ci010
 */
public class StringArrayOption extends GameSetting.Option<String[]>
{
	public StringArrayOption(GameSetting parent, String name)
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
	public String[] getDefaultValue()
	{
		return new String[0];
	}
}
