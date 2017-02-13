package net.wheatlauncher;


import net.launcher.profile.LaunchProfile;
import net.launcher.utils.DirUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ci010
 */
public class WorldSaveMaintainer
{
	private Path root;

	public WorldSaveMaintainer(Path root)
	{
		this.root = root;
	}

	public void importSave(Path savePath) throws IOException
	{
		DirUtils.copy(savePath.toFile(), root.toFile());
	}

	public void onImplement(Path path, LaunchProfile profile) throws IOException
	{
		Path to = path.resolve("saves");
		Files.createSymbolicLink(to, root);
	}
}
