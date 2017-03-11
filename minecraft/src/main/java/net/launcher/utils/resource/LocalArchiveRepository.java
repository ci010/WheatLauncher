package net.launcher.utils.resource;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.util.Pair;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.MD5;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.serial.BiSerializer;
import net.launcher.utils.serial.Deserializer;
import net.launcher.utils.serial.SerializeMetadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

	private final Set<String> importing = Collections.synchronizedSet(new HashSet<>());
	private final AtomicBoolean updating = new AtomicBoolean();

	private Function<T, String> nameDecorator;

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
				archiveSerializer.deserialize(compound.get("data").asCompound()), this,
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

	private String parseName(T data, String fallback)
	{
		if (nameDecorator != null)
			return nameDecorator.apply(data);
		return fallback;
	}

	private T parseData(Path file) throws Exception
	{
		Path realRoot = getRealRoot(file);
		Map<Object, Object> context = new HashMap<>();
		SerializeMetadata.decorateWithFileInfo(context, file);

		T deserialize = parser.deserialize(realRoot, context);
		if (deserialize == null) throw new IOException("Unable to parse the [" + file + "]!");
		return deserialize;
	}

	@Override
	public Task<ArchiveRepository<T>> update()
	{
		return new Task<ArchiveRepository<T>>()
		{
			Map<String, Resource<T>> archesMap1 = new HashMap<>();
			Map<String, Pair<Path, ResourceType>> resourcesMap = new HashMap<>();

			void compareResource() throws Exception
			{
				Map<String, String> nameToMd5 = new HashMap<>();
				Set<String> keySet = archesMap1.keySet();
				for (String name : keySet)
				{
					Pair<Path, ResourceType> pair = resourcesMap.get(name);
					if (pair == null)//no such resource
					{
						archesMap1.remove(name);
						//TODO report missing resources
						continue;
					}
					Path path = pair.getKey();
					String md5 = MD5.digest(path).toString();
					if (!md5.equals(name))
					{
						archesMap1.remove(name);
						nameToMd5.put(name, md5);
						//TODO report modified resources
					}
					else resourcesMap.remove(name);//remove checked resource
				}
				for (Map.Entry<String, Pair<Path, ResourceType>> entry : resourcesMap.entrySet())//reconfigure unpaired resource
				{
					Path file = entry.getValue().getKey();
					ResourceType resourceType = entry.getValue().getValue();
					String name = entry.getKey();

					String md5 = nameToMd5.get(name);
					if (md5 == null) md5 = MD5.digest(file).toString();

					T deserialize = parseData(file);
					name = parseName(deserialize, name);
					Resource<T> resource = new Resource<>(resourceType, md5,
							deserialize, LocalArchiveRepository.this).setName(name);
					saveResource(resource);

					Path diskLocation = root.resolve(md5 + resource.getType().getSuffix());
					Files.move(file, diskLocation);

					archesMap1.put(md5, resource);
				}
			}

			@Override
			protected ArchiveRepository<T> call() throws Exception
			{
				if (updating.compareAndSet(false, true))
				{
					while (!importing.isEmpty())
						synchronized (importing) {importing.wait();}
					if (Files.exists(root))
					{
						Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class),
								2, new SimpleFileVisitor<Path>()
								{
									@Override
									public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
									{
										FileVisitResult result = super.visitFile(file, attrs);
										if (result == FileVisitResult.CONTINUE)
											if (file.getFileName().toString().endsWith(".dat"))
											{
												NBTCompound read = NBT.read(file, true).asCompound();
												Resource<T> tResource = readNBT(read, this);
												archesMap1.put(tResource.getHash(), tResource);
											}
											else
											{
												ResourceType apply = resourceTypeParser.apply(file);
												if (apply != null)
												{
													String name = file.getFileName().toString();
													name = name.substring(0, name.lastIndexOf('.'));
													resourcesMap.put(name, new Pair<>(file, apply));
												}
											}
										return result;
									}
								});
						compareResource();

						LocalArchiveRepository.this.archesMap.clear();
						LocalArchiveRepository.this.archesMap.putAll(this.archesMap1);
					}
					else Files.createDirectories(root);
					updating.set(false);
					updating.notifyAll();
				}
				return LocalArchiveRepository.this;
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
				{
					importing.remove(md5);
					if (importing.isEmpty()) importing.notifyAll();
				}
			}

			@Override
			protected Resource<T> call() throws Exception
			{
				while (updating.get()) synchronized (updating) {updating.wait();}

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

				if (importing.contains(md5))
				{
					updateProgress(2, 3);
					updateMessage("cancel");
					this.cancel();
					return null;
				}
				else
				{
					this.md5 = md5;
					importing.add(md5);
				}

				updateProgress(2, 3);
				updateMessage("resolving");

				T deserialize = parseData(file);
				String simpleName = parseName(deserialize, file.getFileName().toString().replace(resourceType.getSuffix(), ""));
				Resource<T> resource = new Resource<>(resourceType, md5,
						deserialize, LocalArchiveRepository.this).setName(simpleName);
				Platform.runLater(() -> archesMap.put(md5, resource));
				saveResource(resource);

				updateMessage("copying");

				Path diskLocation = root.resolve(resource.getHash() + resource.getType().getSuffix());
				if (!Files.exists(diskLocation))
					if (Files.isDirectory(diskLocation)) NIOUtils.copyDirectory(file, diskLocation);
					else Files.copy(file, diskLocation);

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

	private Path getRealRoot(Path file)
	{
		FileSystem system = null;
		try {system = FileSystems.newFileSystem(file, ArchiveRepository.class.getClassLoader());}
		catch (IOException ignored) {}
		if (system != null) file = system.getPath("/");
		return file;
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
			file = getRealRoot(file).resolve(path);
			return Files.newInputStream(file);
		}
		else throw new FileNotFoundException(path);
	}
}
