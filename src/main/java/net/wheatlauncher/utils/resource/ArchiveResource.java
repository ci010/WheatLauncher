package net.wheatlauncher.utils.resource;

import java.util.Objects;

/**
 * @author ci010
 */
public class ArchiveResource<T>
{
	private ResourceType type;
	private String hash;
	private T containData;
	private String name = "";

	public ArchiveResource(ResourceType type, String hash, T containData)
	{
		this.type = type;
		this.hash = hash;
		this.containData = containData;
	}

	public ArchiveResource<T> setName(String name)
	{
		Objects.requireNonNull(name);
		this.name = name;
		return this;
	}

	public String getName()
	{
		return name;
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

		ArchiveResource<?> that = (ArchiveResource<?>) o;

		return hash.equals(that.hash);
	}

	@Override
	public int hashCode()
	{
		return hash.hashCode();
	}
}
