package net.launcher.services.curseforge;

import java.util.List;

/**
 * @author ci010
 */
public class CurseForgeUser
{
	private String name;
	private String imageUrl;
	private List<CurseForgeMinecraftProject> projectCache;

	public CurseForgeUser(String name, String imageUrl)
	{
		this.name = name;
		this.imageUrl = imageUrl;
	}

	public String getName()
	{
		return name;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

}
