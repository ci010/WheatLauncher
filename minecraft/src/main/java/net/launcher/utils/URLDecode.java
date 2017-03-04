package net.launcher.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class URLDecode
{
	public static Map<String, String> decode(String url)
	{
		String val = url.substring(url.indexOf('?'));
		String[] split = val.split("&");
		TreeMap<String, String> treeMap = new TreeMap<>();
		for (String s : split)
		{
			String[] pair = s.split("=");
			treeMap.put(pair[0], pair[1]);
		}
		return treeMap;
	}
}
