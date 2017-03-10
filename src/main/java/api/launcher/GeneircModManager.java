package api.launcher;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;
import net.launcher.game.mods.ModContainer;
import net.launcher.game.nbt.NBTCompound;

import java.nio.file.Path;

/**
 * @author ci010
 */
public interface GeneircModManager extends LaunchElementManager<ModContainer<?>>
{
	Task<Image> getLogo(ModContainer<?> mod);

	Task<ModContainer<?>[]> importMod(Path path);

	Task<Void> exportMod(Path path, ModContainer<?> mod);

	Task<?> update();

	NBTCompound getCustomMetadata(ModContainer<?> mod);
}
