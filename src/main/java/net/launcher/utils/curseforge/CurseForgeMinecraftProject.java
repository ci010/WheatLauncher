package net.launcher.utils.curseforge;

import net.launcher.utils.DataSizeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CombinedDownloadCallback;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class CurseForgeMinecraftProject implements Iterable<CurseForgeMinecraftProject.Artifact>
{
	private static String root = "https://minecraft.curseforge.com";

	private String name, description, projectPath, imageUrl;
	private List<CurseForgeCategory> categories;
	private List<CurseForgeUser> authors;

	private List<Artifact> artifacts;

	public CurseForgeMinecraftProject(String name, String description, String projectPath, String imageUrl, List<CurseForgeCategory> categories, List<CurseForgeUser> authors, List<Artifact> artifacts)
	{
		this.name = name;
		this.description = description;
		this.projectPath = projectPath;
		this.imageUrl = imageUrl;
		this.categories = categories;
		this.authors = authors;
		this.artifacts = artifacts;
	}

	public String getName()
	{
		return name;
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

	public List<CurseForgeUser> getAuthors()
	{
		return authors;
	}

	public void fetchAllArtifact(HttpRequester requester, CombinedDownloadCallback<List<Artifact>> callback) throws IOException
	{
		if (hrefs != null)
			for (String herf : hrefs)
			{
				String url = root + herf;
				String content = requester.request("GET", url);

				Document parse = Jsoup.parse(content);
				Elements fileList = parse.getElementsByClass("project-file-list-item");
				this.artifacts.addAll(fileList.stream().map(Artifact::new).collect(Collectors.toList()));
			}
	}

	private Set<String> hrefs;

	private CurseForgeMinecraftProject cacheRemainPage(Set<String> href)
	{
		this.hrefs = href;
		return this;
	}

	private CurseForgeMinecraftProject(List<Artifact> allInfo)
	{
		this.artifacts = allInfo;
	}

	public static CurseForgeMinecraftProject fetchProject(HttpRequester requester, String projectPath) throws IOException
	{
		String url = root + "/projects/" + projectPath;

		String content = requester.request("GET", url);
		Document parse = Jsoup.parse(content);

		if (parse.getElementsByClass("no-nav").size() != 0) return null;

		String name = parse.getElementsByClass("project-title").get(0).getElementsByClass("overflow-tip").get(0).text();
		String imageUrl = parse.getElementsByClass("e-avatar64").get(0).attr("href");
		List<CurseForgeUser> authors = new ArrayList<>();
		List<CurseForgeCategory> categories = new ArrayList<>();

		parse.getElementsByClass("project-members").forEach(element ->
		{
			Element avatar = element.getElementsByClass("avatar").get(0).child(0);
			String href = avatar.attr("href");
			authors.add(new CurseForgeUser(href.substring(href.lastIndexOf('/')), avatar.child(0).attr("src")));
		});

		parse.getElementsByClass("project-categories").get(0).getElementsByClass("e-avatar32").forEach(element ->
		{
			String type = element.attr("href");
			type = type.substring(type.lastIndexOf('/'));
			categories.add(CurseForgeCategory.createCategory(type, element.child(0).attr("src")));
		});

		url = root + "/projects/" + projectPath + "/files";
		content = requester.request("GET", url);
		parse = Jsoup.parse(content);

		Elements fileList = parse.getElementsByClass("project-file-list-item");
		if (fileList == null || fileList.size() == 0) return new CurseForgeMinecraftProject(Collections.emptyList());

		List<Artifact> allInfo = new ArrayList<>(fileList.size());

		allInfo.addAll(fileList.stream().map(Artifact::new).collect(Collectors.toList()));

		CurseForgeMinecraftProject project = new CurseForgeMinecraftProject(name, "", projectPath, imageUrl, categories, authors, allInfo);

		Elements pages = parse.getElementsByClass("listing-footer").get(0).getElementsByClass("b-pagination-item");
		if (pages != null)
		{
			Set<String> herfs = new HashSet<>();
			pages.forEach(page ->
					herfs.addAll(page.getElementsByAttribute("href").stream()
							.map(href -> href.attr("href"))
							.collect(Collectors.toList())));
			project.cacheRemainPage(herfs);
		}
		return project;
	}

	public boolean isEmpty()
	{
		return getArtifactSize() == 0;
	}

	public List<Artifact> getAllArtifact()
	{
		return artifacts;
	}

	public int getArtifactSize()
	{
		return artifacts.size();
	}

	public Artifact getLatestAritifact()
	{
		return artifacts.get(0);
	}

	public List<Artifact> getFromMinecraftVersion(String minecraftVersion)
	{
		return artifacts.stream().filter(modInfo -> modInfo.getMcVersion().equals(minecraftVersion))
				.collect(Collectors.toList());
	}

	public Artifact getLatestFromMinecraftVersion(String minecraftVersion)
	{
		return getFromMinecraftVersion(minecraftVersion).get(0);
	}

	@Override
	public Iterator<Artifact> iterator()
	{
		return artifacts.iterator();
	}

	public static class Artifact
	{
		private String downloadURL;
		private String fileName;
		private String mcVersion;
		private String releaseType;
		private long fileSize;
		private Date date;

		public Artifact(Element element)
		{
			releaseType = element.getElementsByClass("tip").get(0).attr("title");
			Element tip = element.getElementsByClass("overflow-tip").get(0);
			mcVersion = element.getElementsByClass("version-label").get(0).text();
			downloadURL = "https://minecraft.curseforge.com" + tip.attr("href") + "/download";
			fileName = tip.text();
			String size = element.getElementsByClass("project-file-size").get(0).text();
			DataSizeUnit unit = DataSizeUnit.of(size);
			if (unit == null) fileSize = 0;
			else this.fileSize = unit.toByte(unit.fromString(size));
			try
			{
				this.date = new Date(Long.parseLong(element.getElementsByAttribute("data-epoch").get(0).attr("data-epoch")));
			}
			catch (Exception ignored) {}
		}

		public String getDownloadURL()
		{
			return downloadURL;
		}

		public String getFileName()
		{
			return fileName;
		}

		public String getMcVersion()
		{
			return mcVersion;
		}

		public String getReleaseType()
		{
			return releaseType;
		}

		public long getFileSize()
		{
			return fileSize;
		}

		public Optional<Date> getDate()
		{
			return Optional.ofNullable(date);
		}

		@Override
		public String toString()
		{
			return "Artifact{" +
					"downloadURL='" + downloadURL + '\'' +
					", fileName='" + fileName + '\'' +
					", mcVersion='" + mcVersion + '\'' +
					", releaseType='" + releaseType + '\'' +
					", fileSize=" + fileSize +
					", date=" + date +
					'}';
		}
	}
}
