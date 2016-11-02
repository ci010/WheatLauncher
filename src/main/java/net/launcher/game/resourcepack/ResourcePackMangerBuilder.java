package net.launcher.game.resourcepack;

import net.launcher.LaunchElementManager;
import net.launcher.game.nbt.NBT;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repositories;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.Builder;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
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
	public LaunchElementManager<ResourcePack> build()
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
					String name = context.get("fileName").toString().split(".")[0], descriptor = "";
					int format = -1;
					Path resolve = file.resolve("pack.mcmeta");
					try (SeekableByteChannel channel = Files.newByteChannel(resolve))
					{
						byte[] bytes = new byte[(int) Files.size(resolve)];
						ByteBuffer buffer = ByteBuffer.wrap(bytes);
						channel.read(buffer);
						JSONObject metaObj = new JSONObject(IOUtils.toString(bytes));
						format = metaObj.optInt("pack_format", -1);
						descriptor = metaObj.optString("description", "");
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
