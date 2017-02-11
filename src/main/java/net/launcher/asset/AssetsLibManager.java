package net.launcher.asset;

import net.launcher.LaunchManager;
import net.launcher.profile.LaunchProfile;
import net.launcher.utils.resource.Repository;
import org.to2mbn.jmccc.option.LaunchOption;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ci010
 */
public class AssetsLibManager implements LaunchManager
{
	private Repository<Void> assetRepository, libRepo;
	private Map<LaunchOption, Object[]> launchCache = new HashMap<>();

	public AssetsLibManager(Repository<Void> assetRepository, Repository<Void> libRepo)
	{
		this.assetRepository = assetRepository;
		this.libRepo = libRepo;
	}

	@Override
	public void onLaunch(LaunchOption option, LaunchProfile profile)
	{
		Path path = option.getRuntimeDirectory().getRoot().toPath();
		Object[] deliveries = new Object[2];

		Path assets = path.resolve("assets");
//		deliveries[0] = assetRepository.fetchAllResources(assets, null, Repository.FetchOption.SYMBOL_LINK);
//		Path libraries = path.resolve("libraries");
//		deliveries[1] = libRepo.fetchAllResources(libraries, null, Repository.FetchOption.SYMBOL_LINK);

		launchCache.put(option, deliveries);
	}

	@Override
	public void onClose(LaunchOption option, LaunchProfile profile)
	{
		Object[] delivery = launchCache.get(option);
		if (delivery != null)
			for (Object o : delivery)
				((Repository.Delivery) o).markRelease();
	}
}
