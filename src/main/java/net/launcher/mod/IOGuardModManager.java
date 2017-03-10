package net.launcher.mod;

import api.launcher.ARML;
import api.launcher.ModManager;
import api.launcher.event.LaunchEvent;
import api.launcher.io.IOGuard;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.launcher.assets.MinecraftVersion;
import net.launcher.game.mods.forge.ForgeMod;
import net.launcher.game.mods.forge.ForgeModMetaData;
import net.launcher.game.mods.forge.ForgeModMetadataBuilder;
import net.launcher.game.mods.forge.ForgeModParser;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Delivery;
import net.launcher.utils.resource.FetchOption;
import net.launcher.utils.resource.LocalArchiveRepository;
import net.launcher.utils.serial.BiSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class IOGuardModManager extends IOGuard<ModManager>
{
	private ArchiveRepository<ForgeMod[]> archiveRepository;

	@Override
	protected void onInit()
	{
		super.onInit();
		archiveRepository = getArchiveRepository();
	}

	@Override
	protected void forceSave() throws IOException
	{}

	@Override
	public ModManager loadInstance() throws IOException
	{
		Path mods = getContext().getRoot().resolve("mods");
		Files.createDirectories(mods);
		return new ModManagerImpl(archiveRepository);
	}

	@Override
	public ModManager defaultInstance() {return new ModManagerImpl(archiveRepository);}

	@Override
	protected void deploy() throws IOException
	{
		ARML.bus().addEventHandler(LaunchEvent.PRE_LAUNCH, event ->
		{
			ARML.logger().info("Start to handle the mods handling");
			Path target = event.getOption().getRuntimeDirectory().getRoot().toPath().resolve("mods");
			ARML.taskCenter().runSimpleTask("PrepareMods", () ->
					NIOUtils.clearDirectory(Files.createDirectories(target)));

			MinecraftVersion mcVersion = event.getProfile().getMcVersion();
			ModManagerImpl instance = (ModManagerImpl) getInstance();
			ObservableList<ForgeMod> mods = instance.getIncludeElementContainer(event.getProfile());
			mods.stream().map(mod -> instance.hashMap.get(instance.getModKey(mod))).forEach(
					s ->
					{
						Task<Delivery<ForgeMod[]>> task = archiveRepository.fetchResource(
								target,
								s, FetchOption.HARD_LINK);
						ARML.taskCenter().listenTask(task);
						task.run();
					}
			);
		});
		getInstance().update().run();
	}

	private ArchiveRepository<ForgeMod[]> getArchiveRepository()
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
						.put("mcVersion", metaData.getMcVersion())
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
		return new LocalArchiveRepository<>(getContext().getRoot().resolve("mods"), ForgeModParser
				.defaultModDeserializer(), combine);
	}
}
