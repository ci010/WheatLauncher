package net.launcher.io;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public final class SourceObject implements Comparable<SourceObject>
{
	private String path;
	private Prototype prototype;

	private SourceObject(String path, Prototype prototype)
	{
		this.path = path;
		this.prototype = prototype;
	}

	public Prototype getPrototype()
	{
		return prototype;
	}

	public String getPath()
	{
		return path;
	}

	public MappedStorageType getType()
	{
		return prototype.type;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SourceObject that = (SourceObject) o;

		return path != null ? path.equals(that.path) : that.path == null;
	}

	@Override
	public int hashCode()
	{
		return path != null ? path.hashCode() : 0;
	}

	@Override
	public int compareTo(SourceObject o)
	{
		return path.compareTo(o.path);
	}

	public static class Prototype
	{
		private String fileName;
		private MappedStorageType type;

		public Prototype(String filename, MappedStorageType type)
		{
			this.fileName = filename;
			this.type = type;
		}

		public String getFileName()
		{
			return fileName;
		}

		public SourceObject create(String... path)
		{
			String realPath = fileName;
			if (path != null && path.length != 0)
				realPath = Arrays.stream(path).collect(Collectors.joining(File.separator, "",
						File.separator.concat(fileName)));
			return new SourceObject(realPath, this);
		}

		public boolean exists(File root, String path)
		{
			return new File(root, path + "/" + fileName).isFile();
		}

		public Optional<SourceObject> createIfExist(File root, String... path)
		{
			String realPath = fileName;
			if (path != null && path.length != 0)
				realPath = Arrays.stream(path).collect(Collectors.joining(File.separator, "",
						File.separator.concat(fileName)));
			if (new File(root, realPath).isFile())
				return Optional.of(new SourceObject(realPath, this));
			return Optional.empty();
		}

		public boolean isTypeOf(SourceObject object)
		{
			return object.getPrototype() == this;
		}
	}
}
