package net.launcher.resourcepack;

import net.launcher.LaunchElementManager;
import net.launcher.game.ResourcePack;
import net.launcher.game.nbt.NBT;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repositories;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.Builder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author ci010
 */
public class ResourcePackMangerBuilder implements Builder<LaunchElementManager<ResourcePack>>
{
	public static ResourcePackMangerBuilder create(Path root, ExecutorService service)
	{
		Objects.requireNonNull(root);
		Objects.requireNonNull(service);
		return new ResourcePackMangerBuilder(root, service);
	}

	private ResourcePackMangerBuilder(Path root, ExecutorService service)
	{
		this.root = root;
		this.service = service;
	}

	private Path root;
	private ExecutorService service;

	private ArchiveRepository<ResourcePack> archiveRepository;

	public ResourcePackMangerBuilder setArchiveRoot(Path root)
	{
		this.root = root;
		return this;
	}

	public ResourcePackMangerBuilder setExecutor(ExecutorService service)
	{
		this.service = service;
		return this;
	}

	public ResourcePackMangerBuilder setArchiveRepository(ArchiveRepository<ResourcePack> archiveRepository)
	{
		this.archiveRepository = archiveRepository;
		return this;
	}

	@Override
	public ResourcePackManager build()
	{
		return new ResourcePackManImpl(archiveRepository == null ? getArchiveRepository() : archiveRepository);
	}

	public ArchiveRepository<ResourcePack> getArchiveRepository()
	{
		return Repositories.newArchiveRepositoryBuilder(root, service,
				BiSerializer.combine(
						(data, context) ->
								NBT.compound().put("name", data.getPackName()).put("description",
										data.getDescription()).put("format", data.packFormat()),
						(serialized, context) -> new ResourcePack(serialized.get("name").asString(), serialized.get("description").asString(), serialized.get("format").asInt())),
				(file, context) ->
				{
					String raw = context.get("fileName").toString();
					String name = raw.substring(0, raw.lastIndexOf('.')), descriptor = "";
					int format = -1;
					Path resolve = file.resolve("pack.mcmeta");
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
					return new ResourcePack(name, descriptor, format);
				})
				.build();
	}
}
