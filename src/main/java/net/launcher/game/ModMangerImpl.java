package net.launcher.game;

import javafx.collections.MapChangeListener;
import jdk.internal.org.objectweb.asm.ClassReader;
import net.launcher.game.mod.*;
import net.launcher.utils.Patterns;
import net.launcher.utils.StringUtils;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.ArchiveResource;
import net.launcher.utils.resource.ResourceType;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ci010
 */
class ModMangerImpl implements ModManager
{
	private ArchiveRepository<Mod.Release[]> archiveResource;
	private Map<String, ModImpl> modIdToMod = new HashMap<>();
	private Map<String, Mod> view = Collections.unmodifiableMap(modIdToMod);

	ModMangerImpl(File root)
	{
		Function<File, Mod.Release[]> zipJar = (file) ->
		{
			List<Mod.Release> releases = new ArrayList<>();
			try
			{
				ZipFile zip = new ZipFile(file);
				Map<String, JSONObject> cacheInfoMap = new HashMap<>();
				final Map<String, Map<String, Object>> annotationMap = new HashMap<>();
				ZipEntry modInfo = zip.getEntry("mcmod.info");
				if (modInfo != null)
				{
					String modInfoString = IOUtils.toString(zip.getInputStream(modInfo));

					JSONArray arr;
					if (modInfoString.startsWith("{")) arr = new JSONObject(modInfoString).getJSONArray("modList");
					else arr = new JSONArray(modInfoString);

					for (int i = 0; i < arr.length(); i++)
					{
						String modid = arr.getJSONObject(i).optString("modid");
						if (StringUtils.isNotEmpty(modid)) cacheInfoMap.put(modid, arr.getJSONObject(i));
					}
				}

				Set<Map<String, Object>> set = new HashSet<>();
				for (ZipEntry jarEntry : Collections.list(zip.entries()))
					if (Patterns.CLASS_FILE.matcher(jarEntry.getName()).matches())
					{
						set.clear();
						ClassReader reader = new ClassReader(zip.getInputStream(jarEntry));
						reader.accept(new ModAnnotationVisitor(set), 0);
						if (!set.isEmpty())
						{
							for (Map<String, Object> stringObjectMap : set)
							{
								String modid = stringObjectMap.get("modid").toString();
								if (StringUtils.isNotEmpty(modid)) annotationMap.put(modid, stringObjectMap);
							}
						}
					}
				Set<String> union = new HashSet<>(cacheInfoMap.keySet());
				union.addAll(annotationMap.keySet());
				for (String s : union)
				{
					MetaDataImpl meta = new MetaDataImpl();
					JSONObject info = cacheInfoMap.get(s);
					if (info != null) meta.loadFromModInfo(info);
					Map<String, Object> anno = annotationMap.get(s);
					if (anno != null) meta.loadFromAnnotationMap(anno);
					releases.add(new ReleaseImpl(meta));
				}
			}
			catch (Exception ignored)
			{
				Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ignored);
			}
			return releases.toArray(new Mod.Release[releases.size()]);
		};
		archiveResource = new ArchiveRepository.Builder<Mod.Release[]>(
				root, "mods",
				BiSerializer.combine((releases, context) ->
				{
					JSONObject jsonObject = new JSONObject();
					for (Mod.Release release : releases)
					{
						jsonObject.put("modId", release.getModId())
								.put("version", (release.getVersion()))
								.put("nickName", (release.getNickName()));
						JSONObject meta = new JSONObject();
						jsonObject.put("meta", meta);
						ModMetaData metaData = release.getMetaData();
						meta.put("name", (metaData.getName()))
								.put("logo", (metaData.getLogoFile()))
								.put("description", (metaData.getDescription()))
								.put("updateJSON", (metaData.getUpdateJSON()))
								.put("acceptableRemoteVersions", metaData.acceptableRemoteVersions())
								.put("acceptableSaveVersions", metaData.acceptableSaveVersions())
								.put("credits", metaData.getCredits())
								.put("authorList", JSONObject.valueToString(metaData.getAuthorList()))
								.put("url", metaData.getUrl())
								.put("parent", metaData.getParent())
								.put("screenShots", JSONObject.valueToString(metaData.getScreenshots()))
								.put("mcVersion", metaData.getAcceptMinecraftVersion())
								.put("fingerprint", metaData.getFingerprint())
								.put("dependencies", metaData.getDependencies());
					}
					return jsonObject;
				}, (json, context) ->
				{
					String modId = json.getString("modId");
					return null;
				}))
				.registerParser(ResourceType.JAR, zipJar)
				.registerParser(ResourceType.ZIP, zipJar)
				.build();
		archiveResource.getAllStorage().addListener(new MapChangeListener<String, ArchiveResource<Mod.Release[]>>()
		{
			@Override
			public void onChanged(Change<? extends String, ? extends ArchiveResource<Mod.Release[]>> change)
			{
				for (Mod.Release modMeta : change.getValueAdded().getContainData())
					if (modIdToMod.containsKey(modMeta.getModId()))
						modIdToMod.get(modMeta.getModId()).register(modMeta);
					else
					{
						ModImpl entry = new ModImpl(modMeta.getModId());
						entry.register(modMeta);
						modIdToMod.put(modMeta.getModId(), entry);
					}
			}
		});
	}

	@Override
	public Set<String> getAllModId()
	{
		return modIdToMod.keySet();
	}

	@Override
	public Optional<Mod> getMod(String modid)
	{
		return Optional.ofNullable(modIdToMod.get(modid));
	}

	@Override
	public Future<ResourcePack> importMod(File mod, Callback<ResourcePack> callback)
	{
		try
		{
			Mod.Release[] releases = archiveResource.importFile(mod);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Mod> getAllMods()
	{
		return view;
	}
}
