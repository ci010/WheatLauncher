package net.wheatlauncher.internal.io;

import org.to2mbn.jmccc.option.MinecraftDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ci010
 */
public class VirtualMinecraftDirectory
{
	private Path assetsDir, libDir, versionDir, saveDir;

	private Path serverData;

	public VirtualMinecraftDirectory(Path root)
	{
		assetsDir = root.resolve("assets");
		libDir = root.resolve("libraries");
		versionDir = root.resolve("versions");
		saveDir = root.resolve("saves");
		serverData = root.resolve("servers.dat");
	}

	public MinecraftDirectory create(Path root) throws IOException
	{
		MinecraftDirectory directory = new MinecraftDirectory(root.toFile());
		Files.createSymbolicLink(directory.getAssets().toPath(), this.assetsDir);
		Files.createSymbolicLink(directory.getLibraries().toPath(), this.libDir);
		Files.createSymbolicLink(directory.getVersions().toPath(), this.versionDir);
		Files.createSymbolicLink(root.resolve("saves"), saveDir);
		Files.createSymbolicLink(root.resolve("servers.dat"), serverData);
		return null;
	}
}
