package net.launcher.resourcepack;

import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;
import net.launcher.game.ResourcePack;

import java.io.IOException;

/**
 * @author ci010
 */
public interface ResourcePackManager extends LaunchElementManager<ResourcePack>
{
	Image getIcon(ResourcePack resourcePack) throws IOException;
}
