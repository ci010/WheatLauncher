package net.wheatlauncher.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author ci010
 */
public class LanguageMap
{
	public static final LanguageMap INSTANCE = new LanguageMap();

	private Map<String, String> map = new HashMap<>();

	private LanguageMap()
	{
		Locale aDefault = Locale.getDefault();
		ResourceBundle lang = ResourceBundle.getBundle("lang", aDefault);
		for (String s : lang.keySet())
			map.put(s, lang.getString(s));
		lang = ResourceBundle.getBundle("lang", Locale.ENGLISH);
		for (String s : lang.keySet())
			if (!map.containsKey(s))
				map.put(s, lang.getString(s));
	}

	public String translate(String key)
	{
		String s = map.get(key);
		return s == null ? key : s;
	}

	public String translate(String key, String fallback)
	{
		String s = map.get(key);
		return s == null ? fallback : s;
	}
}
