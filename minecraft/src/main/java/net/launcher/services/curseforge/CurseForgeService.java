package net.launcher.services.curseforge;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The service for the CurseForge project.
 * <p>Notice that these functions are all blocking.
 *
 * @author ci010
 * @see CurseForgeServices
 */
public interface CurseForgeService
{
	/**
	 * Get what the type this service is requesting.
	 *
	 * @return Project type enum
	 */
	CurseForgeProjectType getRequestingProjectType();

	/**
	 * Search project by keyword。
	 *
	 * @param keyword The keyword used to search
	 * @return The project cache.
	 */
	Cache<CurseForgeProject> search(String keyword) throws IOException;

	/**
	 * Filter projects by option
	 *
	 * @param option The option used to filter projects
	 * @return The project cache.
	 */
	Cache<CurseForgeProject> view(Option option) throws IOException;

	/**
	 * Grow the cache to load more projects into it.
	 *
	 * @param cache The cache will be grow.
	 * @return if the cache is grow. Basically, if there is no more projects satisfy the cache requirement, this will
	 * return false
	 */
	boolean growCache(Cache<?> cache) throws IOException;

	/**
	 * @return The game version constrains, used on the {@link Option#setGameVersionConstrain(String)}.
	 */
	List<String> getGameVersionConstrains();

	/**
	 * @return The sort option, used on the {@link Option#setSortOption(String)} (String)}.
	 */
	List<String> getSortedOptions();

	/**
	 * @return The CurseForge category, used on the {@link Option#setCategory(CurseForgeCategory)}.
	 */
	List<CurseForgeCategory> getCategories();

	/**
	 * Indicate the search filter option.
	 */
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

	/**
	 * Indicate a simple cache for a list of projects
	 *
	 * @param <T>
	 */
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
}
