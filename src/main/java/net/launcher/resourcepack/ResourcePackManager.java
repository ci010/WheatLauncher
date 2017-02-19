package net.launcher.resourcepack;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;
import net.launcher.game.ResourcePack;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public interface ResourcePackManager extends LaunchElementManager<ResourcePack>
{
	Image getIcon(ResourcePack resourcePack);

	Future<?> update();

	Task<ResourcePack> importResourcePack(Path resourcePack);

	Task<Void> exportResourcePack(Path path, Collection<ResourcePack> pack);
}
