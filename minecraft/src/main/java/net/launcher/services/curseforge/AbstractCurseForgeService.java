package net.launcher.services.curseforge;

import net.launcher.utils.DataSizeUnit;
import net.launcher.utils.URLDecode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public abstract class AbstractCurseForgeService implements CurseForgeService
{
	private static String root = "https://minecraft.curseforge.com";

	private Map<CurseForgeProjectType, Container> containerMap = new EnumMap<>(CurseForgeProjectType.class);

	private class Container
	{
		private List<String> filterTypesCache;
		private List<VersionCode> gameVersionConstrains;
		private Map<String, CurseForgeCategory> categoryCache;
		private List<CurseForgeCategory> categoriesCacheList;
	}

	private CurseForgeProjectType requestingType;

	private CurseForgeCategory getCategory(String s)
	{
		return getContainer().categoryCache.get(s);
	}

	private Container getContainer()
	{
		return containerMap.get(requestingType);
	}

	protected AbstractCurseForgeService(CurseForgeProjectType requestingType)
	{
		this.requestingType = requestingType;
	}

	@Override
	public synchronized void setRequestingProjectType(CurseForgeProjectType type) throws IOException
	{
		this.requestingType = type;
		Container container = containerMap.get(type);
		if (container == null)
		{
			String url = buildURL(Option.create(), 1);
			Document request = request(url);
			container = parseContainer(request);
			containerMap.put(type, container);
		}
	}

	private Container parseContainer(Document document)
	{
		Container container = new Container();
		Element filter = document.getElementById("filter-sort");
		container.filterTypesCache = filter.children().stream().map(e -> e.attr("value")).collect(Collectors.toList());
		container.gameVersionConstrains = document.getElementById("filter-game-version").children().stream().map
				(element -> new VersionCode(element.text(), element.val())).collect(Collectors.toList());
		container.categoriesCacheList =
				document.getElementsByClass("level-categories-nav").stream()
						.map(e -> e.child(0))
						.map(e ->
						{
							try
							{
								return new CurseForgeCategory(e.attr("href"), e.child(1).text(),
										e.child(0).attr("src"));
							}
							catch (Exception ex) {return null;}
						})
						.filter(Objects::nonNull).collect(Collectors.toList());
		container.categoryCache = container.categoriesCacheList.stream().collect(Collectors.toMap
				(CurseForgeCategory::getPath, Function.identity()));
		return container;
	}

//	private synchronized void checkCache(Document document)
//	{
//		if (filterTypesCache == null && gameVersionConstrains == null && categoryCache == null)
//		{
//			Element filter = document.getElementById("filter-sort");
//			filterTypesCache = filter.children().stream().map(e -> e.attr("value")).collect(Collectors.toList());
//
//			gameVersionConstrains = document.getElementById("filter-game-version").children().stream().map(element ->
//					new VersionCode(element.text(), element.val())).collect(Collectors.toList());
//
//			categoriesCacheList =
//					document.getElementsByClass("level-categories-nav").stream()
//							.map(e -> e.child(0))
//							.map(e ->
//							{
//								try
//								{
//									return new CurseForgeCategory(e.attr("href"), e.child(1).text(),
//											e.child(0).attr("src"));
//								}
//								catch (Exception ex) {return null;}
//							})
//							.filter(Objects::nonNull).collect(Collectors.toList());
//			categoryCache = categoriesCacheList.stream().collect(Collectors.toMap(CurseForgeCategory::getPath, Function
//					.identity()));
//		}
//	}

	private String buildURL(Option option, int page)
	{
		Map<String, Object> argumenst = new TreeMap<>();
		String url = root +
				(option.getCategory() != null ? option.getCategory().getPath() : requestingType.getPath());
		if (option.getConstrain() != null)
			argumenst.put("filter-game-version", option.getConstrain().getCode());
		if (option.getOption() != null)
			argumenst.put("filter-sort", option.getOption());
		argumenst.put("page", page);
		return HttpUtils.withUrlArguments(url, argumenst);
	}

	//parse Element class=project-file-listStr-item
	private CurseForgeProjectArtifact parseArtifact(Element element)
	{
		String releaseType = element.getElementsByClass("tip").get(0).attr("title");
		Element tip = element.getElementsByClass("overflow-tip").get(0);
		String mcVersion = element.getElementsByClass("version-label").get(0).text();
		String downloadURL = "https://minecraft.curseforge.com" + tip.attr("href") + "/download";
		String fileName = tip.text();
		String size = element.getElementsByClass("project-file-size").get(0).text();
		DataSizeUnit unit = DataSizeUnit.of(size);
		size = size.replace(",", "");
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
					detail.child(2).child(0).children().stream().map(e -> e.child(0).attr("href")).map(this::getCategory).collect(Collectors.toList()),
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
		String path = e.child(1).child(0).child(0).attr("href");
		if (!path.contains(requestingType.getId())) return null;
		return new CurseForgeProject(
				e.child(1).child(0).child(0).text(),
				e.child(1).child(1).text(),
				path,
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
		Document document = request(url);
		if (document.getElementsByClass("tabbed-container").size() == 0) return new Cache<>();
		List<CurseForgeProject> projects = document.getElementsByClass("results").stream().map(this::parseSearchProject)
				.filter(Objects::nonNull).collect(Collectors.toList());
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
		Document document = request(url);

		Element pages = document.getElementsByClass("paging-list").get(0);
		int page = 1;
		String val = pages.child(pages.children().size() - 1).child(0).attr("href");
		int maxPage = Integer.valueOf(URLDecode.decode(val).get("page"));
		Map<String, Object> context = new TreeMap<>();
		context.put("type", "view");
		context.put("page", page);
		context.put("maxPage", maxPage);
		context.put("option", option);
		return new Cache<>(parseProjects(document), context);
	}

	@Override
	public Cache<CurseForgeProjectArtifact> artifact(CurseForgeProject project) throws IOException
	{
		Objects.requireNonNull(project);

		String files = project.getProjectPath() + "/files";
		int page = 1;
		int maxPage = 1;
		Document doc = request(root + files);
		Elements pages = doc.getElementsByClass("b-pagination-item");
		List<String> collect = pages.stream().map(Element::text).collect(Collectors.toList());
		for (String s : collect)
		{
			try
			{
				Integer v = Integer.valueOf(s);
				if (maxPage < v)
					maxPage = v;
			}
			catch (Exception e) {}
		}
		Elements elementsByClass = doc.getElementsByClass("project-file-list-item");
		Map<String, Object> context = new TreeMap<>();

		context.put("page", page);
		context.put("maxPage", maxPage);
		context.put("requestURL", files);

		return new Cache<>(elementsByClass.stream().map(this::parseArtifact).collect(Collectors.toList()), context);
	}


	@Override
	public boolean growCache(Cache<?> cache) throws IOException
	{
		Map<String, Object> context = cache.context;
		if (context.get("type") == null) return false;
		cache.cache.clear();
		switch (context.get("type").toString())
		{
			case "view":
				Cache<CurseForgeProject> projectCache = (Cache<CurseForgeProject>) cache;
				int page = (int) context.get("page");
				if ((page + 1) > (int) context.get("maxPage")) return false;
				String url = buildURL((Option) context.get("option"), page + 1);
				Document document = request(url);
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
				document = request(url);
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
				Document request = request(url);
				artifactCache.cache.addAll(request.getElementsByClass("project-file-listStr-item")
						.stream().map(this::parseArtifact).collect(Collectors.toList()));
				return true;
			default:
				return false;
		}
	}

	@Override
	public List<VersionCode> getGameVersionConstrains()
	{
		return getContainer().gameVersionConstrains;
	}

	@Override
	public List<String> getSortedOptions()
	{
		return getContainer().filterTypesCache;
	}

	@Override
	public List<CurseForgeCategory> getCategories()
	{
		return getContainer().categoriesCacheList;
	}

	protected abstract Document request(String url) throws IOException;
}
