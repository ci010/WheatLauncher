package net.launcher.game;

import javafx.collections.MapChangeListener;
import jdk.internal.org.objectweb.asm.ClassReader;
import net.launcher.game.mod.*;
import net.launcher.utils.Patterns;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.ArchiveResource;
import net.launcher.utils.resource.ResourceType;
import net.launcher.utils.resource.StoragePath;
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
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ci010
 */
class ModMangerImpl implements ModManger
{
	private ArchiveRepository<Mod.Release[]> archiveResource;
	private Map<String, ModImpl> modIdToMod = new HashMap<>();
	private Map<String, Mod> view = Collections.unmodifiableMap(modIdToMod);

	ModMangerImpl(File root)
	{
		Function<File, Mod.Release[]> zipJar = (file) ->
		{
			List<Mod.Release> releases = new ArrayList<>();

			ZipFile zip = null;
			try
			{
				zip = new ZipFile(file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				ModInfo empty = ModInfo.empty();
				RuntimeAnnotation emptyAnno = RuntimeAnnotation.empty();
				Map<String, ModInfo> cacheInfoMap = new HashMap<>();
				final Map<String, RuntimeAnnotation> annotationMap = new HashMap<>();
				ZipEntry modInfo = zip.getEntry("mcmod.info");
				if (modInfo != null)
				{
					String modInfoString = IOUtils.toString(zip.getInputStream(modInfo));

					JSONArray arr;
					if (modInfoString.startsWith("{"))
						arr = new JSONObject(modInfoString).getJSONArray("modList");
					else
						arr = new JSONArray(modInfoString);

					for (int i = 0; i < arr.length(); i++)
					{
						ModInfo info = new ModInfo(arr.getJSONObject(i));
						cacheInfoMap.put(info.getModId(), info);
					}
				}

				Set<Map<String, Object>> set = new HashSet<>();
				for (ZipEntry jarEntry : Collections.list(zip.entries()))
					if (Patterns.CLASS_FILE.matcher(jarEntry.getName()).matches())
					{
						set.clear();
						ClassReader reader = new ClassReader(zip.getInputStream(jarEntry));
						reader.accept(new RuntimeAnnotation.Visitor(set), 0);
						if (!set.isEmpty())
							set.stream().map(RuntimeAnnotation::new).forEach(annotation -> annotationMap.put(annotation.getModId(), annotation));
					}
				Set<String> union = cacheInfoMap.keySet();
				union.addAll(annotationMap.keySet());
				for (String s : union)
				{
					ModInfo info = cacheInfoMap.get(s);
					RuntimeAnnotation anno = annotationMap.get(s);
					releases.add(new ReleaseImpl(s, new ModMetaDataImpl(info == null ? empty : info,
							anno == null ? emptyAnno : anno)));
				}
			}
			catch (Exception ignored) {}
			return releases.toArray(new Mod.Release[releases.size()]);
		};
		archiveResource = new ArchiveRepository.Builder<Mod.Release[]>(
				root, "mods",
				BiSerializer.combine((s, m) -> null, (s, m) -> null))
				.registerParser(ResourceType.JAR, zipJar)
				.registerParser(ResourceType.ZIP, zipJar)
				.setStorageHandler((resource, jsonData) ->
						Arrays.stream(resource.getContainData()).map(release ->
								new StoragePath(release.getVersion() + "/" + release.getModId(),
										jsonData.getJSONObject(release.getModId())))
								.collect(Collectors.toList())
								.toArray(new StoragePath[resource.getContainData().length]))
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
