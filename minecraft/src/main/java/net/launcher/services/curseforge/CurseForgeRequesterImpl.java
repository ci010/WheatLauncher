package net.launcher.services.curseforge;

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
	private List<String> gameVersionConstrains;
	private Map<String, CurseForgeCategory> categoryCache;
	private List<CurseForgeCategory> categoriesCacheList;

	private CurseForgeProjectType requestingType;

	CurseForgeRequesterImpl(HttpRequester requester, CurseForgeProjectType requestingType)
	{
		this.requester = requester;
		this.requestingType = requestingType;
	}

	void init() throws IOException
	{
		checkCache(Jsoup.parse(requester.request("GET", buildURL(Option.create(), 1))));
	}

	private synchronized void checkCache(Document document)
	{
		if (filterTypesCache == null && gameVersionsCache == null && categoryCache == null)
		{
			Element filter = document.getElementById("filter-sort");
			filterTypesCache = filter.children().stream().map(e -> e.attr("value")).collect(Collectors.toList());

			gameVersionsCache = document.getElementById("filter-game-version").children().stream().collect
					(Collectors.toMap(Element::val, Element::text));
			gameVersionConstrains = new ArrayList<>(gameVersionsCache.values());
			categoryCache = document.getElementsByClass("level-categories-nav").stream()
					.map(e -> e.child(0))
					.map(e ->
					{
						try {return new CurseForgeCategory(e.attr("href"), e.child(1).text(), e.child(0).attr("src"));}
						catch (Exception ex) {return null;}
					})
					.filter(Objects::nonNull).collect(Collectors.toMap(CurseForgeCategory::getPath, Function.identity()));
			categoriesCacheList = new ArrayList<>(categoryCache.values());
		}
	}

	private String buildURL(Option option, int page)
	{
		Map<String, Object> argumenst = new TreeMap<>();
		String url = root +
				(option.getCategory() != null ? option.getCategory().getPath() : requestingType.getPath());
		if (option.getConstrain() != null)
			argumenst.put("view-game-version", option.getConstrain());
		if (option.getOption() != null)
			argumenst.put("view-sort", option.getOption());
		argumenst.put("page", page);
		return HttpUtils.withUrlArguments(url, argumenst);
	}

	private CurseForgeProjectArtifact parseArtifact(Element element)
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
					nameElement.text(),
					detail.child(3).child(0).text(),
					nameElement.attr("href"),
					item.getElementsByTag("img").get(0).attr("src"),
					detail.child(2).child(0).children().stream().map(e -> e.child(0).attr("href")).map(categoryCache::get).collect(Collectors.toList()),
					infoName.child(1).child(0).text(),
					infoStat.child(0).text(),
					new Date(Long.parseLong(infoStat.child(1).child(0).attr("data-epoch"))), requestingType));
		}
		return projects;
	}

	@Override
	public CurseForgeProjectType getRequestingProjectType()
	{
		return requestingType;
	}

	private CurseForgeProject parseSearchProject(Element e)
	{
		return new CurseForgeProject(
				e.child(1).child(0).child(0).text(),
				e.child(1).child(1).text(),
				e.child(1).child(0).child(0).attr("href"),
				e.child(0).child(0).child(0).attr("src"),
				Collections.emptyList(),
				e.child(2).child(0).text(),
				"-1",
				new Date(Long.parseLong(e.child(3).child(0).attr("data-epoch"))),
				requestingType);
	}

	@Override
	public Cache<CurseForgeProject> search(String keyword) throws IOException
	{
		String url = root + "/search";
		url = HttpUtils.withUrlArguments(url, Collections.singletonMap("search", keyword));
		Document document = Jsoup.parse(requester.request("GET", url));
		if (document.getElementsByClass("tabbed-container").size() == 0) return new Cache<>();
		List<CurseForgeProject> projects = document.getElementsByClass("results").stream().map(this::parseSearchProject).collect
				(Collectors.toList());
		Map<String, Object> context = new TreeMap<>();
		context.put("type", "search");
		context.put("keyword", keyword);
		context.put("page", 1);
		return new Cache<>(projects, context);
	}

	@Override
	public Cache<CurseForgeProject> view(Option option) throws IOException
	{
		if (option == null) option = new Option();
		String url = buildURL(option, 1);
		Document document = Jsoup.parse(requester.request("GET", url));

		checkCache(document);

		Element pages = document.getElementsByClass("paging-list").get(0);
		int page = 1;
		String val = pages.child(pages.children().size() - 1).child(0).attr("href");
		int maxPage = Integer.valueOf(val.substring(val.lastIndexOf("page=") + 5));
		Map<String, Object> context = new TreeMap<>();
		context.put("type", "view");
		context.put("page", page);
		context.put("maxPage", maxPage);
		context.put("option", option);
		return new Cache<>(parseProjects(document), context);
	}


	@Override
	public boolean growCache(Cache<?> cache) throws IOException
	{
		Map<String, Object> context = cache.context;
		if (context.get("type") == null) return false;
		switch (context.get("type").toString())
		{
			case "view":
				Cache<CurseForgeProject> projectCache = (Cache<CurseForgeProject>) cache;
				int page = (int) context.get("page");
				if ((page + 1) > (int) context.get("maxPage")) return false;
				String url = buildURL((Option) context.get("option"), page + 1);
				Document document = Jsoup.parse(requester.request("GET", url));
				projectCache.cache.addAll(parseProjects(document));
				projectCache.context.put("page", page + 1);
				return true;
			case "search":
				projectCache = (Cache<CurseForgeProject>) cache;
				String keyword = context.get("keyword").toString();
				page = (int) context.get("page");
				url = root + "/search";
				Map<String, Object> args = new TreeMap<>();
				args.put("search", keyword);
				args.put("page", page + 1);
				url = HttpUtils.withUrlArguments(url, args);
				document = Jsoup.parse(requester.request("GET", url));
				projectCache.cache.addAll(document.getElementsByClass("results").stream().map(this::parseSearchProject).collect
						(Collectors.toList()));
				projectCache.context.put("page", page + 1);
				return true;
			case "artifact":
				Cache<CurseForgeProjectArtifact> artifactCache = (Cache<CurseForgeProjectArtifact>) cache;
				page = (int) context.get("page");
				if ((page + 1) > (int) context.get("maxPage")) return false;
				String requestURL = context.get("requestURL").toString();
				url = root + requestURL;
				url = HttpUtils.withUrlArguments(url, Collections.singletonMap("page", page + 1));
				artifactCache.cache.addAll(Jsoup.parse(requester.request("GET", url)).getElementsByClass("project-file-list-item")
						.stream().map(this::parseArtifact).collect(Collectors.toList()));
				return true;
			default:
				return false;
		}
	}

	@Override
	public List<String> getGameVersionConstrains()
	{
		return this.gameVersionConstrains;
	}

	@Override
	public List<String> getSortedOptions()
	{
		return filterTypesCache;
	}

	@Override
	public List<CurseForgeCategory> getCategories()
	{
		return categoriesCacheList;
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
}
