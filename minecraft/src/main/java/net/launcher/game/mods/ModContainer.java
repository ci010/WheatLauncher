package net.launcher.game.mods;

import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @author ci010
 */
public class ModContainer<META>
{
	private META metadata;
	private ArtifactVersion version;
	private String id, author, description;
	private VersionRange minecraftVersion;

	private final String modType;

	public ModContainer(META metadata, ArtifactVersion version, String id, String author, String description, VersionRange minecraftVersion, String modType)
	{
		this.metadata = metadata;
		this.version = version;
		this.id = id;
		this.author = author;
		this.description = description;
		this.minecraftVersion = minecraftVersion;
		this.modType = modType;
	}

	public ArtifactVersion getVersion()
	{
		return version;
	}

	public String getId()
	{
		return id;
	}

	public String getAuthor()
	{
		return author;
	}

	public String getDescription()
	{
		return description;
	}

	public VersionRange getMinecraftVersion()
	{
		return minecraftVersion;
	}

	public META getMetaData()
	{
		return metadata;
	}

	public String getModType() {return modType;}
}
