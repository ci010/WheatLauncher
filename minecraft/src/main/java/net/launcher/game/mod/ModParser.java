package net.launcher.game.mod;/**
 * @author ci010
 */

import jdk.internal.org.objectweb.asm.ClassReader;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.Patterns;
import net.launcher.utils.StringUtils;
import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModParser
{
	public static ModParser create(Deserializer<Mod[], Path> deserializer)
	{
		Objects.requireNonNull(deserializer);
		return new ModParser(deserializer);
	}

	public static ModParser create() {return new ModParser(defaultModDeserializer());}

	public Mod[] parseFile(Path path)
	{
		Objects.requireNonNull(path);
		return deserializer.deserialize(path);
	}

	public Mod[] parseFile(Path path, Consumer<Throwable> exceptionHandler)
	{
		Objects.requireNonNull(path);
		return deserializer.deserializeWithException(path, exceptionHandler);
	}

	public Mod[] parseJson(String json)
	{
		Objects.requireNonNull(json);
		return jsonParser().deserialize(json);
	}

	private Deserializer<Mod[], Path> deserializer;

	public static Deserializer<Mod[], Path> defaultModDeserializer()
	{
		return (path, context) ->
		{
			List<Mod> releases = new ArrayList<>();
			Path modInf = path.resolve("/mcmod.info");
			Map<String, JSONObject> cacheInfoMap = new HashMap<>();
			final Map<String, Map<String, Object>> annotationMap = new HashMap<>();
			try
			{
				String modInfoString = NIOUtils.readToString(modInf);
				JSONArray arr = modInfoString.startsWith("{") ? new JSONObject(modInfoString).getJSONArray("modList") :
						new JSONArray(modInfoString);

				for (int i = 0; i < arr.length(); i++)
				{
					String modid = arr.getJSONObject(i).optString("modid");
					if (StringUtils.isNotEmpty(modid)) cacheInfoMap.put(modid, arr.getJSONObject(i));
				}

				Set<Map<String, Object>> set = new HashSet<>();
				for (Path p : Files.walk(path).filter(pa ->
						pa.getFileName() != null && Patterns.CLASS_FILE.matcher(pa.getFileName().toString()).matches())
						.collect(Collectors.toList()))
				{
					set.clear();
					ClassReader reader = new ClassReader(NIOUtils.readToBytes(p));
					reader.accept(new ModAnnotationVisitor(set), 0);
					if (!set.isEmpty())
						for (Map<String, Object> stringObjectMap : set)
						{
							String modid = stringObjectMap.get("modid").toString();
							if (StringUtils.isNotEmpty(modid)) annotationMap.put(modid, stringObjectMap);
						}
				}
				set = null;
				Set<String> union = new HashSet<>(cacheInfoMap.keySet());
				union.addAll(annotationMap.keySet());
				for (String s : union)
				{
					MetaDataImpl meta = new MetaDataImpl();
					JSONObject info = cacheInfoMap.get(s);
					if (info != null) meta.loadFromModInfo(info);
					Map<String, Object> anno = annotationMap.get(s);
					if (anno != null) meta.loadFromAnnotationMap(anno);
					releases.add(new Mod(meta));
				}
			}
			catch (IOException e)
			{
				throw new IllegalArgumentException(e);
			}
			return releases.toArray(new Mod[releases.size()]);
		};
	}

	private static Deserializer<Mod[], String> jsonParser()
	{
		return (serialized, context) ->
		{
			Map<String, JSONObject> cacheInfoMap = new HashMap<>();
			JSONArray arr = serialized.startsWith("{") ? new JSONObject(serialized).getJSONArray("modList") :
					new JSONArray(serialized);

			for (int i = 0; i < arr.length(); i++)
			{
				String modid = arr.getJSONObject(i).optString("modid");
				if (StringUtils.isNotEmpty(modid)) cacheInfoMap.put(modid, arr.getJSONObject(i));
			}
			Mod[] mods = new Mod[cacheInfoMap.size()];
			int i = 0;
			for (Map.Entry<String, JSONObject> entry : cacheInfoMap.entrySet())
			{
				MetaDataImpl metaData = new MetaDataImpl();
				metaData.loadFromModInfo(entry.getValue());
				mods[i++] = new Mod(metaData);
			}
			return mods;
		};
	}

	private ModParser(Deserializer<Mod[], Path> deserializer) {this.deserializer = deserializer;}
}
