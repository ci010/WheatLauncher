package net.launcher.utils.resource;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.net.Proxy;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Async IO resources fetcher;
 *
 * @author ci010
 */
public interface Repository<T>
{
	/**
	 * @return All known path in memory. Notice that if the storage files changed, repository will not check it by
	 * itself.
	 */
	Collection<String> getAllVisiblePaths();

	/**
	 * Check if there is the resource on that path.
	 *
	 * @param path     The path querying.
	 * @param callback The result callback.
	 */
	Future<Boolean> containResource(String path, Callback<Boolean> callback);

	/**
	 * Fetch the resource to directory under the path.
	 *  @param directory The target directory which the new resource will be placed in.
	 * @param path      The resource path.
	 * @param option    The fetch option.
	 */
	Delivery<T> fetchResource(Path directory, String path, FetchOption option);

	Delivery<Void> fetchAllResources(Path directory, Collection<String> paths, FetchOption option);

	/**
	 * Fetch all the resources recorded in memory into the directory.
	 *  @param directory The target directory which the new resource will be placed in.
	 * @param option    The fetch option.
	 */
	Delivery<Void> fetchAllResources(Path directory, FetchOption option);

	/**
	 * Update this repository indexes from the disk.
	 */
	Future<Void> update();

	/**
	 * Check the directory to find if there are some resources should be included into this repository. If any
	 * exception thrown, The exception handler will get the exception.
	 *
	 * @param directory the target directory.
	 * @param handler   The exception handler.
	 */
	void check(Path directory, Consumer<Throwable> handler);

	interface Delivery<T> extends AutoCloseable, Future<T>
	{
		/**
		 * @return The path of the resource delivered this time.
		 */
		Collection<Path> getResourceVirtualPaths();

		/**
		 * Mark the resources fetched by this delivery need to be cleaned out from disk.
		 *
		 * @return If the resource is successfully been marked, return true. If the resource is already released, return
		 * false.
		 * @throws IllegalStateException If the delivery has been closed.
		 */
		boolean markRelease() throws IllegalStateException;
	}

	enum FetchOption
	{
		/**
		 * This will copy the resource file into the target directory.
		 */
		COPY,
		/**
		 * This will newService symbolic link to the target directory. It could be used in some temporary situation.
		 */
		SYMBOL_LINK,
		/**
		 * This will newService hard link to the target directory. Notice that this cannot be created cross hard disk.
		 */
		HARD_LINK
	}

	interface RemoteRepository<T> extends Repository<T>
	{
		/**
		 * Set the proxy for this repository.
		 *
		 * @param proxy The proxy.
		 */
		void setProxy(Proxy proxy);

		/**
		 * @return The proxy of this repostiory.
		 */
		Proxy getProxy();
	}
}
