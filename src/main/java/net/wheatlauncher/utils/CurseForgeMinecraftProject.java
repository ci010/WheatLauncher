package net.wheatlauncher.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class CurseForgeMinecraftProject implements Iterable<CurseForgeMinecraftProject.Artifact>
{
	private static String root = "https://minecraft.curseforge.com";
	private List<Artifact> allInfo;

	public CurseForgeMinecraftProject(List<Artifact> allInfo)
	{
		this.allInfo = allInfo;
	}

	public static CurseForgeMinecraftProject fetchProject(HttpRequester requester, String projectName) throws IOException
	{
		String url = root + "/projects/" + projectName + "/files";
		String content = requester.request("GET", url);
		Document parse = Jsoup.parse(content);

		Elements fileList = parse.getElementsByClass("project-file-list-item");
		if (fileList == null || fileList.size() == 0) return null;

		List<Artifact> allInfo = new ArrayList<>(fileList.size());

		allInfo.addAll(fileList.stream().map(Artifact::new).collect(Collectors.toList()));

		Elements pages = parse.getElementsByClass("listing-footer").get(0).getElementsByClass("b-pagination-item");
		if (pages != null)
		{
			Set<String> herfs = new HashSet<>();
			for (Element page : pages)
				herfs.addAll(page.getElementsByAttribute("href").stream()
						.map(href -> href.attr("href"))
						.collect(Collectors.toList()));
			for (String herf : herfs)
			{
				System.out.println(herf);
				url = root + herf;
				content = requester.request("GET", url);

				parse = Jsoup.parse(content);
				fileList = parse.getElementsByClass("project-file-list-item");
				allInfo.addAll(fileList.stream().map(Artifact::new).collect(Collectors.toList()));
			}
		}
		return new CurseForgeMinecraftProject(allInfo);
	}

	public Artifact getLatest()
	{
		return allInfo.get(0);
	}

	public List<Artifact> getFromMinecraftVersion(String minecraftVersion)
	{
		return allInfo.stream().filter(modInfo -> modInfo.getMcVersion().equals(minecraftVersion))
				.collect(Collectors.toList());
	}

	public Artifact getLatestFromMinecraftVersion(String minecraftVersion)
	{
		return getFromMinecraftVersion(minecraftVersion).get(0);
	}

	@Override
	public Iterator<Artifact> iterator()
	{
		return allInfo.iterator();
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
