package net.launcher.utils.resource;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.launcher.utils.MD5;
import net.launcher.utils.serial.BiSerializer;
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
public class ArchiveRepository<T>
{
	private ObservableMap<String, ArchiveResource<T>> archesMap = FXCollections.observableHashMap();
	private ObservableMap<String, ArchiveResource<T>> view = FXCollections.unmodifiableObservableMap(archesMap);
	private final File repoRoot;

	private String path;
	private Map<ResourceType, Function<File, T>> parserMap = new EnumMap<>(ResourceType.class);
	private BiSerializer<T, JSONObject> serializer;
	private StorageHandler<T> storageHandler;

	public interface StorageHandler<T>
	{
		StoragePath[] dispatch(ArchiveResource<T> resource, JSONObject jsonData);
	}

	public static StorageHandler<?> DEFAULT = (resource, jsonData) ->
			new StoragePath[]{new StoragePath(resource.getName(), jsonData)};

	private ArchiveRepository(File root, String path,
							  Map<ResourceType, Function<File, T>> parserMap,
							  BiSerializer<T, JSONObject> serializer,
							  StorageHandler<T> handler)
	{
		this.path = path;
		this.parserMap = parserMap;
		this.serializer = serializer;
		this.storageHandler = handler;
		this.repoRoot = new File(root, path);
		this.repoRoot.mkdir();
		new File(repoRoot, "cache").mkdir();
		new File(repoRoot, "index").mkdir();
	}

	public void saveAll() throws IOException
	{
		for (Map.Entry<String, ArchiveResource<T>> entry : archesMap.entrySet())
		{
			StoragePath[] storagePaths = storageHandler.dispatch(entry.getValue(),
					this.writeArchiveToJson(entry.getValue()));
			for (StoragePath storagePath : storagePaths)
			{
				File target = new File(repoRoot, toIndexPath(storagePath.getPath()));
				FileWriter fileWriter = new FileWriter(target);
				storagePath.getJSON().write(fileWriter);
				fileWriter.close();
			}
		}
	}

	private void saveResource(ArchiveResource<T> resource) throws IOException
	{
		StoragePath[] storagePaths = storageHandler.dispatch(resource, this.writeArchiveToJson(resource));
		for (StoragePath storagePath : storagePaths)
		{
			File target = new File(repoRoot, toIndexPath(storagePath.getPath()));
			FileWriter fileWriter = new FileWriter(target);
			storagePath.getJSON().write(fileWriter);
			fileWriter.close();
		}
	}

	public void loadAll() throws IOException
	{
		File index = new File(repoRoot, "index");
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

	public void exportResource(ArchiveResource<T> resource, File target) throws IOException
	{
		File resourceFile = getResourceFile(resource);
		Files.copy(resourceFile.toPath(), target.toPath());
	}

	public T importFile(File file) throws IOException
	{
		ResourceType resourceType = ResourceType.getType(file);
		if (resourceType != null && parserMap.containsKey(resourceType))
		{
			String md5 = MD5.toString(MD5.getMD5Fast(file));
			if (!archesMap.containsKey(md5))
			{
				File target = new File(repoRoot, toCachePath(md5, resourceType));
				if (!target.isFile()) Files.copy(file.toPath(), target.toPath());
				String simpleName = file.getName().replace(resourceType.getSuffix(), "");
				ArchiveResource<T> resource = new ArchiveResource<>(resourceType, md5, parserMap.get
						(resourceType).apply(target)).setName(simpleName);
				this.archesMap.put(md5, resource);
				this.saveResource(resource);
				return resource.getContainData();
			}
			else return archesMap.get(md5).getContainData();
		}
		return null;
	}

	private ArchiveResource<T> loadResource(File indexFile) throws FileNotFoundException
	{
		JSONObject jsonObject = new JSONObject(new FileInputStream(indexFile));
		ArchiveResource<T> tArchiveResource = ArchiveRepository.this.readArchiveFromJson(jsonObject);
		this.archesMap.put(tArchiveResource.getHash(), tArchiveResource);
		return tArchiveResource;
	}

	private JSONObject writeArchiveToJson(ArchiveResource<T> data)
	{
		JSONObject dataObj = new JSONObject();
		dataObj.put("type", data.getType().toString());
		dataObj.put("nameProperty", data.getName());
		dataObj.put("hash", data.getHash());
		dataObj.put("data", serializer.serialize(data.getContainData()));
		return dataObj;
	}

	private ArchiveResource<T> readArchiveFromJson(JSONObject dataObj)
	{
		ResourceType type = ResourceType.valueOf(dataObj.getString("type"));
		String md5 = dataObj.getString("hash");
		String name = dataObj.optString("nameProperty");
		T data = serializer.deserialize(dataObj.getJSONObject("data"));
		return new ArchiveResource<>(type, md5, data).setName(name);
	}

	public ObservableMap<String, ArchiveResource<T>> getAllStorage() {return view;}

	public String getRoot()
	{
		return path;
	}

	private File getResourceFile(ArchiveResource<T> resource)
	{
		return new File(repoRoot, toCachePath(resource.getHash(), resource.getType()));
	}

	public InputStream openStream(ArchiveResource<T> resource, String path) throws IOException
	{
		File resourceFile = getResourceFile(resource);
		return resource.getType().openStream(resourceFile, path);
	}

	private Map<String, ArchiveResource<T>> cachedIndexedMap = new HashMap<>();

	public Optional<ArchiveResource<T>> findResource(String path) throws IOException
	{
		if (path == null) return null;
		ArchiveResource<T> resource = this.cachedIndexedMap.get(path);
		if (resource == null)
		{
			File file = new File(repoRoot, toIndexPath(path));
			if (file.isFile())
			{
				resource = loadResource(file);
				cachedIndexedMap.put(path, resource);
			}
//			else
//				for (RemoteArchiveRepository<T> repository : remoteRepositories)
//					if (repository.hasResource(path))
//						repository.load(path, null);
		}
		return Optional.ofNullable(resource);
	}

	private String toIndexPath(String path)
	{
		return "index/" + path + ".json";
	}

	private String toCachePath(String hash, ResourceType type)
	{
		return "cache/" + hash + type.getSuffix();
	}


	public void bind(ObservableValue<MinecraftDirectory> directoryTarget)
	{
		directoryTarget.addListener(dirListener);
	}

	public void unbind(ObservableValue<MinecraftDirectory> directoryTarget)
	{
		directoryTarget.removeListener(dirListener);
	}

	private ChangeListener<MinecraftDirectory> dirListener = (observable, oldValue, newValue) ->
	{
		File dir = new File(newValue.getRoot(), path);
		if (!dir.exists()) return;

		IOException ex = null;
		File[] files = dir.listFiles();
		if (files != null && files.length > 0)
			for (File file : files)
				try
				{
					this.importFile(file);
				}
				catch (IOException e)
				{
					if (ex == null) ex = e;
					else ex.addSuppressed(e);
				}
		if (ex != null) ex.printStackTrace();//TODO handle
	};

	public static class Builder<T> implements javafx.util.Builder<ArchiveRepository<T>>
	{
		private String path;
		private File parent;
		private Map<ResourceType, Function<File, T>> parserMap = new EnumMap<>(ResourceType.class);
		private BiSerializer<T, JSONObject> serializer;
		private StorageHandler<T> storageHandler = (StorageHandler<T>) DEFAULT;

		public Builder(File parent, String path, BiSerializer<T, JSONObject> serializer)
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
