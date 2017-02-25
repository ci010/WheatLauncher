package api.launcher;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;
import net.launcher.game.ResourcePack;

import java.nio.file.Path;
import java.util.Collection;

/**
 * @author ci010
 */
public interface ResourcePackManager extends LaunchElementManager<ResourcePack>
{
	Image getIcon(ResourcePack resourcePack);

	Task<?> update();

	Task<ResourcePack> importResourcePack(Path resourcePack);

	Task<Void> exportResourcePack(Path path, Collection<ResourcePack> pack);
}
