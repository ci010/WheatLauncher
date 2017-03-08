package net.launcher.game;

import net.launcher.utils.serial.BiSerializer;
import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class ModManifest
{
	private Map<String, Map<String, String>> allMods;

	public ModManifest(Map<String, Map<String, String>> allMods)
	{
		this.allMods = allMods;
	}

	public Set<String> getAllTypes()
	{
		return allMods.keySet();
	}

	public Map<String, String> getMods(String modType)
	{
		return allMods.get(modType);
	}

	@Override
	public String toString()
	{
		return "ModManifest{" +
				"allMods=" + allMods +
				'}';
	}

	public static Deserializer<ModManifest, JSONObject> objDeserializer()
	{
		return (serialized, context) ->
		{
			String type = serialized.optString("type");
			JSONArray array = serialized.getJSONArray("modList");
			Map<String, String> modVersions = new TreeMap<>();
			for (int i = 0; i < array.length(); i++)
			{
				JSONObject mod = array.getJSONObject(i);
				modVersions.put(mod.getString("modid"), mod.getString("version"));
			}
			return new ModManifest(Collections.singletonMap(type, modVersions));
		};
	}

	public static BiSerializer<ModManifest, JSONArray> serializer()
	{
		return BiSerializer.combine((data, context) ->
		{
			JSONArray arr = new JSONArray();
			for (Map.Entry<String, Map<String, String>> entry : data.allMods.entrySet())
			{
				JSONObject object = new JSONObject();
				object.put("type", entry.getKey());
				JSONArray map = new JSONArray();
				for (Map.Entry<String, String> e : entry.getValue().entrySet())
				{
					JSONObject o = new JSONObject();
					o.put("modid", e.getKey());
					o.put("version", e.getValue());
					map.put(o);
				}
				object.put("modList", map);
				arr.put(object);
			}
			return arr;
		}, (serialized, context) ->
		{
			Map<String, Map<String, String>> map = new TreeMap<>();
			for (int j = 0; j < serialized.length(); j++)
			{
				JSONObject object = serialized.getJSONObject(j);
				String type = object.optString("type");
				JSONArray array = object.getJSONArray("modList");
				Map<String, String> modVersions = new TreeMap<>();
				for (int i = 0; i < array.length(); i++)
				{
					JSONObject mod = array.getJSONObject(i);
					modVersions.put(mod.getString("modid"), mod.getString("version"));
				}
				map.put(type, modVersions);
			}
			return new ModManifest(map);
		});
	}
}
