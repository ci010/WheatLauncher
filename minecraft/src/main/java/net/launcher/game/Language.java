package net.launcher.game;

import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

/**
 * @author ci010
 */
public class Language
{
	private String name, id, region;
	private boolean bidirectional;

	public Language(String name, String id, String region, boolean bidirectional)
	{
		this.name = name;
		this.id = id;
		this.region = region;
		this.bidirectional = bidirectional;
	}

	public String getName()
	{
		return name;
	}

	public String getId()
	{
		return id;
	}

	public String getRegion()
	{
		return region;
	}

	public boolean isBidirectional()
	{
		return bidirectional;
	}

	public static Deserializer<Language[], JSONObject> deserializer()
	{
		return (serialized, context) ->
		{
			JSONObject languages = serialized.getJSONObject("language");
			Language[] langs = new Language[languages.length()];
			int i = 0;
			for (String s : languages.keySet())
			{
				JSONObject language = languages.getJSONObject(s);
				langs[i++] = new Language(s, language.getString("name"), language.getString("region"), language
						.getBoolean("bidirectional"));
			}
			return langs;
		};
	}
}
