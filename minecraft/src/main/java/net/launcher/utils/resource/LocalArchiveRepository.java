package net.launcher.utils.resource;

import javafx.application.Platform;
import javafx.concurrent.Task;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.MD5;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.Tasks;
import net.launcher.utils.serial.BiSerializer;
import net.launcher.utils.serial.Deserializer;
import net.launcher.utils.serial.SerializeMetadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class LocalArchiveRepository<T> extends ArchiveRepositoryBase<T> implements ArchiveRepository<T>
{
	private Path root;
	private Deserializer<T, Path> parser;
	private BiSerializer<T, NBTCompound> archiveSerializer;
	private Function<Path, ResourceType> resourceTypeParser;

	private Set<String> hashLock = Collections.synchronizedSet(new HashSet<>());

	public LocalArchiveRepository(Path root, Deserializer<T, Path> parser,
								  BiSerializer<T, NBTCompound> archiveSerializer)
	{
		this(root, DefaultResourceType::getType, parser, archiveSerializer);
	}

	public LocalArchiveRepository(Path root, Function<Path, ResourceType> resourceTypeParser, Deserializer<T, Path> parser,
								  BiSerializer<T, NBTCompound> archiveSerializer)
	{
		this.root = root;
		this.parser = parser;
		this.archiveSerializer = archiveSerializer;
		this.resourceTypeParser = resourceTypeParser;
	}


	protected Resource<T> getResource(String resource) throws IOException
	{
		if (Files.exists(root.resolve(resource + ".dat")))
			return loadResource(root.resolve(resource + ".dat"));
		return null;
	}

	private Resource<T> loadResource(Path indexFile) throws IOException
	{
		NBTCompound read = NBT.read(indexFile, true).asCompound();
		Resource<T> tResource = readNBT(read, this);
		this.archesMap.put(tResource.getHash(), tResource);
		return tResource;
	}

	private void saveResource(Resource<T> resource) throws IOException
	{
		NBTCompound compound = NBT.compound()
				.put("type", resource.getType().toString())
				.put("hash", resource.getHash())
				.put("data", archiveSerializer.serialize(resource.getContainData()))
				.put("metadata", resource.getCompound());
		NBT.write(root.resolve(resource.getHash() + ".dat"), compound, true);

	}

	private Resource<T> readNBT(NBTCompound compound, Object sig)
	{
		return new Resource<>(
				DefaultResourceType.valueOf(compound.get("type").asString()),
				compound.get("hash").asString(),
				archiveSerializer.deserialize(compound.get("data").asCompound()), sig,
				compound.getOption("metadata").orElse(NBT.compound()).asCompound());
	}

	protected Resource<T> fetch(Path dir, String path, FetchOption option) throws IOException
	{
		Resource<T> resource = archesMap.get(path);
		if (resource == null) return null;
		Files.createDirectories(dir);
		Path resolve = root.resolve(resource.getHash() + resource.getType().getSuffix());
		Path target = dir.resolve(resource.getName() + resource.getType().getSuffix());
		FetchUtils.fetch(resolve, target, option);
		return resource;
	}

	@Override
	public Task<ArchiveRepository<T>> update()
	{
		return new Task<ArchiveRepository<T>>()
		{
			@Override
			protected ArchiveRepository<T> call() throws Exception
			{
				if (Files.exists(root))
					Files.walkFileTree(root, new SimpleFileVisitor<Path>()
					{
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
						{
							FileVisitResult result = super.visitFile(file, attrs);
							if (result == FileVisitResult.CONTINUE)
								if (file.getFileName().toString().endsWith(".dat")) loadResource(file);
							return result;
						}
					});
				else Files.createDirectories(root);
				return null;
			}
		};
	}


	@Override
	public Task<Resource<T>> importResource(Path file)
	{
		Objects.requireNonNull(file);
		return new Task<Resource<T>>()
		{
			private String md5;

			@Override
			protected void done()
			{
				if (md5 != null)
					hashLock.remove(md5);
			}

			@Override
			protected Resource<T> call() throws Exception
			{
				ResourceType resourceType = resourceTypeParser.apply(file);
				if (resourceType == null) throw new IOException();
				MD5 digest = MD5.digest(file);
				String md5 = digest.toString();

				updateProgress(1, 3);
				updateMessage("checking");

				if (getResource(md5) != null)
				{
					updateProgress(2, 3);
					updateMessage("exist");
					return archesMap.get(md5);
				}

				if (hashLock.contains(md5))
				{
					updateProgress(2, 3);
					updateMessage("cancel");
					this.cancel();
					return null;
				}
				else
				{
					this.md5 = md5;
					hashLock.add(md5);
				}

				updateProgress(2, 3);

				Path diskLocation = root.resolve(md5 + resourceType.getSuffix());
				Path fileRoot = diskLocation;
				FileSystem system = Tasks.optional(() -> FileSystems.newFileSystem(file,
						LocalArchiveRepository.class.getClassLoader())).orElse(null);
				if (system != null) fileRoot = system.getPath("/");

				Map<Object, Object> context = new HashMap<>();
				SerializeMetadata.decorateWithFileInfo(context, file);

				updateMessage("resolving");

				T deserialize = parser.deserialize(fileRoot, context);
				if (deserialize == null) throw new IOException("Unable to parse the [" + file + "]!");

				updateMessage("copying");

				if (!Files.exists(diskLocation))
					if (Files.isDirectory(diskLocation)) NIOUtils.copyDirectory(file, diskLocation);
					else Files.copy(file, diskLocation);

				String simpleName = file.getFileName().toString().replace(resourceType.getSuffix(), "");
				Resource<T> resource = new Resource<>(resourceType, md5,
						deserialize, LocalArchiveRepository.this).setName(simpleName);
				Platform.runLater(() -> archesMap.put(md5, resource));
				updateMessage("saving");
				saveResource(resource);
				updateMessage("done");
				return resource;
			}
		};
	}

	@Override
	public Task<Collection<Resource<T>>> importResources(Path directory)
	{
		Objects.requireNonNull(directory);

		return new Task<Collection<Resource<T>>>()
		{
			@Override
			protected Collection<Resource<T>> call() throws Exception
			{
				if (!Files.exists(directory)) throw new FileNotFoundException();
				List<Task<Resource<T>>> collect = Files.list(directory).map(LocalArchiveRepository.this::importResource).collect(Collectors
						.toList());
				ArrayList<Resource<T>> list = new ArrayList<>(collect.size());

				for (Task<Resource<T>> resourceTask : collect)
					resourceTask.run();
				for (Task<Resource<T>> task : collect)
					try
					{
						list.add(task.get());
					}
					catch (Exception ignored) {}
				return list;
			}
		};
	}

	@Override
	public InputStream openStream(Resource<T> resource, String path) throws IOException
	{
		Objects.requireNonNull(resource);
		Objects.requireNonNull(path);

		if (resource.getSignature() == this && getResource(resource.getHash()) != null)
		{
			Path file = root.resolve(resource.getHash() + resource.getType().getSuffix());
			if (!Files.exists(file)) throw new IOException("corrupted resource " + resource.getHash());
			FileSystem system = null;
			try {system = FileSystems.newFileSystem(file, ArchiveRepository.class.getClassLoader());}
			catch (IOException ignored) {}
			if (system != null) file = system.getPath("/");
			file = file.resolve(path);
			return Files.newInputStream(file);
		}
		else throw new FileNotFoundException(path);
	}
}
