package net.launcher.mod;

import api.launcher.ARML;
import api.launcher.event.ModStorageRegisterEvent;
import api.launcher.io.IOGuard;
import net.launcher.game.mods.AbstractModParser;
import net.launcher.game.mods.ModContainer;
import net.launcher.game.mods.ModTypes;
import net.launcher.game.mods.forge.ForgeModMetaData;
import net.launcher.game.mods.forge.ForgeModMetadataBuilder;
import net.launcher.game.mods.forge.ForgeModParser;
import net.launcher.game.mods.liteloader.LiteModMetaData;
import net.launcher.game.mods.liteloader.LiteModParser;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.game.nbt.NBTList;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.LocalArchiveRepository;
import net.launcher.utils.serial.BiSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class IOGuardGenericModManager extends IOGuard<GeneircModManager>
{
	private Map<String, BiSerializer<ModContainer<?>, NBTCompound>> nbtSerial;
	private List<AbstractModParser> parsers;

	@Override
	protected void onInit()
	{
		ForgeModParser parser = new ForgeModParser();
		ModStorageRegisterEvent event = new ModStorageRegisterEvent();
		event.register(ModTypes.LITE_LOADER, BiSerializer.combine((releases, context) ->
				{
					NBTCompound compound = NBT.compound();
					LiteModMetaData metaData = (LiteModMetaData) releases.getMetaData();
					compound.put("name", metaData.getName());
					compound.put("version", metaData.getVersion());
					compound.put("author", metaData.getAuthor());
					compound.put("description", metaData.getDescription());
					compound.put("mcVersion", metaData.getMcVersion());
					compound.put("revision", metaData.getRevision());
					compound.put("tweakClass", metaData.getTweakClass());
					compound.put("url", metaData.getUrl());
					compound.put("transformers", NBT.listStr(metaData.getClassTransformerClasses()));
					compound.put("dependOn", NBT.listStr(metaData.getDependsOn()));
					compound.put("requiredAPIs", NBT.listStr(metaData.getRequiredAPIs()));
					return compound;
				}, (nbt, context) ->
				{
					LiteModMetaData.Builder builder = LiteModMetaData.builder();
					builder.setName(nbt.get("name").asString())
							.setAuthor(nbt.getOption("author").map(NBT::asString).orElse(""))
							.setDescription(nbt.getOption("description").map(NBT::asString).orElse(""))
							.setVersion(nbt.getOption("version").map(NBT::asString).orElse(""))
							.setMcVersion(nbt.getOption("mcVersion").map(NBT::asString).orElse(""))
							.setUrl(nbt.getOption("url").map(NBT::asString).orElse(""))
							.setTweakClass(nbt.getOption("tweakClass").map(NBT::asString).orElse(""))
							.setClassTransformerClasses(nbt.getOption("tweakClass").map(NBT::asList).map(lst -> lst
									.stream().map(NBT::asString).toArray(String[]::new)).get())
							.setRequiredAPIs(nbt.getOption("requiredAPIs").map(NBT::asList).map(lst -> lst
									.stream().map(NBT::asString).toArray(String[]::new)).get())
							.setDependsOn(nbt.getOption("dependOn").map(NBT::asList).map(lst -> lst
									.stream().map(NBT::asString).toArray(String[]::new)).get())
							.setRevision(nbt.getOption("revision").map(NBT::asInt).orElse(0));

					return LiteModMetaData.createMod(builder.build());
				}
		), new LiteModParser());
		event.register(ModTypes.FORGE, BiSerializer.combine((releases, context) ->
		{
			NBTCompound compound = NBT.compound();
			ForgeModMetaData metaData = (ForgeModMetaData) releases.getMetaData();
			compound.put("modid", metaData.getModId())
					.put("version", metaData.getVersion())
					.put("name", metaData.getName())
					.put("logo", metaData.getLogoFile())
					.put("description", metaData.getDescription())
					.put("updateJSON", metaData.getUpdateJSON())
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
			return compound;
		}, (modObj, context) ->
		{
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
			return ForgeModMetaData.createMod(builder.build(),
					parser::parseVersionRange);
		}), parser);
		ARML.bus().postEvent(event);
		this.nbtSerial = event.getNbtSerial();
		this.parsers = event.getParsers();
	}

	@Override
	protected void forceSave() throws IOException {}

	@Override
	public GeneircModManager loadInstance() throws IOException
	{
		return new GenericModManagerImpl(createRepo());
	}

	@Override
	public GeneircModManager defaultInstance()
	{
		return new GenericModManagerImpl(createRepo());
	}

	@Override
	protected void deploy() throws IOException
	{

	}

	private ArchiveRepository<ModContainer<?>[]> createRepo()
	{
		BiSerializer<ModContainer<?>[], NBTCompound> combine = BiSerializer.combine((releases, context) ->
		{
			NBTCompound compound = NBT.compound();
			for (ModContainer<?> release : releases)
			{
				BiSerializer<ModContainer<?>, NBTCompound> serializer = nbtSerial.get(release.getModType());
				NBT nbt = compound.get(release.getModType());
				if (nbt.isNull())
					compound.put(release.getModType(), nbt = NBT.list());
				nbt.asList().add(serializer.serialize(release));
			}
			return compound;
		}, (nbt, context) ->
		{
			List<String> strings = nbt.keySet().stream().collect(Collectors.toList());
			List<ModContainer<?>> containers = new ArrayList<>();
			for (String type : strings)
			{
				BiSerializer<ModContainer<?>, NBTCompound> serializer = nbtSerial.get(type);
				NBTList nbts = nbt.get(type).asList();
				nbts.stream().map(NBT::asCompound).map(serializer::deserialize).forEach(containers::add);
			}
			return containers.toArray(new ModContainer<?>[containers.size()]);
		});

		return new LocalArchiveRepository<>(getContext().getRoot(), (path, context) ->
		{
			for (AbstractModParser parser : parsers)
				try
				{
					path = parser.validate(path);
					if (path != null) return parser.deserialize(path, context);
				}
				catch (Exception ignored) {}
			return null;
		}, combine);
	}
}
