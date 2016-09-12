package net.wheatlauncher.internal.repository;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.wheatlauncher.Core;
import net.wheatlauncher.utils.JsonSerializer;
import net.wheatlauncher.utils.MD5;
import net.wheatlauncher.utils.resource.ArchiveResource;
import net.wheatlauncher.utils.resource.ResourceType;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
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
public class ArchiveRepository<T> implements ChangeListener<MinecraftDirectory>
{
	private ObservableMap<String, ArchiveResource<T>> archesMap = FXCollections.observableHashMap();
	private ObservableMap<String, ArchiveResource<T>> archesMapView = FXCollections.unmodifiableObservableMap(archesMap);
	private final File repoRoot;

	private String path;
	private Map<ResourceType, Function<File, T>> parserMap = new EnumMap<>(ResourceType.class);
	private JsonSerializer<T> serializer;
	private StorageHandler<T> storageHandler;

	public interface RemoteRepository
	{
		boolean hasResource(String path);
	}

	public interface StorageHandler<T>
	{
		StoragePath[] dispatch(T rawData, JSONObject jsonData);
	}

	public static StorageHandler<?> DEFAULT = (rawData, jsonData) ->
			new StoragePath[]{new StoragePath("", jsonData)};

	private ArchiveRepository(String path,
							  Map<ResourceType, Function<File, T>> parserMap,
							  JsonSerializer<T> serializer,
							  StorageHandler<T> handler)
	{
		this.path = path;
		this.parserMap = parserMap;
		this.serializer = serializer;
		this.storageHandler = handler;
		this.repoRoot = new File(Core.INSTANCE.getArchivesRoot(), path);
		this.repoRoot.mkdir();
		this.getCacheRoot().mkdir();
		this.getIndexRoot().mkdir();
	}

	public void saveAll() throws IOException
	{
		for (Map.Entry<String, ArchiveResource<T>> entry : archesMap.entrySet())
		{
			StoragePath[] storagePaths = storageHandler.dispatch(entry.getValue().getContainData(), this.writeArchiveToJson(entry.getValue()));
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

	private void trySave(ArchiveResource<T> resource) throws IOException
	{
		StoragePath[] storagePaths = storageHandler.dispatch(resource.getContainData(), this.writeArchiveToJson(resource));
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
				tryLoad(file.toFile());
				return super.visitFile(file, attrs);
			}
		});
	}

	private ArchiveResource<T> tryLoad(File file) throws FileNotFoundException
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
		dataObj.put("hash", data.getHash());
		dataObj.put("data", serializer.serialize(data.getContainData()));
		return dataObj;
	}

	private ArchiveResource<T> readArchiveFromJson(JSONObject dataObj)
	{
		ResourceType type = ResourceType.valueOf(dataObj.getString("type"));
		String md5 = dataObj.getString("hash");
		T data = serializer.deserialize(dataObj.getJSONObject("data"));
		return new ArchiveResource<>(type, md5, data);
	}

	public ObservableMap<String, ArchiveResource<T>> getAllStorage() {return archesMapView;}

	public String getRoot()
	{
		return path;
	}

	public File getFileLocation(ArchiveResource<T> resource)
	{
		return new File(getCacheRoot(), resource.getHash() + resource.getType().getSuffix());
	}

	private Map<String, ArchiveResource<T>> cachedIndexedMap = new HashMap<>();

	public Optional<ArchiveResource<T>> findResource(String indexPath)
	{
		if (indexPath == null) return null;
		ArchiveResource<T> resource = this.cachedIndexedMap.get(indexPath);
		if (resource == null)
		{
			File file = new File(getIndexRoot(), indexPath + ".json");
			if (file.isFile())
			{
				try
				{
					resource = tryLoad(file);
					cachedIndexedMap.put(indexPath, resource);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			else
			{

			}
		}
		return Optional.ofNullable(resource);
	}

	public File getIndexRoot()
	{
		return new File(repoRoot, "index");
	}

	public File getCacheRoot()
	{
		return new File(repoRoot, "cache");
	}

	@Override
	public void changed(ObservableValue<? extends MinecraftDirectory> observable, MinecraftDirectory oldValue, MinecraftDirectory newValue)
	{
		File dir = new File(newValue.getRoot(), path);
		if (!dir.exists()) return;

		File[] files = dir.listFiles();
		if (files != null)
			for (File file : files)
				try
				{
					ResourceType resourceType = ResourceType.getType(file);
					if (resourceType != null && parserMap.containsKey(resourceType))
					{
						String md5 = MD5.toString(MD5.getMD5Fast(file));
						if (!archesMap.containsKey(md5))
						{
							Files.copy(file.toPath(), new File(getCacheRoot(),
									md5 + resourceType.getSuffix()).toPath());
							ArchiveResource<T> resource = new ArchiveResource<>(resourceType, md5, parserMap.get
									(resourceType).apply(file));
							archesMap.put(md5, resource);
							trySave(resource);
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
	}

	public static class Builder<T> implements javafx.util.Builder<ArchiveRepository<T>>
	{
		private String path;
		private Map<ResourceType, Function<File, T>> parserMap = new EnumMap<>(ResourceType.class);
		private JsonSerializer<T> serializer;
		private StorageHandler<T> storageHandler = (StorageHandler<T>) DEFAULT;

		public Builder(String path, JsonSerializer<T> serializer)
		{
			Objects.requireNonNull(path);
			Objects.requireNonNull(serializer);
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
			return new ArchiveRepository<>(path, parserMap, serializer, storageHandler);
		}
	}
}
