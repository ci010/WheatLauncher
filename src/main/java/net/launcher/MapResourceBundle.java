package net.launcher;

import sun.util.ResourceBundleEnumeration;

import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author ci010
 */
public class MapResourceBundle extends ResourceBundle
{
	private Map<String, Object> properties;

	public MapResourceBundle(Map<String, Object> properties)
	{
		this.properties = properties;
	}

	@Override
	protected Object handleGetObject(String key)
	{
		return properties.get(key);
	}

	@Override
	public Enumeration<String> getKeys()
	{
		return new ResourceBundleEnumeration(properties.keySet(), null);
	}

	protected Set<String> handleKeySet()
	{
		return properties.keySet();
	}
}
