package net.launcher.game;

import net.launcher.game.text.TextComponent;

import java.nio.file.Path;

/**
 * @author ci010
 */
public class ResourcePack
{
	private String packName;
	private TextComponent description;
	private int format;

	public ResourcePack(String packName, TextComponent description, int format)
	{
		this.packName = packName;
		this.description = description;
		this.format = format;
	}

	public String getPackName()
	{
		return packName;
	}

	public int packFormat()
	{
		return format;
	}

	public TextComponent getDescription()
	{
		return description;
	}

	public static Path getImagePath(Path resourcePackPath) {return resourcePackPath.resolve("pack.png");}

	@Override
	public String toString()
	{
		return "ResourcePack{" +
				"packName='" + packName + '\'' +
				", description='" + description + '\'' +
				", format=" + format +
				'}';
	}
}
