package net.launcher.services.curseforge;

import java.util.Date;
import java.util.List;

/**
 * @author ci010
 */
public class CurseForgeProject
{
	private String name, description, projectPath, imageUrl;
	private List<CurseForgeCategory> categories;
	private String authors;

	private String downloadCount;
	private Date lastTime;

	private CurseForgeProjectType projectType;

	public CurseForgeProject(String name, String description, String projectPath, String imageUrl,
					  List<CurseForgeCategory> categories, String authors, String downloadCount,
					  Date lastTime, CurseForgeProjectType projectType)
	{
		this.name = name;
		this.description = description;
		this.projectPath = projectPath;
		this.imageUrl = imageUrl;
		this.categories = categories;
		this.authors = authors;
		this.downloadCount = downloadCount;
		this.lastTime = lastTime;
		this.projectType = projectType;
	}

	public String getName()
	{
		return name;
	}

	public CurseForgeProjectType getProjectType()
	{
		return projectType;
	}

	public String getDescription()
	{
		return description;
	}

	public String getProjectPath()
	{
		return projectPath;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public List<CurseForgeCategory> getCategories()
	{
		return categories;
	}

	public String getAuthor()
	{
		return authors;
	}

	public String getDownloadCount()
	{
		return downloadCount;
	}

	public Date getLastTime()
	{
		return lastTime;
	}
}
