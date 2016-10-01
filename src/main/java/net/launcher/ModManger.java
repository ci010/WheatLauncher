package net.launcher;

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

	Future<File> importMod(File mod);

	Map<String, Mod> getAllMods();
}
