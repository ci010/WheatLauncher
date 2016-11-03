package net.launcher.game.mod;

import jdk.internal.org.objectweb.asm.ClassReader;
import net.launcher.LaunchElementManager;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.io.NIOUtils;
import net.launcher.utils.Patterns;
import net.launcher.utils.StringUtils;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repositories;
import net.launcher.utils.serial.BiSerializer;
import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.Builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ModManagerBuilder implements Builder<LaunchElementManager<Mod>>
{
	public static ModManagerBuilder create(Path root, ExecutorService service)
	{
		Objects.requireNonNull(root);
		Objects.requireNonNull(service);
		return new ModManagerBuilder(root, service);
	}

	private ModManagerBuilder(Path root, ExecutorService service)
	{
		this.root = root;
		this.service = service;
	}

	private Path root;
	private ExecutorService service;
	private ArchiveRepository<Mod[]> archiveRepository;

	public ModManagerBuilder setArchiveRepository(ArchiveRepository<Mod[]> archiveRepository)
	{
		this.archiveRepository = archiveRepository;
		return this;
	}

	@Override
	public LaunchElementManager<Mod> build()
	{
		return new ModMangerImpl(archiveRepository == null ? getArchiveRepository() : archiveRepository);
	}

	public ArchiveRepository<Mod[]> getArchiveRepository()
	{
		if (archiveRepository == null)
		{
			Deserializer<Mod[], Path> parser = (path, context) ->
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
						releases.add(new ReleaseImpl(meta));
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				return releases.toArray(new Mod[releases.size()]);
			};
			BiSerializer<Mod[], NBTCompound> combine = BiSerializer.combine((releases, context) ->
			{
				NBTCompound compound = NBT.compound();
				for (Mod release : releases)
				{
					NBTCompound meta = NBT.compound();
					ModMetaData metaData = release.getMetaData();
					meta.put("modid", metaData.getModId())
							.put("version", metaData.getVersion())
							.put("name", (metaData.getName()))
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
					compound.put(release.getModId(), meta);
				}
				return compound;
			}, (nbt, context) ->
			{
				List<String> strings = nbt.keySet().stream().collect(Collectors.toList());
				Mod[] releases = new Mod[strings.size()];
				for (int i = 0; i < strings.size(); i++)
				{
					NBTCompound modObj = nbt.get(strings.get(i)).asCompound();
					MetaDataImpl metaData = new MetaDataImpl();
					JSONObject object = new JSONObject(modObj.asRaw());
					metaData.loadFromModInfo(object);
					metaData.loadFromAnnotationMap(object.toMap());
					ReleaseImpl release = new ReleaseImpl(metaData);
					releases[i] = release;
				}
				return releases;
			});
			return archiveRepository = Repositories.newArchiveRepositoryBuilder(root, service, combine, parser).build();
		}
		return archiveRepository;
	}
}
