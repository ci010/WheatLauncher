package net.launcher.services.curseforge;

/**
 * @author ci010
 */
public enum CurseForgeProjectType
{
	Modpacks("/modpacks"),
	Customization("/customization"),
	Addons("/mc-addons"),
	Mods("/mc-mods"),
	TexturePacks("/texture-packs"),
	Worlds("/worlds");

	private String path;

	CurseForgeProjectType(String path)
	{
		this.path = path;
	}

	public String getPath()
	{
		return path;
	}
}
