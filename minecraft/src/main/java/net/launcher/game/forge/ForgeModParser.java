package net.launcher.game.forge;

import jdk.internal.org.objectweb.asm.ClassReader;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.Patterns;
import net.launcher.utils.StringUtils;
import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * @author ci010
 */

public class ForgeModParser
{
	public static ForgeModParser create(Deserializer<ForgeMod[], Path> deserializer)
	{
		Objects.requireNonNull(deserializer);
		return new ForgeModParser(deserializer);
	}

	public static ForgeModParser create() {return new ForgeModParser(defaultModDeserializer());}

	public ForgeMod[] parseFile(Path path)
	{
		Objects.requireNonNull(path);
		if (Files.exists(path))
			return deserializer.deserialize(path);
		else return new ForgeMod[0];
	}

	public ForgeMod[] parseFile(Path path, Consumer<Throwable> exceptionHandler)
	{
		Objects.requireNonNull(path);
		if (Files.exists(path))
		{
			if (Patterns.ZIP_JAR.matcher(path.getFileName().toString()).matches())
			{
				try
				{
					FileSystem fileSystem = FileSystems.newFileSystem(path, this.getClass().getClassLoader());
					path = fileSystem.getPath("/");
				}
				catch (IOException e)
				{
					exceptionHandler.accept(e);
				}
			}
			return deserializer.deserializeWithException(path, exceptionHandler);

		}
		else return new ForgeMod[0];
	}

	public ForgeMod[] parseJson(String json)
	{
		Objects.requireNonNull(json);
		return jsonParser().deserialize(json);
	}

	private Deserializer<ForgeMod[], Path> deserializer;

	public static Deserializer<ForgeMod[], Path> defaultModDeserializer()
	{
		return (path, context) ->
		{
			List<ForgeMod> releases = new ArrayList<>();
			Path modInf = path.resolve("/mcmod.info");
			Map<String, JSONObject> cacheInfoMap = new HashMap<>();
			final Map<String, Map<String, Object>> annotationMap = new HashMap<>();
			if (Files.exists(modInf))
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
						releases.add(new ForgeMod(meta));
					}
				}
				catch (IOException e)
				{
					throw new IllegalArgumentException(e);
				}
			else
			{
				Path manifestPath = path.resolve("META-INF").resolve("MANIFEST.MF");
				try
				{
					Manifest manifest = new Manifest(Files.newInputStream(manifestPath));
					Attributes fmlCorePlugin = manifest.getAttributes("FMLCorePlugin");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return releases.toArray(new ForgeMod[releases.size()]);
		};
	}

	private static Deserializer<ForgeMod[], String> jsonParser()
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
			ForgeMod[] mods = new ForgeMod[cacheInfoMap.size()];
			int i = 0;
			for (Map.Entry<String, JSONObject> entry : cacheInfoMap.entrySet())
			{
				MetaDataImpl metaData = new MetaDataImpl();
				metaData.loadFromModInfo(entry.getValue());
				mods[i++] = new ForgeMod(metaData);
			}
			return mods;
		};
	}

	private ForgeModParser(Deserializer<ForgeMod[], Path> deserializer) {this.deserializer = deserializer;}
}
