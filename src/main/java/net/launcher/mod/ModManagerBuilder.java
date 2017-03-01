package net.launcher.mod;

import api.launcher.ModManager;
import net.launcher.LaunchElementManager;
import net.launcher.game.forge.ForgeMod;
import net.launcher.game.forge.ForgeModMetaData;
import net.launcher.game.forge.ForgeModMetadataBuilder;
import net.launcher.game.forge.ForgeModParser;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.LocalArchiveRepository;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.util.Builder;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ModManagerBuilder implements Builder<LaunchElementManager<ForgeMod>>
{
	public static ModManagerBuilder create(Path root)
	{
		Objects.requireNonNull(root);
		return new ModManagerBuilder(root);
	}

	private ModManagerBuilder(Path root)
	{
		this.root = root;
	}

	private Path root;
	private ArchiveRepository<ForgeMod[]> archiveRepository;

	public ModManagerBuilder setArchiveRepository(ArchiveRepository<ForgeMod[]> archiveRepository)
	{
		this.archiveRepository = archiveRepository;
		return this;
	}

	@Override
	public ModManager build()
	{
		return new ModManagerImpl(archiveRepository == null ? getArchiveRepository() : archiveRepository);
	}

	public ArchiveRepository<ForgeMod[]> getArchiveRepository()
	{
		if (archiveRepository == null)
		{
			BiSerializer<ForgeMod[], NBTCompound> combine = BiSerializer.combine((releases, context) ->
			{
				NBTCompound compound = NBT.compound();
				for (ForgeMod release : releases)
				{
					NBTCompound meta = NBT.compound();
					ForgeModMetaData metaData = release.getMetaData();
					meta.put("modid", metaData.getModId())
							.put("version", metaData.getVersion())
							.put("name", (metaData.getName()))
							.put("logo", (metaData.getLogoFile()))
							.put("description", (metaData.getDescription()))
							.put("updateJSON", (metaData.getUpdateJSON()))
							.put("acceptableRemoteVersions", metaData.acceptableRemoteVersions())
							.put("acceptableSaveVersions", metaData.acceptableSaveVersions())
							.put("acceptableMinecraftVersion", metaData.getAcceptMinecraftVersion())
							.put("credits", metaData.getCredits())
							.put("authorList", NBT.listStr(metaData.getAuthorList()))
							.put("url", metaData.getUrl())
							.put("parent", metaData.getParent())
							.put("screenShots", NBT.listStr(metaData.getScreenshots()))
							.put("mcVersion", metaData.getAcceptMinecraftVersion())
							.put("fingerprint", metaData.getFingerprint())
							.put("dependencies", metaData.getDependencies());
					compound.put(release.getModId(), meta);
				}
				return compound;
			}, (nbt, context) ->
			{
				List<String> strings = nbt.keySet().stream().collect(Collectors.toList());
				ForgeMod[] mods = new ForgeMod[strings.size()];
				for (int i = 0; i < strings.size(); i++)
				{
					NBTCompound modObj = nbt.get(strings.get(i)).asCompound();
					ForgeModMetadataBuilder builder = ForgeModMetadataBuilder.create();
					builder.setModId(modObj.get("modid").asString());
					builder.setVersion(modObj.get("version").asString());
					builder.setName(modObj.get("name").asString());
					builder.setLogoFile(modObj.get("logo").asString());
					builder.setDescription(modObj.get("description").asString());
					builder.setUpdateJSON(modObj.get("updateJSON").asString());
					builder.setAcceptableRemoteVersions(modObj.get("acceptableRemoteVersions").asString());
					builder.setAcceptableSaveVersions(modObj.get("acceptableSaveVersions").asString());
					builder.setAcceptableMinecraftVersion(modObj.get("acceptableMinecraftVersion").asString());
					builder.setCredits(modObj.get("credits").asString());
					builder.setAuthorList(Arrays.stream(modObj.get("authorList").asList().toArray()).map
							(Object::toString).toArray(String[]::new));
					builder.setUrl(modObj.get("url").asString());
					builder.setParent(modObj.get("parent").asString());
					builder.setScreenshots(Arrays.stream(modObj.get("screenShots").asList().toArray()).map
							(Object::toString).toArray(String[]::new));
					builder.setMcVersion(modObj.get("mcVersion").asString());
					builder.setFingerprint(modObj.get("fingerprint").asString());
					builder.setDependencies(modObj.get("dependencies").asString());
					ForgeMod release = new ForgeMod(builder.build());
					mods[i] = release;
				}
				return mods;
			});
			return archiveRepository = new LocalArchiveRepository<>(root, ForgeModParser
					.defaultModDeserializer(), combine);
		}
		return archiveRepository;
	}
}
