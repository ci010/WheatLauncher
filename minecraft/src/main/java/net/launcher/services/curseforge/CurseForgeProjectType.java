package net.launcher.services.curseforge;

/**
 * @author ci010
 */
public enum CurseForgeProjectType
{
	Modpacks("modpacks"),
	Customization("customization"),
	Addons("mc-addons"),
	Mods("mc-mods"),
	TexturePacks("texture-packs"),
	Worlds("worlds");

	private String path, id;

	CurseForgeProjectType(String id)
	{
		this.id = id;
		this.path = "/" + id;
	}

	public String getPath()
	{
		return path;
	}

	public String getId()
	{return id;}
}
