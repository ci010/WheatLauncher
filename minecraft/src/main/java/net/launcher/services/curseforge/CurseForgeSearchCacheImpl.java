package net.launcher.services.curseforge;

import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
class CurseForgeSearchCacheImpl implements CurseForgeService.SearchCache
{
	boolean canGrow;

	private List<CurseForgeProject> projects;
	private List<String> sortOP;

	CurseForgeSearchCacheImpl()
	{
		canGrow = false;
		projects = Collections.emptyList();
		sortOP = Collections.emptyList();
	}

	CurseForgeSearchCacheImpl(boolean canGrow, List<CurseForgeProject> projects, List<String> sortOP)
	{
		this.canGrow = canGrow;
		this.projects = projects;
		this.sortOP = sortOP;
	}

	@Override
	public boolean canGrow()
	{
		return canGrow;
	}

	@Override
	public List<CurseForgeProject> getProjects()
	{
		return projects;
	}

	@Override
	public List<String> getSortedOptions()
	{
		return sortOP;
	}
}
