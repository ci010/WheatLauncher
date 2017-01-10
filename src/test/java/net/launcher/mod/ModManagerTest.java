package net.launcher.mod;

import net.launcher.LaunchElementManager;
import net.launcher.game.forge.ForgeMod;
import net.launcher.utils.resource.ArchiveRepository;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.concurrent.Executors;

/**
 * @author ci010
 */
public class ModManagerTest
{
	@Test
	public void testCreate()
	{
		ModManagerBuilder modManagerBuilder = ModManagerBuilder.create(Paths.get("C:\\Users\\CIJhn\\Desktop\\testMod"), Executors.newFixedThreadPool(1));
		ArchiveRepository<ForgeMod[]> archiveRepository = modManagerBuilder.getArchiveRepository();
		LaunchElementManager<ForgeMod> build = modManagerBuilder.build();
		archiveRepository.importFile();
	}
}
