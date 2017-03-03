package api.launcher;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;
import net.launcher.game.forge.ForgeMod;

import java.nio.file.Path;

/**
 * @author ci010
 */
public interface ModManager extends LaunchElementManager<ForgeMod>
{
	Image getLogo(ForgeMod forgeMod);

	Task<ForgeMod[]> importMod(Path path);

	Task<Void> exportMod(Path path, ForgeMod mod);

	Task<?> update();
}
