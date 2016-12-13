package net.launcher.services.curseforge;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public interface CurseForgeService
{
	CurseForgeProjectType getRequestingProjectType();

	Cache<CurseForgeProject> search(String keyword) throws IOException;

	Cache<CurseForgeProject> filter(Option option) throws IOException;

	boolean growCache(Cache<?> session) throws IOException;

	List<String> getGameVersionConstrains();

	List<String> getSortedOptions();

	List<CurseForgeCategory> getCategories();

	class Option
	{
		private CurseForgeCategory category;
		private String option, constrain;

		public static Option create() {return new Option();}

		public Option setCategory(CurseForgeCategory category) {this.category = category; return this;}

		public Option setSortOption(String option) {this.option = option; return this;}

		public Option setGameVersionConstrain(String constrain) {this.constrain = constrain; return this;}

		public CurseForgeCategory getCategory() {return category;}

		public String getOption() {return option;}

		public String getConstrain() {return constrain;}

		@Override
		public String toString()
		{
			return "Option{" +
					"category=" + category +
					", option='" + option + '\'' +
					", constrain='" + constrain + '\'' +
					'}';
		}
	}

	class Cache<T>
	{
		protected List<T> cache;
		protected Map<String, Object> context;

		public Cache(List<T> cache, Map<String, Object> contest)
		{
			this.cache = cache;
			this.context = contest;
		}

		public Cache()
		{
			cache = Collections.emptyList();
			context = Collections.emptyMap();
		}

		public List<T> getCache()
		{
			return cache;
		}

		public Map<String, Object> getContext()
		{
			return context;
		}

		@Override
		public String toString()
		{
			return "Cache{" +
					"cache=" + cache +
					", context=" + context +
					'}';
		}
	}

	class ProjectCache
	{
		protected List<CurseForgeProject> projects;
		protected Map<String, Object> context;

		public ProjectCache(List<CurseForgeProject> projects, Map<String, Object> contest)
		{
			this.projects = projects;
			this.context = contest;
		}

		public ProjectCache()
		{
			projects = Collections.emptyList();
			context = Collections.emptyMap();
		}

		public List<CurseForgeProject> getProjects() {return Collections.unmodifiableList(projects);}

		public Map<String, Object> getContext() {return Collections.unmodifiableMap(context);}

		@Override
		public String toString()
		{
			return "ProjectCache{" +
					"projects=" + projects +
					", context=" + context +
					'}';
		}
	}

	class ArtifactCache
	{
		private List<CurseForgeProjectArtifact> artifacts;
		private Map<String, Object> context;
		private int page, max;
		private String requestingURL;

		ArtifactCache(List<CurseForgeProjectArtifact> artifacts, int max, String requestingURL)
		{
			this.artifacts = artifacts;
			this.max = max;
			this.page = 1;
			this.requestingURL = requestingURL;
		}

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
}
