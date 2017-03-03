package net.launcher.resourcepack;

import api.launcher.ARML;
import api.launcher.ResourcePackManager;
import api.launcher.event.LaunchEvent;
import api.launcher.io.IOGuard;
import javafx.concurrent.Task;
import net.launcher.game.ResourcePack;
import net.launcher.game.nbt.NBT;
import net.launcher.game.text.components.TextComponentString;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Delivery;
import net.launcher.utils.resource.FetchOption;
import net.launcher.utils.resource.LocalArchiveRepository;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class IOGuardResourcePackManager extends IOGuard<ResourcePackManager>
{
	private ArchiveRepository<ResourcePack> archiveRepository;

	@Override
	protected void forceSave() throws IOException {}

	@Override
	public ResourcePackManager loadInstance() throws IOException
	{
		Files.createDirectories(getContext().getRoot().resolve("resourcepacks"));
		return new ResourcePackManImpl(getArchiveRepository());
	}

	@Override
	public ResourcePackManager defaultInstance()
	{
		return new ResourcePackManImpl(getArchiveRepository());
	}

	@Override
	protected void deploy() throws IOException
	{
		ARML.bus().addEventHandler(LaunchEvent.LAUNCH_EVENT, event ->
		{
			ARML.taskCenter().runSimpleTask("CleaOldResource", () -> NIOUtils.clearDirectory(event.getOption()
					.getRuntimeDirectory()
					.getRoot().toPath().resolve("resourcepacks")));

			Task<Delivery<ResourcePack>> resourcepacks = archiveRepository.fetchAllResource(
					event.getOption().getRuntimeDirectory().getRoot().toPath().resolve
							("resourcepacks"), FetchOption.SYMBOL_LINK);
			ARML.taskCenter().listenTask(resourcepacks);
			resourcepacks.run();
		});
		getInstance().update().run();
	}

	private ArchiveRepository<ResourcePack> getArchiveRepository()
	{
		if (archiveRepository != null) return archiveRepository;
		return archiveRepository = new LocalArchiveRepository<>(getContext().getRoot().resolve("resourcepacks"),
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
