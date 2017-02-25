package api.launcher;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.game.WorldInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author ci010
 */
public interface MinecraftWorldManager
{
	/**
	 * @return All the minecraft saved maps.
	 */
	ObservableList<WorldInfo> getWorldInfos();

	Image getWorldIcon(WorldInfo worldInfo) throws IOException;

	Task<List<WorldInfo>> refresh();

	Task<Void> saveMap(WorldInfo worldInfo);

	Task<WorldInfo> importMap(Path path);

	Task<Path> exportMap(WorldInfo worldInfo, Path target);
}
