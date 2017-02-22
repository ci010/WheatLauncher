package net.launcher;

import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

/**
 * @author ci010
 */
public class PluginMetaData
{
	private String pluginClass;
	private String group;
	private String id, version;

	private PluginMetaData(String pluginClass, String group, String id, String version)
	{
		this.pluginClass = pluginClass;
		this.group = group;
		this.id = id;
		this.version = version;
	}

	public String getGroup() {return group;}

	public String getPluginClass() {return pluginClass;}

	public String getId() {return id;}

	public String getVersion() {return version;}

	@Override
	public String toString()
	{
		return "PluginMetaData{" +
				"pluginClass='" + pluginClass + '\'' +
				", id='" + id + '\'' +
				", version='" + version + '\'' +
				'}';
	}

	public static Deserializer<PluginMetaData, JSONObject> deserializer()
	{
		return (serialized, context) -> new PluginMetaData(serialized.getString("class"),
				serialized.getString("group"),
				serialized.getString("id"),
				serialized.getString("version"));
	}
}
