package net.launcher.game;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

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

	Map<String, ResourcePack> getAllResourcePacks();

	Image getIcon(ResourcePack resourcePack) throws IOException;

	Future<ResourcePack> importResourcePack(File file, Callback<ResourcePack> callback);

	ObservableList<ResourcePack> enabledResourcePack();
}
