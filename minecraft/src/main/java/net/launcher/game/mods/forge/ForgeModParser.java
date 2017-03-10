package net.launcher.game.mods.forge;

import net.launcher.game.mods.AbstractModParser;
import net.launcher.game.mods.ModContainer;
import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.VersionParser;
import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.VersionRange;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.Patterns;
import net.launcher.utils.StringUtils;
import net.launcher.utils.Tasks;
import net.launcher.utils.serial.Deserializer;
import org.objectweb.asm.ClassReader;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ci010
 */

public class ForgeModParser extends AbstractModParser
{
	private Function<String, String> versionTranslation;

	public ForgeModParser(Function<String, String> versionTranslation)
	{
		this.versionTranslation = versionTranslation;
	}

	public ForgeModParser()
	{
		this.versionTranslation = defaultTranslation();
	}

	public static Deserializer<ForgeMod[], Path> defaultModDeserializer()
	{
		return (path, context) ->
		{
			Map<String, JSONObject> cacheInfoMap = new HashMap<>();
			Map<String, Map<String, Object>> annotationMap = new HashMap<>();
			try
			{
				Set<Map<String, Object>> set = new HashSet<>();
				List<Path> list = Files.walk(path).filter(pa ->
						pa.getFileName() != null && Patterns.CLASS_FILE.matcher(pa.getFileName().toString()).matches())
						.collect(Collectors.toList());
				for (Path p : list)
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
			}
			catch (Exception e0)
			{
				e0.printStackTrace();
			}
			Path modInf = path.resolve("/mcmod.info");
			if (Files.exists(modInf))
				try
				{
					String modInfoString = NIOUtils.readToString(modInf);
					modInfoString = modInfoString.replace("\n", "");
					JSONArray arr = modInfoString.startsWith("{") ?
							new JSONObject(modInfoString).getJSONArray("modList") :
							new JSONArray(modInfoString);

					for (int i = 0; i < arr.length(); i++)
					{
						String modid = arr.getJSONObject(i).optString("modid");
						if (StringUtils.isNotEmpty(modid)) cacheInfoMap.put(modid, arr.getJSONObject(i));
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			List<ForgeMod> releases = new ArrayList<>();
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
			return releases.toArray(new ForgeMod[releases.size()]);
		};
	}

	public static Function<String, String> defaultTranslation()
	{
		return mcVersionString ->
		{
			if ("[1.8.8]".equals(mcVersionString))
				mcVersionString = "[1.8.8,1.8.9]"; // MC 1.8.8 and 1.8.9 is forward SRG compatible so accept these versions by default.
			if ("[1.9.4]".equals(mcVersionString) ||
					"[1.9,1.9.4]".equals(mcVersionString) ||
					"[1.9.4,1.10)".equals(mcVersionString) ||
					"[1.10]".equals(mcVersionString))
				mcVersionString = "[1.9.4,1.10.2]";
			return mcVersionString;
		};
	}

	@Override
	public ModContainer<?>[] deserialize(Path path, Map<Object, Object> context) throws Exception
	{
		Map<String, JSONObject> cacheInfoMap = new HashMap<>();
		Map<String, Map<String, Object>> annotationMap = new HashMap<>();
		try
		{
			Set<Map<String, Object>> set = new HashSet<>();
			List<Path> list = Files.walk(path).filter(pa ->
					pa.getFileName() != null && Patterns.CLASS_FILE.matcher(pa.getFileName().toString()).matches())
					.collect(Collectors.toList());
			for (Path p : list)
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
		}
		catch (Exception e0)
		{
			e0.printStackTrace();
		}
		Path modInf = path.resolve("/mcmod.info");
		if (Files.exists(modInf))
			try
			{
				String modInfoString = NIOUtils.readToString(modInf);
				modInfoString = modInfoString.replace("\n", "");
				JSONArray arr = modInfoString.startsWith("{") ?
						new JSONObject(modInfoString).getJSONArray("modList") :
						new JSONArray(modInfoString);

				for (int i = 0; i < arr.length(); i++)
				{
					String modid = arr.getJSONObject(i).optString("modid");
					if (StringUtils.isNotEmpty(modid)) cacheInfoMap.put(modid, arr.getJSONObject(i));
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		else
		{
			//TODO maybe parse coremod
//				Path manifestPath = path.resolve("META-INF").resolve("MANIFEST.MF");
//				try
//				{
//					Manifest manifest = new Manifest(Files.newInputStream(manifestPath));
//					Attributes fmlCorePlugin = manifest.getAttributes("FMLCorePlugin");
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//				}
		}
		List<ModContainer<ForgeModMetaData>> releases = new ArrayList<>();
		Set<String> union = new HashSet<>(cacheInfoMap.keySet());
		union.addAll(annotationMap.keySet());
		for (String s : union)
		{
			MetaDataImpl meta = new MetaDataImpl();
			JSONObject info = cacheInfoMap.get(s);
			if (info != null) meta.loadFromModInfo(info);
			Map<String, Object> anno = annotationMap.get(s);
			if (anno != null) meta.loadFromAnnotationMap(anno);

			releases.add(ForgeModMetaData.createMod(meta, this::parseVersionRange));
		}
		return releases.toArray(new ModContainer[releases.size()]);
	}

	public VersionRange parseVersionRange(String version)
	{
		return Tasks.optional(() -> VersionParser.parseRange(versionTranslation.apply(version))).orElse(null);
	}
}
