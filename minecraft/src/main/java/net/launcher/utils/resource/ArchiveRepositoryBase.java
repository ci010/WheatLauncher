package net.launcher.utils.resource;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author ci010
 */
public abstract class ArchiveRepositoryBase<T> implements ReadOnlyArchiveRepository<T>
{
	protected ObservableMap<String, Resource<T>> archesMap = FXCollections.synchronizedObservableMap(FXCollections
			.observableHashMap());
	private ObservableMap<String, Resource<T>> view = FXCollections.unmodifiableObservableMap(this.archesMap);

	protected abstract Resource<T> fetch(Path dir, String path, FetchOption option) throws IOException;

	@Override
	public ObservableMap<String, Resource<T>> getResourceMap()
	{
		return view;
	}

	@Override
	public Task<Delivery<T>> fetchResource(Path directory, String path, FetchOption option)
	{
		Objects.requireNonNull(directory);
		Objects.requireNonNull(path);
		return new Task<Delivery<T>>()
		{
			@Override
			protected Delivery<T> call() throws Exception
			{
				Resource<T> fetch = fetch(directory, path, option);
				if (fetch == null)
					throw new IOException("No resource", new FileNotFoundException("No resource in " + path));

				return new Delivery<>(Collections.singleton(fetch.getContainData()),
						Collections.singleton(directory.resolve(path + fetch.getType().getSuffix())));
			}
		};
	}

	@Override
	public Task<Delivery<T>> fetchAllResource(Path directory, FetchOption option)
	{
		return fetchAllResource(directory, archesMap.keySet(), option);
	}

	@Override
	public Task<Delivery<T>> fetchAllResource(Path directory, Collection<String> paths, FetchOption option)
	{
		Objects.requireNonNull(directory);
		return new Task<Delivery<T>>()
		{
			@Override
			protected Delivery<T> call() throws Exception
			{
				List<Path> virtualPath = new ArrayList<>(paths.size());
				List<T> values = new ArrayList<>(paths.size());
				updateProgress(0, paths.size());
				int i = 0;
				for (String key : paths)
				{
					Resource<T> resource = null;
					try {resource = fetch(directory, key, option);}
					catch (Exception e)
					{
						if (getException() != null) setException(new IOException("corrupted file " + key));
						else getException().addSuppressed(new IOException("corrupted file " + key));
					}
					if (resource == null)
					{
						if (getException() != null) setException(new IOException("corrupted file " + key));
						else getException().addSuppressed(new IOException("corrupted file " + key));
						continue;
					}
					values.add(resource.getContainData());
					virtualPath.add(directory.resolve(resource.getHash().concat(resource.getType().getSuffix())));
					updateProgress(i++, paths.size());
				}
				if (getException() != null) throw (Exception) getException();
				return new Delivery<>(values, virtualPath);
			}
		};
	}
}
