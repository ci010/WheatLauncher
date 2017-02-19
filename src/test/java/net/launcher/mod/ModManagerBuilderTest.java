package net.launcher.mod;

import net.launcher.LaunchElementManager;
import net.launcher.game.forge.ForgeMod;
import net.launcher.utils.DirUtils;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Repository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public class ModManagerBuilderTest
{
	private static Path root;
	ArchiveRepository<ForgeMod[]> archiveRepository;
	LaunchElementManager<ForgeMod> build;

	@BeforeClass
	public static void init() throws IOException
	{
		root = Paths.get("").resolve("test").resolve("modMan");
		if (!Files.exists(root))
			Files.createDirectories(root);
//		else
//			DirUtils.deleteContent(root);

	}

	@Test
	public void testImport() throws URISyntaxException, ExecutionException, InterruptedException, IOException
	{
		DirUtils.deleteContent(root);
		System.out.println(root.toFile().getAbsolutePath());

		ModManagerBuilder builder = ModManagerBuilder.create(root,
				Executors.newCachedThreadPool());
		archiveRepository = builder.getArchiveRepository();
		build = builder.build();
//		Path inv = Paths.get("").resolve("src\\test\\resources\\InventoryTweaks-1.61-58.jar");
//		Path map = Paths.get("").resolve("src\\test\\resources\\journeymap-1.11.2-5.4.3.jar");
//		Path mantle = Paths.get("").resolve("src\\test\\resources\\Mantle-1.10.2-1.1.4.jar");
//		Future<ArchiveRepository.Resource<ForgeMod[]>> resourceFuture = archiveRepository.importFile(inv, null);
//		Future<ArchiveRepository.Resource<ForgeMod[]>> resourceFuture1 = archiveRepository.importFile(map, null);
//		Future<ArchiveRepository.Resource<ForgeMod[]>> resourceFuture2 = archiveRepository.importFile(mantle, null);
//		ArchiveRepository.Resource<ForgeMod[]> resource = resourceFuture.get();
//		ArchiveRepository.Resource<ForgeMod[]> resource1 = resourceFuture1.get();
//		ArchiveRepository.Resource<ForgeMod[]> resource2 = resourceFuture2.get();
//		ForgeMod[] containData = resource.getContainData();
//		for (ForgeMod containDatum : containData)
//			System.out.println(containDatum);
//		for (ForgeMod forgeMod : resource1.getContainData())
//			System.out.println(forgeMod);
//		for (ForgeMod forgeMod : resource2.getContainData())
//			System.out.println(forgeMod);
	}

	@Test
	public void testVisiblePath() throws InterruptedException, ExecutionException, URISyntaxException
	{
		ModManagerBuilder builder = ModManagerBuilder.create(root,
				Executors.newCachedThreadPool());
		archiveRepository = builder.getArchiveRepository();
		build = builder.build();
		Path inv = Paths.get("").resolve("src\\test\\resources\\InventoryTweaks-1.61-58.jar");
		Future<ArchiveRepository.Resource<ForgeMod[]>> resourceFuture = archiveRepository.importFile(inv, null);
		ArchiveRepository.Resource<ForgeMod[]> resource = resourceFuture.get();
		Collection<String> allVisiblePaths =
				archiveRepository.getAllVisiblePaths();
		for (String allVisiblePath : allVisiblePaths)
		{
			System.out.println(allVisiblePath);
		}

		archiveRepository.update().get();

		allVisiblePaths = archiveRepository.getAllVisiblePaths();
		for (String allVisiblePath : allVisiblePaths)
		{
			System.out.println(allVisiblePath);
		}

//		for (ArchiveRepository.Resource<ForgeMod[]> resource1 : archiveRepository.getResourceMap().values())
//			for (ForgeMod forgeMod : resource1.getContainData())
//				System.out.println(forgeMod.getKey());
	}

	@Test
	public void testLink() throws Exception
	{
		ModManagerBuilder builder = ModManagerBuilder.create(root,
				Executors.newCachedThreadPool());
		archiveRepository = builder.getArchiveRepository();
		build = builder.build();
		archiveRepository.update().get();
		Path test = Paths.get("").resolve("test");
		Collection<String> allVisiblePaths = archiveRepository.getAllVisiblePaths();
		String next = allVisiblePaths.iterator().next();
		Repository.Delivery<ArchiveRepository.Resource<ForgeMod[]>> resourceDelivery = archiveRepository.fetchResource(test, next, Repository.FetchOption.HARD_LINK);
		resourceDelivery.get();
		for (Path path : resourceDelivery.getResourceVirtualPaths())
		{
			System.out.println(path);
		}
		resourceDelivery.markRelease();

	}

	@Test
	public void testOpenStream() throws Exception
	{
//mcmod.info
		ModManagerBuilder builder = ModManagerBuilder.create(root,
				Executors.newCachedThreadPool());
		archiveRepository = builder.getArchiveRepository();
		build = builder.build();
		archiveRepository.update().get();
		Collection<String> allVisiblePaths = archiveRepository.getAllVisiblePaths();
		String next = allVisiblePaths.iterator().next();
		ArchiveRepository.Resource<ForgeMod[]> resource = archiveRepository.getResourceMap().get(next);
		String s = IOUtils.toString(archiveRepository.openStream(resource, "mcmod.info"));
		System.out.println(s);
	}

	@Test
	public void testSearchAlg() throws Exception
	{
//		inventorytweaks:1.61-58-a1fd884
//		mantle:1.1.4
//		journeymap:1.11.2-5.4.3
//		ModManagerBuilder builder = ModManagerBuilder.create(root,
//				Executors.newCachedThreadPool());
//		archiveRepository = builder.getArchiveRepository();
//		build = builder.build();
//		archiveRepository.update().get();
//		ObservableMap<String, ArchiveRepository.Resource<ForgeMod[]>> resourceMap = archiveRepository.getResourceMap();
//		Optional<List<ForgeMod>> reduce = resourceMap.values().stream().map(ArchiveRepository.Resource::getContainData).map(Arrays::asList).reduce((forgeMods, forgeMods2) ->
//		{
//			ArrayList<ForgeMod> forgeMods1 = new ArrayList<>(forgeMods);
//			forgeMods1.addAll(forgeMods2);
//			return forgeMods1;
//		});
//		CurseForgeService curseForgeService = CurseForgeServices.newService(CurseForgeProjectType.Mods);
//		List<ForgeMod> forgeMods = reduce.get();
//		for (ForgeMod forgeMod : forgeMods)
//		{
//			CurseForgeService.Cache<CurseForgeProject> search = curseForgeService.search(forgeMod.getModId());
//			List<CurseForgeProject> cache = search.getCache();
//			if (cache.isEmpty())
//			{
//				System.out.println(forgeMod.getModId() + " is empty!");
//				System.out.println("------------------------------------");
//			}
//			else
//			{
//				for (CurseForgeProject curseForgeProject : cache)
//				{
//					if (curseForgeProject.getProjectType() != CurseForgeProjectType.Mods) continue;
//
//					if (match(forgeMod.getKey(), curseForgeProject))
//					{
//						System.out.println("------------------------------------");
//						System.out.println("match!");
//						System.out.println(forgeMod);
//						System.out.println(curseForgeProject);
//						System.out.println("------------------------------------");
//					}
//				}
//			}
//		}
	}

//	private boolean matchVersion(ForgeMod.Key key, CurseForgeService service,CurseForgeProject project)
//	{
//
//	}

//	private boolean match(ForgeMod.Key key, CurseForgeProject project)
//	{
//		String modid = key.getModid();
//		String name = project.getName();
//		if (modid.equals(name)) return true;
//		if (modid.equals(name.toLowerCase())) return true;
//		String reduce = name.replace(" ", "").toLowerCase();
//		if (modid.equals(reduce)) return true;
//		System.out.println(reduce);
//		return false;
////	}

}
