package net.launcher.utils.resource;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

/**
 * @author ci010
 */
public abstract class RemoteArchiveRepository<T> extends ArchiveRepositoryBase<T>
{
	protected abstract URL resolve(String path);

	@Override
	public Task<ArchiveRepository<T>> update()
	{
		return null;
	}

	@Override
	protected Resource<T> fetch(Path dir, String path, FetchOption option) throws IOException
	{
		return null;
	}
}
