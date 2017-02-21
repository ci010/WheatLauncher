package net.launcher.game;

import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @author ci010
 */
public class ModContainer<META>
{
	private META metadata;
	private ArtifactVersion version;
	private String name, author, description;
	private VersionRange minecraftVersion;

	private ModType<META> modType;

	public static class ModType<META>
	{
		private final String name;
		private final String description;

		public ModType(String name, String description)
		{
			this.name = name;
			this.description = description;
		}
	}

	public ModContainer(META metadata, ArtifactVersion version, String name, String author, String description, VersionRange minecraftVersion)
	{
		this.metadata = metadata;
		this.version = version;
		this.name = name;
		this.author = author;
		this.description = description;
		this.minecraftVersion = minecraftVersion;
	}

	public ArtifactVersion getVersion()
	{
		return version;
	}

	public String getName()
	{
		return name;
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
}
