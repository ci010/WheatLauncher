package net.wheatlauncher.utils.resource;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.wheatlauncher.utils.JsonSerializer;
import net.wheatlauncher.utils.MD5;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CombinedDownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask;
import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;

/**
 * @author ci010
 */
public class ArchiveRepository<T>
{
	private ObservableMap<String, ArchiveResource<T>> archesMap = FXCollections.observableHashMap();
	private ObservableMap<String, ArchiveResource<T>> archesMapView = FXCollections.unmodifiableObservableMap(archesMap);
	private final File repoRoot;

	private List<RemoteRepository<T>> remoteRepositories = Collections.emptyList();
	private String path;
	private Map<ResourceType, Function<File, T>> parserMap = new EnumMap<>(ResourceType.class);
	private JsonSerializer<T> serializer;
	private StorageHandler<T> storageHandler;

	public interface StorageHandler<T>
	{
		StoragePath[] dispatch(ArchiveResource<T> resource, JSONObject jsonData);
	}

	public static StorageHandler<?> DEFAULT = (resource, jsonData) ->
			new StoragePath[]{new StoragePath(resource.getName(), jsonData)};

	private ArchiveRepository(File root, String path,
							  Map<ResourceType, Function<File, T>> parserMap,
							  JsonSerializer<T> serializer,
							  StorageHandler<T> handler)
	{
		this.path = path;
		this.parserMap = parserMap;
		this.serializer = serializer;
		this.storageHandler = handler;
		this.repoRoot = new File(root, path);
		this.repoRoot.mkdir();
		this.getCacheRoot().mkdir();
		this.getIndexRoot().mkdir();
	}

	public void saveAll() throws IOException
	{
		for (Map.Entry<String, ArchiveResource<T>> entry : archesMap.entrySet())
		{
			StoragePath[] storagePaths = storageHandler.dispatch(entry.getValue(),
					this.writeArchiveToJson(entry.getValue()));
			File index = getIndexRoot();
			for (StoragePath storagePath : storagePaths)
			{
				File target = new File(index, storagePath.getPath() + ".json");
				FileWriter fileWriter = new FileWriter(target);
				storagePath.getJSON().write(fileWriter);
				fileWriter.close();
			}
		}
	}

	private void saveResource(ArchiveResource<T> resource) throws IOException
	{
		StoragePath[] storagePaths = storageHandler.dispatch(resource, this.writeArchiveToJson(resource));
		File index = getIndexRoot();
		for (StoragePath storagePath : storagePaths)
		{
			File target = new File(index, storagePath.getPath() + ".json");
			FileWriter fileWriter = new FileWriter(target);
			storagePath.getJSON().write(fileWriter);
			fileWriter.close();
		}
	}

	public void loadAll() throws IOException
	{
		File index = getIndexRoot();
		Files.walkFileTree(index.toPath(), new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				loadResource(file.toFile());
				return super.visitFile(file, attrs);
			}
		});
	}

	private ArchiveResource<T> loadResource(File file) throws FileNotFoundException
	{
		JSONObject jsonObject = new JSONObject(new FileInputStream(file));
		ArchiveResource<T> tArchiveResource = ArchiveRepository.this.readArchiveFromJson(jsonObject);
		ArchiveRepository.this.archesMap.put(tArchiveResource.getHash(), tArchiveResource);
		return tArchiveResource;
	}

	private JSONObject writeArchiveToJson(ArchiveResource<T> data)
	{
		JSONObject dataObj = new JSONObject();
		dataObj.put("type", data.getType().toString());
		dataObj.put("name", data.getName());
		dataObj.put("hash", data.getHash());
		dataObj.put("data", serializer.serialize(data.getContainData()));
		return dataObj;
	}

	private ArchiveResource<T> readArchiveFromJson(JSONObject dataObj)
	{
		ResourceType type = ResourceType.valueOf(dataObj.getString("type"));
		String md5 = dataObj.getString("hash");
		String name = dataObj.optString("name");
		T data = serializer.deserialize(dataObj.getJSONObject("data"));
		return new ArchiveResource<>(type, md5, data).setName(name);
	}

	public ObservableMap<String, ArchiveResource<T>> getAllStorage() {return archesMapView;}

	public String getRoot()
	{
		return path;
	}

	public File getResourceFile(ArchiveResource<T> resource)
	{
		return new File(getCacheRoot(), resource.getHash() + resource.getType().getSuffix());
	}

	private Map<String, ArchiveResource<T>> cachedIndexedMap = new HashMap<>();

	public Optional<ArchiveResource<T>> findResource(String indexPath) throws IOException
	{
		if (indexPath == null) return null;
		ArchiveResource<T> resource = this.cachedIndexedMap.get(indexPath);
		if (resource == null)
		{
			File file = new File(getIndexRoot(), indexPath + ".json");
			if (file.isFile())
			{
				resource = loadResource(file);
				cachedIndexedMap.put(indexPath, resource);
			}
			else
				for (RemoteRepository<T> repository : remoteRepositories)
					if (repository.hasResource(indexPath))
						repository.fetch(indexPath, createCallback());
		}
		return Optional.ofNullable(resource);
	}

	private CombinedDownloadCallback<File> createCallback()
	{
		return new CombinedDownloadCallback<File>()
		{
			@Override
			public <R> DownloadCallback<R> taskStart(DownloadTask<R> task)
			{
				return null;
			}

			@Override
			public void done(File result)
			{

			}

			@Override
			public void failed(Throwable e)
			{

			}

			@Override
			public void cancelled()
			{

			}
		};
	}

	public File getIndexRoot()
	{
		return new File(repoRoot, "index");
	}

	public File getCacheRoot()
	{
		return new File(repoRoot, "cache");
	}

	private void scanFile(File file)
	{
		try
		{
			ResourceType resourceType = ResourceType.getType(file);
			if (resourceType != null && parserMap.containsKey(resourceType))
			{
				String md5 = MD5.toString(MD5.getMD5Fast(file));
				if (!archesMap.containsKey(md5))
				{
					File target = new File(getCacheRoot(),
							md5 + resourceType.getSuffix());
					if (!target.isFile()) Files.copy(file.toPath(), target.toPath());
					String simpleName = file.getName().replace(resourceType.getSuffix(), "");
					ArchiveResource<T> resource = new ArchiveResource<>(resourceType, md5, parserMap.get
							(resourceType).apply(target)).setName(simpleName);
					this.archesMap.put(md5, resource);
					this.saveResource(resource);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void bind(ObservableValue<MinecraftDirectory> directoryTarget)
	{
		directoryTarget.addListener(dirListener);
	}

	public void unbind(ObservableValue<MinecraftDirectory> directoryTarget)
	{
		directoryTarget.removeListener(dirListener);
	}

	private ChangeListener<MinecraftDirectory> dirListener = (observable, oldValue, newValue) -> {
		File dir = new File(newValue.getRoot(), path);
		if (!dir.exists()) return;

		File[] files = dir.listFiles();
		if (files != null && files.length > 0)
			for (File file : files)
				this.scanFile(file);
	};

	public static class Builder<T> implements javafx.util.Builder<ArchiveRepository<T>>
	{
		private String path;
		private File parent;
		private Map<ResourceType, Function<File, T>> parserMap = new EnumMap<>(ResourceType.class);
		private JsonSerializer<T> serializer;
		private StorageHandler<T> storageHandler = (StorageHandler<T>) DEFAULT;

		public Builder(File parent, String path, JsonSerializer<T> serializer)
		{
			Objects.requireNonNull(path);
			Objects.requireNonNull(serializer);
			this.parent = parent;
			this.path = path;
			this.serializer = serializer;
		}

		public Builder<T> setStorageHandler(StorageHandler<T> storageHandler)
		{
			Objects.requireNonNull(storageHandler);
			this.storageHandler = storageHandler;
			return this;
		}

		public Builder<T> registerParser(ResourceType type, Function<File, T> parser)
		{
			Objects.requireNonNull(type);
			Objects.requireNonNull(parser);
			parserMap.put(type, parser);
			return this;
		}

		@Override
		public ArchiveRepository<T> build()
		{
			if (parserMap.isEmpty()) throw new IllegalStateException("The parser map is still empty! This repository " +
					"won't work!");
			return new ArchiveRepository<>(parent, path, parserMap, serializer, storageHandler);
		}
	}
}
