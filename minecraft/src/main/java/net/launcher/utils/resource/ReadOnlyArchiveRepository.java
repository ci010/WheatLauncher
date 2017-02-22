package net.launcher.utils.resource;

import javafx.collections.ObservableMap;
import javafx.concurrent.Task;

import java.nio.file.Path;
import java.util.Collection;

/**
 * @author ci010
 */
public interface ReadOnlyArchiveRepository<T>
{
	ObservableMap<String, Resource<T>> getResourceMap();

	Task<ArchiveRepository<T>> update();

	Task<Delivery<T>> fetchResource(Path directory, String path, FetchOption option);

	Task<Delivery<T>> fetchAllResource(Path directory, FetchOption option);

	Task<Delivery<T>> fetchAllResource(Path directory, Collection<String> paths, FetchOption option);

}
