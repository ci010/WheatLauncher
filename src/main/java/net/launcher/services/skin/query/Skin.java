package net.launcher.services.skin.query;

import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;

import java.awt.*;

/**
 * @author ci010
 */
public class Skin
{
	private String name, author, description;
	private Dimension dimension;
	private TextureType type;
	private Texture previewTexture, texture;

	public Skin(String name, String author, String description, Dimension dimension, TextureType type, Texture previewTexture, Texture texture)
	{
		this.name = name;
		this.author = author;
		this.description = description;
		this.dimension = dimension;
		this.type = type;
		this.previewTexture = previewTexture;
		this.texture = texture;
	}

	public TextureType getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public Texture getPreviewTexture() {return previewTexture;}

	public Texture getTexture() {return texture;}

	public String getDescription()
	{
		return description;
	}

	public String getAuthor()
	{
		return author;
	}

	public Dimension size()
	{
		return dimension;
	}
}
