package net.launcher.game.setting;

import net.launcher.io.SourceObject;
import org.to2mbn.jmccc.internal.org.json.JSONArray;

/**
 * @author ci010
 */
public class StringArrayOption extends Option<String[]>
{
	public StringArrayOption(SourceObject.Prototype src, String name, String[] defaultV)
	{
		super(src, name, defaultV);
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
}
