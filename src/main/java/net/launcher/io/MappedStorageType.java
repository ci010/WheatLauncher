package net.launcher.io;

import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public interface MappedStorageType
{
	Map<String, String> deserialize(String string);

	String serialize(Map<String, String> pair);

	MappedStorageType JSON = new MappedStorageType()
	{
		@Override
		public Map<String, String> deserialize(String string)
		{
			return new JSONObject(string).toMap().entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, vMap -> vMap.getValue().toString()));
		}

		@Override
		public String serialize(Map<String, String> pair)
		{
			return new JSONObject(pair).toString();
		}
	}, MC = new MappedStorageType()
	{
		@Override
		public Map<String, String> deserialize(String string)
		{
			Map<String, String> map = new HashMap<>();
			String[] split = string.split("\n");
			for (String s : split)
			{
				String[] keyVPair = s.split(":");
				map.put(keyVPair[0], keyVPair[1]);
			}
			return map;
		}

		@Override
		public String serialize(Map<String, String> pair)
		{
			return pair.entrySet().stream()
					.map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining("\n"));
		}
	}, PROPERTIES = new MappedStorageType()
	{
		@Override
		public Map<String, String> deserialize(String string)
		{
			Properties properties = new Properties();
			try
			{
				properties.load(new StringReader(string));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return properties.entrySet().stream().collect(Collectors.toMap(kMap -> kMap.getKey().toString(), vMap -> vMap.getValue()
					.toString()));
		}

		@Override
		public String serialize(Map<String, String> pair)
		{
			Properties properties = new Properties();
			pair.forEach(properties::put);
			String s = null;
			try (StringWriter writer = new StringWriter())
			{
				properties.store(writer, null);
				s = writer.toString();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return s;
		}
	};
}
