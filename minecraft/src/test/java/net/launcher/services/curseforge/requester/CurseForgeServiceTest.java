package net.launcher.services.curseforge.requester;

import net.launcher.services.curseforge.*;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ci010
 */
public class CurseForgeServiceTest
{
	@Test
	public void testBuild() throws IOException
	{
		CurseForgeService service = CurseForgeServices.newService(CurseForgeProjectType.Mods);
		assert service != null;
	}

	@Test
	public void testViewSession() throws IOException
	{
		CurseForgeService service = CurseForgeServices.newService(CurseForgeProjectType.Mods);
		CurseForgeService.Cache<CurseForgeProject> cache = service.view(null);
		assert service != null;
		String s = cache.getCache().toString();

		System.out.println(s);
		System.out.println("---------------------------------------");

		service.growCache(cache);

		String second = cache.getCache().toString();

		System.out.println(second);
		System.out.println("---------------------------------------");

		assert !s.equals(second);

		CurseForgeService.Cache<CurseForgeProject> anotherCache
				= service.view(CurseForgeService.Option.create().setCategory(service.getCategories().get(1)));

		String n = anotherCache.getCache().toString();
		System.out.println(s);
		assert !s.equals(n);
	}

	@Test
	public void testSearch() throws IOException
	{
		CurseForgeService service = CurseForgeServices.newService(CurseForgeProjectType.Mods);
		CurseForgeService.Cache<CurseForgeProject> cache = service.search("tinker");
		assert cache.getCache().size() != 0;

		CurseForgeService.Cache<CurseForgeProject> cache1 = service.search("asddd");
		assert cache1.getCache().size() == 0;
	}


	@Test
	public void testArtifact() throws IOException
	{
		CurseForgeService service = CurseForgeServices.newService(CurseForgeProjectType.Mods);
		CurseForgeService.Cache<CurseForgeProject> view = service.view(null);
		CurseForgeProject curseForgeProject = view.getCache().get(0);
		CurseForgeService.Cache<CurseForgeProjectArtifact> artifact = service.artifact(curseForgeProject);
		assert artifact != null;
		for (CurseForgeProjectArtifact curseForgeProjectArtifact : artifact.getCache())
			System.out.println(curseForgeProjectArtifact);
	}
}
