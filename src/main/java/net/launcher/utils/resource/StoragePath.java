package net.launcher.utils.resource;

import org.to2mbn.jmccc.internal.org.json.JSONObject;

/**
 * @author ci010
 */
public class StoragePath
{
	private String path;
	private JSONObject object;

	public StoragePath(String path, JSONObject object)
	{
		this.path = path;
		this.object = object;
	}

	public String getPath()
	{
		return path;
	}

	public JSONObject getJSON()
	{
		return object;
	}
}
