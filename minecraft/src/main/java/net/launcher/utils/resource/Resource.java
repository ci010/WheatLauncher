package net.launcher.utils.resource;

import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;

import java.util.Objects;

/**
 * @author ci010
 */
public class Resource<T>
{
	private ResourceType type;
	private String hash;
	private T containData;
	private Object signature;
	private NBTCompound compound;

	public Resource(ResourceType type, String hash, T containData, Object signiture)
	{
		this.type = type;
		this.hash = hash;
		this.containData = containData;
		this.signature = signiture;
		this.compound = NBT.compound();
		this.compound.put("name", hash);
	}

	public Resource(ResourceType type, String hash, T containData, Object signiture, NBTCompound compound)
	{
		this.type = type;
		this.hash = hash;
		this.containData = containData;
		this.signature = signiture;
		this.compound = compound;
	}

	public Resource<T> setName(String name)
	{
		Objects.requireNonNull(name);
		compound.put("name", name);
		return this;
	}

	public NBTCompound getCompound() {return compound;}

	public Object getSignature() {return signature;}

	public String getName()
	{
		NBT name = compound.get("name");
		return name.asString();
	}

	public ResourceType getType() {return type;}

	public String getHash() {return hash;}

	public T getContainData() {return containData;}

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
				'}';
	}

	@Override
	public int hashCode()
	{
		return hash.hashCode();
	}
}
