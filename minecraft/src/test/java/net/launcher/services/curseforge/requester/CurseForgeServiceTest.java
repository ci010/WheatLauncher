package net.launcher.services.curseforge.requester;

import net.launcher.services.curseforge.CurseForgeProjectType;
import net.launcher.services.curseforge.CurseForgeService;
import net.launcher.services.curseforge.CurseForgeServiceBuilder;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ci010
 */
public class CurseForgeServiceTest
{
	@Test
	public void testBuild()
	{
		CurseForgeService service = CurseForgeServiceBuilder.create(CurseForgeProjectType.Mods).build();
		assert service != null;
	}

	@Test
	public void testViewSession() throws IOException
	{
		CurseForgeService service = CurseForgeServiceBuilder.create(CurseForgeProjectType.Mods).build();
		CurseForgeService.ViewSession session = service.viewSession();
		assert service != null;
		String s = session.getProjects().toString();

		session.setCategory(session.getCategories().get(0));
		service.refresh(session);

		String n = session.getProjects().toString();

		assert !s.equals(n);

	}

	public void testArtifact() throws IOException
	{
		CurseForgeService service = CurseForgeServiceBuilder.create(CurseForgeProjectType.Mods).build();
		CurseForgeService.ViewSession session = service.viewSession();
		CurseForgeService.ArtifactCache artifactCache = service.cacheArtifact(session.getProjects().get(0));
		assert artifactCache != null;
		System.out.println(artifactCache);

	}

}