package net.launcher.utils.resource;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.CallbacksOption;
import net.launcher.utils.MD5;
import net.launcher.utils.ProgressCallback;
import net.launcher.utils.ProgressCallbackAdapter;
import net.launcher.utils.serial.BiSerializer;
import net.launcher.utils.serial.Deserializer;
import net.wheatlauncher.utils.DirUtils;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.mcdownloader.download.Downloader;
import org.to2mbn.jmccc.mcdownloader.download.DownloaderBuilders;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.FileDownloadTask;
import org.to2mbn.jmccc.mcdownloader.download.tasks.MemoryDownloadTask;

import java.io.*;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ci010
 */
class ArchiveRepositoryBase<T>
		implements ArchiveRepository<T>,
				   Repository.RemoteRepository<ArchiveRepository.Resource<T>>
{
	private Path root;
	private Proxy proxy;
	private ExecutorService service;

	private ObservableMap<String, Resource<T>> archesMap = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());
	private EmbeddedRemoteArchiveRepository[] remoteRepository;
	private Deserializer<T, Path> parser;
	private BiSerializer<T, NBTCompound> archiveSerializer;

	public Path getRoot()
	{
		return root;
	}

	public ExecutorService getService()
	{
		return service;
	}

	public ArchiveRepositoryBase(Path root, ExecutorService service,
								 Remote[] remote,
								 Deserializer<T, Path> parser,
								 BiSerializer<T, NBTCompound> archiveSerializer)
	{
		this.root = root;
		this.service = service;
		this.remoteRepository = (EmbeddedRemoteArchiveRepository[]) new Object[remote.length];
		for (int i = 0; i < remote.length; i++)
			this.remoteRepository[i] = new EmbeddedRemoteArchiveRepository(remote[i]);
		this.parser = parser;
		this.archiveSerializer = archiveSerializer;
		this.update();
	}

	@Override
	public void setProxy(Proxy proxy)
	{
		this.proxy = proxy;
	}

	@Override
	public Proxy getProxy()
	{
		return proxy;
	}

	@Override
	public Future<Void> update()
	{
		return service.submit(() ->
		{
			for (EmbeddedRemoteArchiveRepository tRemoteRepository : remoteRepository)
				tRemoteRepository.update();
			Files.walkFileTree(root, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					FileVisitResult result = super.visitFile(file, attrs);
					if (result == FileVisitResult.CONTINUE)
						if (file.endsWith(".dat")) loadResource(file);
					return result;
				}
			});
			return null;
		});
	}

	protected Resource<T> loadResource(Path indexFile) throws IOException
	{
		NBTCompound read = NBT.read(indexFile, true).asCompound();
		Resource<T> tResource = readNBT(read, this);
		return tResource;
	}

	private Resource<T> readNBT(NBTCompound compound, Object sig)
	{
		return new Resource<>(
				ResourceType.valueOf(compound.get("type").asString()),
				compound.get("hash").asString(),
				archiveSerializer.deserialize(compound.get("data").asCompound()), sig);
	}

	@Override
	public void check(Path directory, Consumer<Throwable> handler)
	{
		Objects.requireNonNull(directory);

		if (!Files.exists(directory)) if (handler != null) handler.accept(new FileNotFoundException());
		service.submit(() ->
				{
					try (Stream<Path> stream = Files.list(directory))
					{
						stream.forEach(p ->
								importFile(p, new ProgressCallbackAdapter<Resource<T>>()
								{
									@Override
									public void failed(Throwable e)
									{
										if (handler != null) handler.accept(e);
									}
								}));
					}
					catch (IOException e)
					{
						if (handler != null)
							handler.accept(e);
					}
				}
		);
	}

	@Override
	public Set<String> getAllVisiblePaths()
	{
		Set<String> set = new HashSet<>();
		for (EmbeddedRemoteArchiveRepository repository : remoteRepository)
			set.addAll(repository.getAllVisiblePaths());
		set.addAll(archesMap.keySet());
		return set;
	}

	private ObservableMap<String, Resource<T>> view = FXCollections.unmodifiableObservableMap(this.archesMap);

	@Override
	public ObservableMap<String, Resource<T>> getResourceMap()
	{
		return view;
	}

	@Override
	public Resource<T> mapResource(String hash)
	{
		Objects.requireNonNull(hash);
		return archesMap.get(hash);
	}

	@Override
	public Future<Boolean> containResource(String path, Callback<Boolean> callback)
	{
		Objects.requireNonNull(path);
		return service.submit(CallbacksOption.wrap(() ->
		{
			if (archesMap.containsKey(path)) return true;
			if (Files.exists(root.resolve(path + ".dat")))
			{
				service.submit(() -> loadResource(root.resolve(path + ".dat")));
				return true;
			}
			else for (EmbeddedRemoteArchiveRepository repository : remoteRepository)
				if (repository.containResource(path, null).get()) return true;
			return false;
		}, callback));
	}

	@Override
	public Delivery<Resource<T>> fetchResource(Path directory, String path, ProgressCallback<Resource<T>> callback,
											   FetchOption option)
	{
		Objects.requireNonNull(directory);
		Objects.requireNonNull(path);
		callback.updateProgress(0, 3, "start");
		return new DeliveryImpl<>(service.submit(() ->
		{
			callback.updateProgress(1, 3, "checking");
			if (!containLocal(path))
			{
				for (EmbeddedRemoteArchiveRepository repository : remoteRepository)
					if (repository.containResource(path, null).get())
					{
						repository.fetchResource(root, path, null, option).get();
						callback.updateProgress(2, 3, "fetching");
						return fetchLocal(directory, path, option);
					}
				throw new IOException("No resource", new FileNotFoundException("No resource in " + path));
			}
			else
			{
				callback.updateProgress(2, 3, "fetching");
				return fetchLocal(directory, path, option);
			}
		}), Collections.singleton(directory.resolve(path)), service);
	}

	private boolean containLocal(String path)
	{
		if (archesMap.containsKey(path))
			return true;
		return Files.exists(root.resolve(path + ".dat"));
	}

	private Resource<T> fetchLocal(Path dir, String path, FetchOption option) throws IOException
	{
		Resource<T> resource = archesMap.get(path);
		Path resolve = root.resolve(resource.getHash() + resource.getType().getSuffix());
		Path target = dir.resolve(resource.getName() + resource.getType().getSuffix());
		FetchUtils.fetch(resolve, target, option);
		return resource;
	}

	@Override
	public Future<Resource<T>> importFile(Path file, ProgressCallback<Resource<T>> callback)
	{
		Objects.requireNonNull(file);
		ProgressCallback<Resource<T>> call = callback == null ? new ProgressCallbackAdapter<Resource<T>>() {} : callback;
		return service.submit(CallbacksOption.wrap(() ->
		{
			call.updateProgress(0, 3, "start");
			ResourceType resourceType = ResourceType.getType(file);
			if (resourceType != null)
			{
				String md5 = MD5.toString(MD5.check(file));
				call.updateProgress(1, 3, "checking");

				if (!this.containResource(md5, null).get())
				{
					call.updateProgress(2, 3, "copying");

					Path target = root.resolve(md5 + resourceType.getSuffix());
					if (!Files.exists(target))
						if (Files.isDirectory(target)) DirUtils.copy(file.toFile(), target.toFile());
						else Files.copy(file, target);

					call.updateProgress(2, 3, "resolving");

					String simpleName = file.getFileName().toString().replace(resourceType.getSuffix(), "");
					FileSystem system = null;
					try
					{
						system = FileSystems.newFileSystem(file, ArchiveRepositoryBase.class.getClassLoader());
					}
					catch (IOException ignored) {}
					if (system != null)
						target = system.getPath("");
					T deserialize = parser.deserializeWithException(target, call::failed);
					if (deserialize == null) throw new IOException();
					Resource<T> resource = new Resource<>(resourceType, md5,
							deserialize, this).setName(simpleName);
					this.archesMap.put(md5, resource);

					call.updateProgress(2, 3, "saving");

					this.saveResource(resource);
					return resource;
				}
				else
				{
					call.updateProgress(2, 3, "exist");
					return archesMap.get(md5);
				}
			}
			else throw new IOException();
		}, call));
	}

	protected void saveResource(ArchiveRepository.Resource<T> resource) throws IOException
	{
		NBTCompound compound = NBT.compound().put("type", resource.getType().toString()).put("hash", resource.getHash())
				.put("data", archiveSerializer.serialize(resource.getContainData())).put("name", resource.getName());
		NBT.write(root.resolve(resource.getHash() + ".dat"), compound, true);
	}

	@Override
	public InputStream openStream(Resource<T> resource, String path) throws IOException
	{
		Objects.requireNonNull(resource);
		Objects.requireNonNull(path);

		if (resource.getSignature() == this && containLocal(resource.getHash()))
			return new FileInputStream(root.resolve(resource.getHash() + resource.getType().getSuffix()).toFile());
		else
			for (EmbeddedRemoteArchiveRepository repository : remoteRepository)
				if (resource.getSignature() == repository)
					return new URL(repository.remote.parseToURL(this.getProxy(), resource.getHash() + resource.getType().getSuffix())).openStream();
		throw new FileNotFoundException();
	}

	@Override
	public Delivery<Void> fetchAllResources(Path directory, ProgressCallback<Void> callback, FetchOption option)
	{
		Objects.requireNonNull(directory);
		callback.updateProgress(0, 2, "start");
		return new DeliveryImpl<>(service.submit(CallbacksOption.wrap(() ->
		{
			callback.updateProgress(1, 2, "fetching");
			for (EmbeddedRemoteArchiveRepository repository : remoteRepository)
				for (String s : repository.getAllVisiblePaths())
					if (!containLocal(s))
						repository.fetchResource(root, s, null, option).get();
			for (String s : getAllVisiblePaths())
				fetchLocal(directory, s, option);
			return null;
		}, callback)), this.getAllVisiblePaths().stream().map(directory::resolve).collect(Collectors.toSet()), service);
	}


	private class EmbeddedRemoteArchiveRepository
			implements RemoteRepository<Resource<T>>
	{
		private Remote remote;
		private Set<String> index;
		private TreeMap<String, Resource<T>> cache;

		private EmbeddedRemoteArchiveRepository(Remote remote)
		{
			this.remote = remote;
			this.index = new TreeSet<>();
			this.cache = new TreeMap<>();
			this.update();
		}

		@Override
		public Future<Void> update()
		{
			return getService().submit(() ->
			{
				HttpRequester requester = new HttpRequester(getProxy());
				String s = remote.parseToURL(getProxy(), "index.json");
				String get = requester.request("GET", s);
				if (get != null) new JSONArray(get).forEach(o -> index.add(o.toString()));
				return null;
			});
		}

		@Override
		public void check(Path directory, Consumer<Throwable> handler)
		{
			throw new UnsupportedOperationException();
			//noop
		}

		public Proxy getProxy() {return ArchiveRepositoryBase.this.getProxy();}

		@Override
		public void setProxy(Proxy proxy) {}

		@Override
		public Delivery<Resource<T>> fetchResource(Path directory, String path,
												   ProgressCallback<Resource<T>> callback,
												   FetchOption option)
		{
			Objects.requireNonNull(directory);
			Objects.requireNonNull(path);
			if (directory != root)
				throw new IllegalArgumentException();
			if (callback == null) callback = new ProgressCallbackAdapter<Resource<T>>() {};
			ProgressCallback<Resource<T>> call = callback;
			return new DeliveryImpl<>(getService().submit(() ->
			{
				call.updateProgress(0, 3, "start");
				if (containResource(path, null).get())
				{
					call.updateProgress(1, 3, "found");

					Downloader build = DownloaderBuilders.downloader().build();
					Resource<T> resource = cache.get(path);
					if (resource != null)
						cache.remove(path);
					else
					{
						Path resolve = root.resolve(path + ".dat");
						build.download(new FileDownloadTask(remote.parseToURL(getProxy(), path + ".dat"), resolve.toFile()), null).get();
						resource = loadResource(resolve);
					}
					String cachePath = resource.getHash() + resource.getType().getSuffix();
					final DownloadCallback<T> finalDownloadCallback = call instanceof DownloadCallback ? (DownloadCallback<T>) call : null;

					Resource<T> finalRes = resource;
					call.updateProgress(2, 3, "downloading");

					build.download(new FileDownloadTask(remote.parseToURL(getProxy(), cachePath), directory.resolve
									(cachePath)
									.toFile()),
							new DownloadCallback<Void>()
							{
								@Override
								public void updateProgress(long done, long total)
								{
									if (finalDownloadCallback != null)
										finalDownloadCallback.updateProgress(done, total);
								}

								@Override
								public void retry(Throwable e, int current, int max)
								{
									if (finalDownloadCallback != null)
										finalDownloadCallback.retry(e, current, max);
								}

								@Override
								public void done(Void result)
								{
									call.done(finalRes);
								}

								@Override
								public void failed(Throwable e)
								{
									call.failed(e);
								}

								@Override
								public void cancelled()
								{
									call.cancelled();
								}
							});
					return resource;
				}
				return null;
			}), Collections.singleton(directory.resolve(path)), service);
		}

		@Override
		public Future<Boolean> containResource(String hash, Callback<Boolean> callback)
		{
			return getService().submit(CallbacksOption.wrap(() ->
			{
				if (getAllVisiblePaths().contains(hash)) return true;
				String url = remote.parseToURL(getProxy(), hash + ".dat");
				MemoryDownloadTask memoryDownloadTask = new MemoryDownloadTask(url);
				Downloader downloader = DownloaderBuilders.cacheableDownloader().build();
				try
				{
					byte[] bytes = downloader.download(memoryDownloadTask, null).get();
					memoryDownloadTask = null;
					NBTCompound read = NBT.read(new ByteArrayInputStream(bytes), true).asCompound();
					boolean notNull = read != null;
					if (notNull)
					{
						cache.put(hash, readNBT(read, this));
						index.add(hash);
					}
					return notNull;
				}
				catch (ExecutionException e)
				{
					return false;
				}
			}, callback));
		}

		@Override
		public Collection<String> getAllVisiblePaths()
		{
			return index;
		}

		@Override
		public Delivery<Void> fetchAllResources(Path directory, ProgressCallback<Void> callback, FetchOption option)
		{
			Objects.requireNonNull(directory);
			if (directory != root)
				throw new IllegalArgumentException();
			if (callback == null) callback = new ProgressCallbackAdapter<Void>() {};
			Callback<Void> call = callback;
			getService().submit(CallbacksOption.wrap(() ->
			{
				for (String path : index)
					fetchResource(directory, path, new ProgressCallbackAdapter<Resource<T>>()
					{
						@Override
						public void failed(Throwable e)
						{
							call.failed(e);
						}
					}, option).get();
				return null;
			}, call));

			return null;
		}
	}
}
