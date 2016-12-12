package net.launcher.services.curseforge;

import java.io.IOException;
import java.util.List;

/**
 * @author ci010
 */
public interface CurseForgeService
{
	CurseForgeProjectType getRequestingProjectType();

	ViewSession viewSession() throws IOException;

	ViewSession refresh(ViewSession session) throws IOException;

	ArtifactCache cacheArtifact(CurseForgeProject project) throws IOException;

	SearchCache search(String keyword) throws IOException;

	boolean growSearchCache(SearchCache session) throws IOException;

	boolean growArtifactCache(ArtifactCache cache) throws IOException;

	interface SearchCache
	{
		boolean canGrow();

		List<CurseForgeProject> getProjects();

		List<String> getSortedOptions();
	}

	interface ViewSession
	{
		List<CurseForgeProject> getProjects();

		List<String> getSortedOptions();

		List<String> getGameVersionConstrains();

		List<CurseForgeCategory> getCategories();

		CurseForgeCategory getCategory();

		void setCategory(CurseForgeCategory category);

		String getSortOption();

		void setSortOption(String selectedSortOption);

		String getGameVersionConstrain();

		void setGameVersionConstrain(String gameVersionConstrain);

		void setPage(int page);

		int getPage();

		int getMaxPages();
	}

	interface ArtifactCache
	{
		int getCachedSegment();

		int getTotalSegment();

		List<CurseForgeProjectArtifact> getCachedArtifact();

		CurseForgeProjectArtifact getLatestArtifact();

		List<CurseForgeProjectArtifact> getFromMinecraftVersion(String minecraftVersion);

		CurseForgeProjectArtifact getLatestFromMinecraftVersion(String minecraftVersion);
	}
}
