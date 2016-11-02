package net.launcher.game.resourcepack;

import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;

import java.io.IOException;

/**
 * @author ci010
 */
public interface ResourcePackManager extends LaunchElementManager<ResourcePack>
{
	Image getIcon(ResourcePack resourcePack) throws IOException;

}
