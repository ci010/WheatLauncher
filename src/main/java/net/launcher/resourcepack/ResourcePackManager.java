package net.launcher.resourcepack;

import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;
import net.launcher.game.ResourcePack;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public interface ResourcePackManager extends LaunchElementManager<ResourcePack>
{
	Image getIcon(ResourcePack resourcePack) throws IOException;

	Future<?> update();

	void importResourcePack(Path resourcePack, Callback<ResourcePack> callback);

	void exportResourcePack(Path path, Collection<ResourcePack> pack);
}
