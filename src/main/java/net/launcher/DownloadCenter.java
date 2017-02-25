package net.launcher;

import javafx.concurrent.Task;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloadOption;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.download.combine.CombinedDownloadTask;
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask;
import org.to2mbn.jmccc.mcdownloader.provider.forge.ForgeVersion;
import org.to2mbn.jmccc.mcdownloader.provider.liteloader.LiteloaderVersion;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Version;

import java.util.Objects;

/**
 * @author ci010
 */
public interface DownloadCenter
{
	static String combineVersion(ForgeVersion forgeVersion, LiteloaderVersion liteloaderVersion)
	{
		Objects.requireNonNull(forgeVersion);
		Objects.requireNonNull(liteloaderVersion);
		return forgeVersion.getVersionName() + "-LiteLoader" + liteloaderVersion.getMinecraftVersion();
	}

	MinecraftDownloader listenDownloader(String name, MinecraftDownloader downloader);

	Task<Version> downloadVersion(MinecraftDirectory dir, String version, MinecraftDownloadOption... options);

	<T> Task<T> download(CombinedDownloadTask<T> task);

	<T> Task<T> download(CombinedDownloadTask<T> task, int tries);

	<T> Task<T> download(DownloadTask<T> task);

	<T> Task<T> download(DownloadTask<T> task, int tries);
}
