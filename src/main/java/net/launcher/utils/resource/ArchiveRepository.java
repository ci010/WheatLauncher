package net.launcher.utils.resource;

import javafx.collections.ObservableMap;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * The repository that store all the resources flatly. It will extract the resource's info and index by its hash.
 * <p>
 * The actual manipulation of the resource will bound by {@link Resource}. So you need to
 * <p>
 * Notice that ALL the string path in this repository is the hash of the file. Do not use regular file path.
 *
 * @author ci010
 * @see Resource
 */
public interface ArchiveRepository<T> extends Repository<ArchiveRepository.Resource<T>>
{
	/**
	 * @return All the hash path of the resources.
	 */
	@Override
	Collection<String> getAllVisiblePaths();

	ObservableMap<String, Resource<T>> getResourceMap();


	/**
	 * Map resource to the callback.
	 *
	 * @param hash     The hash of the resources.
	 * @param callback The callback adapts the resource.
	 */
	void mapResource(String hash, Callback<Resource<T>> callback);

	interface Local<T> extends LocalRepository<Resource<T>>, ArchiveRepository<T>
	{
		/**
		 * Import a single file into the repository.
		 *
		 * @param file     The file actual path.
		 * @param callback The resource callback.
		 */
		Future<Resource<T>> importFile(Path file, Callback<Resource<T>> callback);

		/**
		 * Open stream for a path of a resource.
		 *
		 * @param resource The resource.
		 * @param path     The path in the resource.
		 * @return The {@link InputStream} of that resource.
		 */
		InputStream openStream(Resource<T> resource, String path) throws IOException;
	}

	interface Remote<T>
	{
		/**
		 *
		 * @param proxy
		 * @param hash
		 * @return
		 * @throws Exception
		 */
		String parseToURL(Proxy proxy, String hash) throws Exception;
	}

	class Resource<T>
	{
		private ResourceType type;
		private String hash;
		private T containData;
		private String[] tags;

		public Resource(ResourceType type, String hash, T containData)
		{
			this.type = type;
			this.hash = hash;
			this.containData = containData;
			this.tags = new String[16];
			this.tags[0] = hash;
		}

		public Resource<T> setName(String name)
		{
			Objects.requireNonNull(name);
			tags[0] = name;
			return this;
		}

		public void addTag(String tag)
		{

		}

		public String getName()
		{
			return tags[0];
		}

		public ResourceType getType()
		{
			return type;
		}

		public String getHash()
		{
			return hash;
		}

		public T getContainData()
		{
			return containData;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Resource<?> that = (Resource<?>) o;

			return hash.equals(that.hash);
		}

		@Override
		public int hashCode()
		{
			return hash.hashCode();
		}
	}
}
