package net.launcher;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public interface ResourcePackManger
{
	Optional<ResourcePack> getResourcePack(String name);

	Future<File> importResourcePack(File file);

	Image getIcon(ResourcePack resourcePack) throws IOException;

	Map<String, ResourcePack> getAllResourcePacks();
}
