package net.launcher.mod;

import net.launcher.LaunchElementManager;
import net.launcher.game.mod.Mod;
import net.launcher.game.mod.ModMetaData;
import net.launcher.game.mod.ModMetadataBuilder;
import net.launcher.game.mod.ModParser;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repositories;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.util.Builder;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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
							.put("authorList", NBT.list(metaData.getAuthorList()))
							.put("url", metaData.getUrl())
							.put("parent", metaData.getParent())
							.put("screenShots", NBT.list(metaData.getScreenshots()))
							.put("mcVersion", metaData.getAcceptMinecraftVersion())
							.put("fingerprint", metaData.getFingerprint())
							.put("dependencies", metaData.getDependencies());
					compound.put(release.getModId(), meta);
				}
				return compound;
			}, (nbt, context) ->
			{
				List<String> strings = nbt.keySet().stream().collect(Collectors.toList());
				Mod[] mods = new Mod[strings.size()];
				for (int i = 0; i < strings.size(); i++)
				{
					NBTCompound modObj = nbt.get(strings.get(i)).asCompound();
					ModMetadataBuilder builder = ModMetadataBuilder.create();
					builder.setModId(modObj.get("modid").asString());
					builder.setVersion(modObj.get("version").asString());
					builder.setName(modObj.get("name").asString());
					builder.setLogoFile(modObj.get("logo").asString());
					builder.setDescription(modObj.get("description").asString());
					builder.setUpdateJSON(modObj.get("updateJSON").asString());
					builder.setAcceptableRemoteVersions(modObj.get("acceptableRemoteVersions").asString());
					builder.setAcceptableSaveVersions(modObj.get("acceptableSaveVersions").asString());
					builder.setCredits(modObj.get("credits").asString());
					builder.setAuthorList((String[]) modObj.get("authorList").asList().toArray());
					builder.setUrl(modObj.get("url").asString());
					builder.setParent(modObj.get("parent").asString());
					builder.setScreenshots((String[]) modObj.get("screenShots").asList().toArray());
					builder.setMcVersion(modObj.get("mcVersion").asString());
					builder.setFingerprint(modObj.get("fingerprint").asString());
					builder.setDependencies(modObj.get("dependencies").asString());
					Mod release = new Mod(builder.build());
					mods[i] = release;
				}
				return mods;
			});
			return archiveRepository = Repositories.newArchiveRepositoryBuilder(root, service, combine, ModParser.defaultModDeserializer())
					.build();
		}
		return archiveRepository;
	}
}
