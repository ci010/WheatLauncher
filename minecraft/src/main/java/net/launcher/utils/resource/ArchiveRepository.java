package net.launcher.utils.resource;

import javafx.collections.ObservableMap;
import net.launcher.utils.ProgressCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.nio.file.Path;
import java.util.Arrays;
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
	 * Import a single file into the repository.
	 * <p>Progress message:
	 * <ul>
	 * start->checking->copying->resolving->saving
	 * </ul>
	 * <p>Or:
	 * <ul>start->exist</ul>
	 *
	 * @param file     The file actual path.
	 * @param callback The resource callback.
	 */
	Future<Resource<T>> importFile(Path file, ProgressCallback<Resource<T>> callback);

	/**
	 * Open stream for a path of a resource.
	 *
	 * @param resource The resource.
	 * @param path     The path in the resource.
	 * @return The {@link InputStream} of that resource.
	 */
	InputStream openStream(Resource<T> resource, String path) throws IOException;

	interface Remote
	{
		/**
		 * @param proxy
		 * @param hash
		 * @return
		 * @throws Exception
		 */
		String parseToURL(Proxy proxy, String hash) throws IOException;
	}

	class Resource<T>
	{
		private ResourceType type;
		private String hash;
		private T containData;
		private Object signature;
		private String[] tags;

		public Resource(ResourceType type, String hash, T containData)
		{
			this.type = type;
			this.hash = hash;
			this.containData = containData;
			this.tags = new String[16];
			this.tags[0] = hash;
		}

		public Resource(ResourceType type, String hash, T containData, Object signiture)
		{
			this.type = type;
			this.hash = hash;
			this.containData = containData;
			this.signature = signiture;
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

		public Object getSignature()
		{
			return signature;
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
		public String toString()
		{
			return "Resource{" +
					"type=" + type +
					", hash='" + hash + '\'' +
					", containData=" + containData +
					", signature=" + signature +
					", tags=" + Arrays.toString(tags) +
					'}';
		}

		@Override
		public int hashCode()
		{
			return hash.hashCode();
		}
	}
}
