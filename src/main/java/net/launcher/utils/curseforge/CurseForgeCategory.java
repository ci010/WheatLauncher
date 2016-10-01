package net.launcher.utils.curseforge;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class CurseForgeCategory
{
	private static Map<String, CurseForgeCategory> cached;

	public static CurseForgeCategory createCategory(String name, String imgUrl)
	{
		if (cached == null)
			cached = new TreeMap<>();
		if (cached.containsKey(name)) return cached.get(name);
		CurseForgeCategory category = new CurseForgeCategory(name, imgUrl);
		cached.put(name, category);
		return category;
	}

	public static Optional<CurseForgeCategory> getCategory(String name)
	{
		if (cached == null) return Optional.empty();
		return Optional.ofNullable(cached.get(name));
	}

	private String name, imgUrl;

	private CurseForgeCategory(String name, String imgUrl)
	{
		this.name = name;
		this.imgUrl = imgUrl;
	}

	public String getName()
	{
		return name;
	}

	public String getImgUrl()
	{
		return imgUrl;
	}
}
