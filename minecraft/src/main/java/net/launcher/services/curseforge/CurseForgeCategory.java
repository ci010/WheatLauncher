package net.launcher.services.curseforge;

/**
 * @author ci010
 */
public class CurseForgeCategory
{
	private String path, imgUrl, name;

	public CurseForgeCategory(String path, String name, String imgUrl)
	{
		this.path = path;
		this.imgUrl = imgUrl;
		this.name = name;
	}

	public String getPath()
	{
		return path;
	}

	public String getDefaultName() {return name;}

	public String getImgUrl()
	{
		return imgUrl;
	}

	@Override
	public String toString()
	{
		return "CurseForgeCategory{" +
				"path='" + path + '\'' +
				", imgUrl='" + imgUrl + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
