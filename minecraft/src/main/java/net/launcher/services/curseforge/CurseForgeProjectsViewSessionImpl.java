package net.launcher.services.curseforge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class CurseForgeProjectsViewSessionImpl implements CurseForgeService.ViewSession
{
	private List<CurseForgeProject> view;

	protected List<CurseForgeProject> projects;

	private List<String> sortedOptions;
	private String sortOption;

	private List<String> gameVersions;
	private String gameVersionConstrain;

	private int page;
	private int maxPages;

	private List<CurseForgeCategory> categories;
	private CurseForgeCategory category;

	CurseForgeProjectsViewSessionImpl(List<CurseForgeProject> projects, List<String> sortedOptions, String sortOption,
									  Map<String, String> gameVersionConstrains, String gameVersionConstrain, int page, int maxPages,
									  List<CurseForgeCategory> categories, CurseForgeCategory category)
	{
		this.projects = projects;
		this.view = Collections.unmodifiableList(projects);
		this.sortedOptions = sortedOptions;
		this.sortOption = sortOption;
		this.gameVersions = new ArrayList<>(gameVersionConstrains.values());
		this.gameVersionConstrain = gameVersionConstrain;
		this.page = page;
		this.maxPages = maxPages;
		this.categories = categories;
		this.category = category;
	}

	@Override
	public List<String> getSortedOptions()
	{
		return Collections.unmodifiableList(sortedOptions);
	}

	@Override
	public List<String> getGameVersionConstrains()
	{
		return Collections.unmodifiableList(gameVersions);
	}

	@Override
	public List<CurseForgeProject> getProjects()
	{
		return view;
	}

	@Override
	public List<CurseForgeCategory> getCategories()
	{
		return Collections.unmodifiableList(categories);
	}

	@Override
	public CurseForgeCategory getCategory()
	{
		return category;
	}

	@Override
	public void setCategory(CurseForgeCategory category)
	{
		Objects.requireNonNull(category);

		this.category = category;
	}

	@Override
	public String getSortOption()
	{
		return sortOption;
	}

	@Override
	public void setSortOption(String selectedSortOption)
	{
		if (selectedSortOption == null) selectedSortOption = "";
		if (!sortedOptions.contains(selectedSortOption)) throw new IllegalArgumentException();
		this.sortOption = selectedSortOption;
	}

	@Override
	public String getGameVersionConstrain()
	{
		return gameVersionConstrain;
	}

	@Override
	public void setGameVersionConstrain(String gameVersionConstrain)
	{
		if (gameVersionConstrain == null) gameVersionConstrain = "";
		if (!gameVersionConstrain.contains(gameVersionConstrain)) throw new IllegalArgumentException();
		this.gameVersionConstrain = gameVersionConstrain;
	}

	@Override
	public void setPage(int page)
	{
		this.page = page;
	}

	@Override
	public int getPage()
	{
		return page;
	}

	@Override
	public int getMaxPages()
	{
		return maxPages;
	}

	static class ArtifactCache implements CurseForgeService.ArtifactCache
	{
		private List<CurseForgeProjectArtifact> artifacts;
		private int page, max;
		private String requestingURL;

		ArtifactCache(List<CurseForgeProjectArtifact> artifacts, int max, String requestingURL)
		{
			this.artifacts = artifacts;
			this.max = max;
			this.page = 1;
			this.requestingURL = requestingURL;
		}

		List<CurseForgeProjectArtifact> artifacts() {return artifacts;}

		String getRequestingURL() {return requestingURL;}

		public int getCachedSegment()
		{
			return page;
		}

		public int getTotalSegment()
		{
			return max;
		}

		public List<CurseForgeProjectArtifact> getCachedArtifact()
		{
			return artifacts;
		}

		public CurseForgeProjectArtifact getLatestArtifact()
		{
			return artifacts.get(0);
		}

		public List<CurseForgeProjectArtifact> getFromMinecraftVersion(String minecraftVersion)
		{
			return artifacts.stream().filter(modInfo -> modInfo.getGameVersion().equals(minecraftVersion))
					.collect(Collectors.toList());
		}

		public CurseForgeProjectArtifact getLatestFromMinecraftVersion(String minecraftVersion)
		{
			return getFromMinecraftVersion(minecraftVersion).get(0);
		}

		@Override
		public String toString()
		{
			return "ArtifactCache{" +
					"artifacts=" + artifacts +
					", page=" + page +
					", max=" + max +
					", requestingURL='" + requestingURL + '\'' +
					'}';
		}
	}

	@Override
	public String toString()
	{
		return "CurseForgeProjectsViewSessionImpl{" +
				"view=" + view +
				", projects=" + projects +
				", sortedOptions=" + sortedOptions +
				", sortOption='" + sortOption + '\'' +
				", gameVersions=" + gameVersions +
				", gameVersionConstrain='" + gameVersionConstrain + '\'' +
				", page=" + page +
				", maxPages=" + maxPages +
				", categories=" + categories +
				", category=" + category +
				'}';
	}
}
