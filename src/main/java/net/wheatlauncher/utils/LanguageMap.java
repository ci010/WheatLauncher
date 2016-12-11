package net.wheatlauncher.utils;

import java.io.PrintStream;
import java.util.*;

/**
 * @author ci010
 */
public class LanguageMap
{
	public static final LanguageMap INSTANCE = new LanguageMap();

	private Map<String, String> map = new HashMap<>();

	private Set<String> lostKey = new TreeSet<>();

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
		if (key == null) return "";
		String s = map.get(key);
		if (s == null)
		{
			lostKey.add(key);
			return key;
		}
		return s;
	}

	public String translate(String key, String fallback)
	{
		if (key == null) return "";
		String s = map.get(key);
		if (s == null)
		{
			lostKey.add(key);
			return fallback;
		}
		return s;
	}

	public void logLostKey(PrintStream stream)
	{
		for (String s : lostKey) stream.println(s);
	}
}
