package api.launcher;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;
import net.launcher.game.mods.forge.ForgeMod;
import net.launcher.game.nbt.NBTCompound;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author ci010
 */
public interface ModManager extends LaunchElementManager<ForgeMod>
{
	InputStream getLogoStream(ForgeMod forgeMod) throws IOException;

	Image getLogo(ForgeMod forgeMod);

	Task<ForgeMod[]> importMod(Path path);

	Task<Void> exportMod(Path path, ForgeMod mod);

	Task<?> update();

	NBTCompound getMetadata(ForgeMod forgeMod);
}
