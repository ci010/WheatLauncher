package net.launcher.utils.resource;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.CallbacksOption;
import net.launcher.utils.MD5;
import net.launcher.utils.serial.BiSerializer;
import net.launcher.utils.serial.Deserializer;
import net.wheatlauncher.utils.DirUtils;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.mcdownloader.download.Downloader;
import org.to2mbn.jmccc.mcdownloader.download.DownloaderBuilders;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.FileDownloadTask;
import org.to2mbn.jmccc.mcdownloader.download.tasks.MemoryDownloadTask;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
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
public class ArchiveRepositoryBase<T>
		implements ArchiveRepository<T>,
				   Repository.RemoteRepository<ArchiveRepository.Resource<T>>,
				   ArchiveRepository.Local<T>
{
	private Path root;
	private Proxy proxy;
	private ExecutorService service;

	private ObservableMap<String, Resource<T>> archesMap = FXCollections.observableHashMap();
	private EmbeddedRemoteArchiveRepository[] remoteRepository;
	private Map<ResourceType, Deserializer<T, Path>> parserMap = new EnumMap<>(ResourceType.class);
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
								 Remote<T>[] remote,
								 Map<ResourceType, Deserializer<T, Path>> parserMap,
								 BiSerializer<T, NBTCompound> archiveSerializer)
	{
		this.root = root;
		this.service = service;
		this.remoteRepository = (EmbeddedRemoteArchiveRepository[]) new Object[remote.length];
		for (int i = 0; i < remote.length; i++)
			this.remoteRepository[i] = new EmbeddedRemoteArchiveRepository(remote[i]);
		this.parserMap = parserMap;
		this.archiveSerializer = archiveSerializer;
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
		Resource<T> tResource = readNBT(read);
		return tResource;
	}

	private Resource<T> readNBT(NBTCompound compound)
	{
		return new Resource<>(
				ResourceType.valueOf(compound.get("type").asString()),
				compound.get("hash").asString(),
				archiveSerializer.deserialize(compound.get("data").asCompound()));
	}

	@Override
	public void check(Path directory, Consumer<Throwable> handler)
	{
		if (!Files.exists(directory)) return;
		service.submit(() ->
				{
					try (Stream<Path> stream = Files.list(directory))
					{
						stream.forEach(p ->
								importFile(p, new CallbackAdapter<Resource<T>>()
								{
									@Override
									public void failed(Throwable e) {handler.accept(e);}
								}));
					}
					catch (IOException e)
					{
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
	public void mapResource(String hash, Callback<Resource<T>> callback)
	{
		Objects.requireNonNull(hash);
		service.submit(CallbacksOption.wrap(() ->
		{
			Resource<T> resource = archesMap.get(hash);
			if (resource != null)
				return resource;
			if (containLocal(hash))
			{
				resource = loadResource(root.resolve(hash + ".dat"));
				this.archesMap.put(hash, resource);
			}
			return resource;
		}, callback));
	}

	@Override
	public Future<Boolean> containResource(String path, Callback<Boolean> callback)
	{
		Objects.requireNonNull(path);
		return service.submit(CallbacksOption.wrap(() ->
		{
			if (containLocal(path)) return true;
			else for (EmbeddedRemoteArchiveRepository repository : remoteRepository)
				if (repository.containResource(path, null).get()) return true;
			return false;
		}, callback));
	}

	@Override
	public Delivery<Resource<T>> fetchResource(Path directory, String path, Callback<Resource<T>> callback,
											   FetchOption option)
	{
		Objects.requireNonNull(directory);
		Objects.requireNonNull(path);
		return new DeliveryImpl<>(service.submit(() ->
		{
			if (!containLocal(path))
				for (EmbeddedRemoteArchiveRepository repository : remoteRepository)
					if (repository.containResource(path, null).get())
					{
						repository.fetchResource(root, path, null, option).get();
						break;
					}
			return fetchLocal(directory, path, option);
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
	public Future<Resource<T>> importFile(Path file, Callback<Resource<T>> callback)
	{
		return service.submit(CallbacksOption.wrap(() ->
		{
			ResourceType resourceType = ResourceType.getType(file);
			if (resourceType != null && parserMap.containsKey(resourceType))
			{
				String md5 = MD5.toString(MD5.check(file));
				if (this.containResource(md5, null).get())
				{
					Path target = root.resolve(md5 + resourceType.getSuffix());
					if (!Files.exists(target))
						if (Files.isDirectory(target)) DirUtils.copy(file.toFile(), target.toFile());
						else Files.copy(file, target);
					String simpleName = file.getFileName().toString().replace(resourceType.getSuffix(), "");
					Resource<T> resource = new Resource<>(resourceType, md5, parserMap.get
							(resourceType).deserialize(target)).setName(simpleName);
					this.archesMap.put(md5, resource);
					this.saveResource(resource);
					return resource;
				}
				else return archesMap.get(md5);
			}
			else throw new IOException();
		}, callback));
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
		if (archesMap.containsKey(resource.getHash()))
		{
			Path target = root.resolve(resource.getHash() + resource.getType().getSuffix());
			try (FileChannel channel = FileChannel.open(target))
			{
				long size = Files.size(target);
				channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
				byte[] bytes = new byte[(int) size];
				return new ByteArrayInputStream(bytes);
			}
		}
		throw new FileNotFoundException();
	}

	@Override
	public Delivery<Void> fetchAllResources(Path directory, Callback<Void> callback, FetchOption option)
	{
		return new DeliveryImpl<>(service.submit(CallbacksOption.wrap(() ->
		{
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
		private Remote<T> remote;
		private Set<String> index;
		private TreeMap<String, Resource<T>> cache;

		private EmbeddedRemoteArchiveRepository(Remote<T> remote)
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

		public Proxy getProxy() {return ArchiveRepositoryBase.this.getProxy();}

		@Override
		public void setProxy(Proxy proxy) {}

		@Override
		public Delivery<Resource<T>> fetchResource(Path directory, String path,
												   Callback<Resource<T>> callback,
												   FetchOption option)
		{
			Objects.requireNonNull(directory);
			Objects.requireNonNull(path);
			if (directory != root)
				throw new IllegalArgumentException();
			if (callback == null) callback = CallbacksOption.empty();
			Callback<Resource<T>> call = callback;
			return new DeliveryImpl<>(getService().submit(() ->
			{
				if (containResource(path, null).get())
				{
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
						cache.put(hash, readNBT(read));
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
		public Delivery<Void> fetchAllResources(Path directory, Callback<Void> callback, FetchOption option)
		{
			Objects.requireNonNull(directory);
			if (directory != root)
				throw new IllegalArgumentException();
			if (callback == null) callback = CallbacksOption.empty();
			Callback<Void> call = callback;
			getService().submit(CallbacksOption.wrap(() ->
			{
				for (String path : index)
					fetchResource(directory, path, CallbacksOption.handle(call::failed), option).get();
				return null;
			}, call));

			return null;
		}
	}
}
