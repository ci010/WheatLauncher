package net.launcher.utils.resource;

import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;

/**
 * @author ci010
 */
public interface ArchiveRepository<T> extends ReadOnlyArchiveRepository<T>
{
	Task<Resource<T>> importResource(Path file);

	Task<Collection<Resource<T>>> importResources(Path directory);

	/**
	 * Open stream for a path of a resource.
	 *
	 * @param resource The resource.
	 * @param path     The path in the resource.
	 * @return The {@link InputStream} of that resource.
	 */
	InputStream openStream(Resource<T> resource, String path) throws IOException;
}
