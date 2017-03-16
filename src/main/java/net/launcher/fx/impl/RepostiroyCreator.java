package net.launcher.fx.impl;

import api.launcher.event.ModStorageRegisterEvent;
import net.launcher.game.ResourcePack;
import net.launcher.game.mods.AbstractModParser;
import net.launcher.game.mods.ModContainer;
import net.launcher.game.mods.ModTypes;
import net.launcher.game.mods.forge.ForgeMod;
import net.launcher.game.mods.forge.ForgeModMetaData;
import net.launcher.game.mods.forge.ForgeModMetadataBuilder;
import net.launcher.game.mods.forge.ForgeModParser;
import net.launcher.game.mods.liteloader.LiteModMetaData;
import net.launcher.game.mods.liteloader.LiteModParser;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.game.nbt.NBTList;
import net.launcher.game.text.components.TextComponentString;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.LocalArchiveRepository;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class RepostiroyCreator
{
	static ArchiveRepository<ModContainer<?>[]> modContainer(Path root)
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
//		ARML.bus().postEvent(event);
		Map<String, BiSerializer<ModContainer<?>, NBTCompound>> nbtSerial = event.getNbtSerial();
		List<AbstractModParser> parsers = event.getParsers();

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

		return new LocalArchiveRepository<>(root.resolve("mods"), (path, context) ->
		{
			for (AbstractModParser par : parsers)
				try
				{
					path = par.validate(path);
					if (path != null) return par.deserialize(path, context);
				}
				catch (Exception ignored) {}
			return null;
		}, combine);
	}

	static ArchiveRepository<ForgeMod[]> mod(Path root)
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
		return new LocalArchiveRepository<>(root.resolve("mods"), ForgeModParser
				.defaultModDeserializer(), combine);
	}

	static ArchiveRepository<ResourcePack> resourcePack(Path root)
	{
		return new LocalArchiveRepository<>(root.resolve("resourcepacks"),
				(file, context) ->
				{
					String raw = context.get("fileName").toString();
					String name = raw.substring(0, raw.lastIndexOf('.')), descriptor = "";
					int format = -1;
					Path resolve = file.resolve("pack.mcmeta");
					if (Files.exists(resolve))
						try
						{
							JSONObject metaObj = new JSONObject(NIOUtils.readToString(resolve));
							JSONObject pack = metaObj.optJSONObject("pack");
							if (pack != null)
							{
								format = pack.optInt("pack_format", -1);
								descriptor = pack.optString("description", "");
							}
						}
						catch (IOException e)
						{
							Consumer<Throwable> exceptionHandler = (Consumer<Throwable>) context.get("exceptionHandler");
							exceptionHandler.accept(e);
						}
					else
					{
						resolve = file.resolve("pack.txt");
						try
						{
							descriptor = NIOUtils.readToString(resolve);
						}
						catch (IOException e)
						{
							Consumer<Throwable> exceptionHandler = (Consumer<Throwable>) context.get("exceptionHandler");
							exceptionHandler.accept(e);
						}
					}
					return new ResourcePack(name, TextComponentString.convert(descriptor), format);
				},
				BiSerializer.combine(
						(data, context) ->
								NBT.compound().put("name", data.getPackName()).put("description",
										data.getDescription().getFormattedText()).put("format", data.packFormat()),
						(serialized, context) -> new ResourcePack(serialized.get("name").asString(),
								TextComponentString.convert(serialized.get("description").asString()), serialized.get
								("format").asInt()))
		);
	}
}
