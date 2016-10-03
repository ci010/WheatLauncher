package net.launcher.services.skinme;

import java.util.Date;

/**
 * @author ci010
 */
public class SkinMeSkin
{
	private String id, name, author, srcUrl;
	private int width, height;
	private Date uploadDate;

	public SkinMeSkin(String id, String name, String author, String srcUrl, int width, int height, Date uploadDate)
	{
		this.id = id;
		this.name = name;
		this.author = author;
		this.srcUrl = srcUrl;
		this.width = width;
		this.height = height;
		this.uploadDate = uploadDate;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getAuthor()
	{
		return author;
	}

	public String getSrcUrl()
	{
		return srcUrl;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public Date getUploadDate()
	{
		return uploadDate;
	}
}
