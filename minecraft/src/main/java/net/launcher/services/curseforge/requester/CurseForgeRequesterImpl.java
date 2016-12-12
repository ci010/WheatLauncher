package net.launcher.services.curseforge.requester;

import net.launcher.services.curseforge.CurseForgeCategory;
import net.launcher.services.curseforge.CurseForgeProject;
import net.launcher.services.curseforge.CurseForgeProjectArtifact;
import net.launcher.services.curseforge.CurseForgeProjectType;
import net.launcher.utils.DataSizeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class CurseForgeRequesterImpl implements CurseForgeService
{
	private static String root = "https://minecraft.curseforge.com";
	private HttpRequester requester;

	private List<String> filterTypesCache;
	private Map<String, String> gameVersionsCache;
	private Map<String, CurseForgeCategory> categoryCache;
	private List<CurseForgeCategory> categoriesCacheList;

	private CurseForgeProjectType requestingType;

	CurseForgeRequesterImpl(HttpRequester requester, CurseForgeProjectType requestingType)
	{
		this.requester = requester;
		this.requestingType = requestingType;
	}

	private synchronized void checkCache(Document document)
	{
		if (filterTypesCache == null && gameVersionsCache == null && categoryCache == null)
		{
			Element filter = document.getElementById("filter-sort");
			filterTypesCache = filter.children().stream().map(e -> e.attr("value")).collect(Collectors.toList());
			gameVersionsCache = document.getElementById("filter-game-version").children().stream().
					collect(Collectors.toMap(Element::val, v -> v.attr("value")));
			categoryCache = document.getElementsByClass("level-categories-nav  ").stream().map(e -> e.child(0)).map(e ->
					new CurseForgeCategory(e.attr("href"), e.child(1).val(), e.child(0).attr("src"))).collect(
					Collectors.toMap(CurseForgeCategory::getPath, Function.identity()));
			categoriesCacheList = new ArrayList<>(categoryCache.values());
		}
	}

	@Override
	public CurseForgeProjectType getRequestingProjectType()
	{
		return requestingType;
	}

	@Override
	public ViewSession viewSession() throws IOException
	{
		Document document = Jsoup.parse(requester.request("GET", requestingType.getPath()));

		checkCache(document);

		Element pages = document.getElementsByClass("paging-list").get(0);
		int page = 1;
		int maxPage = Integer.valueOf(pages.child(pages.children().size() - 2).val());

		return new CurseForgeProjectsViewSessionImpl(parseProjects(document), filterTypesCache, "", gameVersionsCache, "", page,
				maxPage, categoriesCacheList, null);
	}

	private String buildURL(ViewSession session)
	{
		Map<String, Object> argumenst = new TreeMap<>();
		String url = root + requestingType.getPath() + session.getCategory().getPath();
		argumenst.put("filter-game-version", session.getGameVersionConstrain());
		argumenst.put("filter-sort", session.getSortOption());
		argumenst.put("page", session.getPage());
		return HttpUtils.withUrlArguments(url, argumenst);
	}

	private List<CurseForgeProject> parseProjects(Document document)
	{
		Elements projectItems = document.getElementsByClass("project-list-item");
		List<CurseForgeProject> projects = new ArrayList<>(projectItems.size());
		for (Element item : projectItems)
		{
			Element detail = item.child(1);
			Element infoName = detail.child(0);
			Element infoStat = detail.child(1);
			Element nameElement = infoName.child(0).child(0);
			projects.add(new CurseForgeProject(
					nameElement.val(),
					detail.child(3).child(0).val(),
					nameElement.attr("href"),
					item.child(0).child(0).child(0).attr("src"),
					detail.child(2).child(0).children().stream().map(e -> e.child(0).attr("href")).map(categoryCache::get).collect(Collectors.toList()),
					infoName.child(1).child(0).val(), infoStat.child(0).val(),
					new Date(Long.parseLong(infoStat.child(1).attr("data-epoch"))), requestingType));
		}
		return projects;
	}

	@Override
	public ViewSession refresh(ViewSession session) throws IOException
	{
		((CurseForgeProjectsViewSessionImpl) session).projects.addAll(parseProjects(Jsoup.parse(requester.request("GET", buildURL(session)))));
		return session;
	}

	@Override
	public String toString()
	{
		return "CurseForgeRequesterImpl{" +
				"requester=" + requester +
				", requestPath='" + requestingType + '\'' +
				", filterTypesCache=" + filterTypesCache +
				", gameVersionsCache=" + gameVersionsCache +
				", categoryCache=" + categoryCache +
				", categoriesCacheList=" + categoriesCacheList +
				'}';
	}


	@Override
	public ArtifactCache cacheArtifact(CurseForgeProject project) throws IOException
	{
		String url = root + project.getProjectPath() + "/file";
		Document document = Jsoup.parse(requester.request("GET", url));
		if (document.getElementsByClass("no-nav").size() != 0) return null;

		return new CurseForgeProjectsViewSessionImpl.ArtifactCache(document.getElementsByClass("project-file-list-item")
				.stream().map(this::parse).collect(Collectors.toList()), 1, url);
	}

	@Override
	public SearchCache search(String keyword) throws IOException
	{
		String url = root + "/search";
		url = HttpUtils.withUrlArguments(url, Collections.singletonMap("search", keyword));
		Document document = Jsoup.parse(requester.request("GET", url));
		if (document.getElementsByClass("no-nav").size() != 0) return new CurseForgeSearchCacheImpl();
		List<CurseForgeProject> projects = document.getElementsByClass("results").stream().map(e ->
		{
			Element summery = e.child(1).child(1);
			String description = "";
			for (Element element : summery.children())
				description += element.val();
			return new CurseForgeProject(e.child(1).child(0).child(0).val(),
					description,
					e.child(1).child(0).child(0).attr("href"),
					e.child(0).child(0).child(0).attr("src"),
					Collections.emptyList(),
					e.child(2).child(0).val(),
					"-1",
					new Date(Long.parseLong(e.child(3).child(0).attr("data-epoch"))),
					requestingType
			);
		}).collect(Collectors.toList());
		new CurseForgeSearchCacheImpl(true, projects, new ArrayList<>());

		return null;
	}

	@Override
	public boolean growSearchCache(SearchCache session) throws IOException
	{
		return false;
	}


	@Override
	public boolean growArtifactCache(ArtifactCache cache) throws IOException
	{
		Objects.requireNonNull(cache);
		if (cache.getCachedSegment() == cache.getTotalSegment())
			return false;
		String url = root + ((CurseForgeProjectsViewSessionImpl.ArtifactCache) cache).getRequestingURL();
		url = HttpUtils.withUrlArguments(url, Collections.singletonMap("page", cache.getCachedSegment() + 1));
		((CurseForgeProjectsViewSessionImpl.ArtifactCache) cache).artifacts().addAll(Jsoup.parse(requester.request("GET", url)).getElementsByClass("project-file-list-item")
				.stream().map(this::parse).collect(Collectors.toList()));
		return true;
	}

	private CurseForgeProjectArtifact parse(Element element)
	{
		String releaseType = element.getElementsByClass("tip").get(0).attr("title");
		Element tip = element.getElementsByClass("overflow-tip").get(0);
		String mcVersion = element.getElementsByClass("version-label").get(0).text();
		String downloadURL = "https://minecraft.curseforge.com" + tip.attr("href") + "/download";
		String fileName = tip.text();
		String size = element.getElementsByClass("project-file-size").get(0).text();
		DataSizeUnit unit = DataSizeUnit.of(size);
		long fileSize;
		if (unit == null) fileSize = 0;
		else fileSize = unit.toByte(unit.fromString(size));
		Date date = null;
		try
		{
			date = new Date(Long.parseLong(element.getElementsByAttribute("data-epoch").get(0).attr("data-epoch")));
		}
		catch (Exception ignored) {}
		return new CurseForgeProjectArtifact(downloadURL, fileName, mcVersion, releaseType, fileSize, date);
	}

	private String replace(String url)
	{
		return url.replaceAll(":", "%3A");
	}


	//	public CurseForgeProject requestProject(String projectPath) throws IOException
//	{
//		String url = root + "/projects/" + projectPath;
//
//		String content = requester.request("GET", url);
//		Document parse = Jsoup.parse(content);
//
//		if (parse.getElementsByClass("no-nav").size() != 0) return null;
//
//		String name = parse.getElementsByClass("project-title").get(0).getElementsByClass("overflow-tip").get(0).text();
//		String imageUrl = parse.getElementsByClass("e-avatar64").get(0).attr("href");
//		List<String> authors = new ArrayList<>();
//		List<CurseForgeCategory> categories = new ArrayList<>();
//
//		parse.getElementsByClass("project-members").forEach(element ->
//		{
//			Element avatar = element.getElementsByClass("avatar").get(0).child(0);
//			String href = avatar.attr("href");
////			authors.add(new CurseForgeUser(href.substring(href.lastIndexOf('/')), avatar.child(0).attr("src")));
//		});
//
//		parse.getElementsByClass("project-categories").get(0).getElementsByClass("e-avatar32").forEach(element ->
//		{
//			String type = element.attr("href");
//			type = type.substring(type.lastIndexOf('/'));
//			categories.add(CurseForgeCategory.getCreateCategory(type, element.child(0).attr("src")));
//		});
//
//		url = root + "/projects/" + projectPath + "/files";
//		content = requester.request("GET", url);
//		parse = Jsoup.parse(content);
//
//		Elements fileList = parse.getElementsByClass("project-file-list-item");
//		if (fileList == null || fileList.size() == 0) return new CurseForgeProject();
//
//		List<CurseForgeProject.CurseForgeProjectArtifact> allInfo = new ArrayList<>(fileList.size());
//
//		allInfo.addAll(fileList.stream().map(CurseForgeProject.CurseForgeProjectArtifact::new).collect(Collectors.toList()));
//
//		CurseForgeProject project = new CurseForgeProject(name, "", projectPath, imageUrl, categories, authors, allInfo);
//
//		Elements pages = parse.getElementsByClass("listing-footer").get(0).getElementsByClass("b-pagination-item");
//		if (pages != null)
//		{
//			Set<String> herfs = new HashSet<>();
//			pages.forEach(page ->
//					herfs.addAll(page.getElementsByAttribute("href").stream()
//							.map(href -> href.attr("href"))
//							.collect(Collectors.toList())));
//			project.cacheRemainPage(herfs);
//		}
//		return project;
//	}
}
