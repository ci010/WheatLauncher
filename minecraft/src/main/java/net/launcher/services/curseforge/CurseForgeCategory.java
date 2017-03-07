package net.launcher.services.curseforge;

/**
 * @author ci010
 */
public class CurseForgeCategory
{
	private String path, imgUrl, name;

	CurseForgeCategory(String path, String name, String imgUrl)
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
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CurseForgeCategory that = (CurseForgeCategory) o;

		if (path != null ? !path.equals(that.path) : that.path != null) return false;
		if (imgUrl != null ? !imgUrl.equals(that.imgUrl) : that.imgUrl != null) return false;
		return name != null ? name.equals(that.name) : that.name == null;
	}

	@Override
	public int hashCode()
	{
		int result = path != null ? path.hashCode() : 0;
		result = 31 * result + (imgUrl != null ? imgUrl.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
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
