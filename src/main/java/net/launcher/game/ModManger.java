package net.launcher.game;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public interface ModManger
{
	Set<String> getAllModId();

	Optional<Mod> getMod(String modid);

	Future<ResourcePack> importMod(File mod, Callback<ResourcePack> callback);

	Map<String, Mod> getAllMods();
}
