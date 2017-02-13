package net.launcher;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.provider.forge.ForgeVersion;
import org.to2mbn.jmccc.mcdownloader.provider.liteloader.LiteloaderVersion;

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

	/**
	 * Listen the downloader and all the download task will be record as {@link TaskInfo}.
	 * <p>Access the those task info by {@link #getListenedDownloaderMap()}.</p>
	 *
	 * @param name       The name for the downloader. Basically, this is the purpose
	 * @param downloader The original downloader
	 * @return The listened downloader
	 */
	MinecraftDownloader listenDownloader(String name, MinecraftDownloader downloader);

	ObservableMap<String, ObservableList<TaskInfo>> getListenedDownloaderMap();

	interface TaskInfo
	{//@formatter:off
		String getURL();
		LongProperty getProgress();
		LongProperty getTotal();
		IntegerProperty retryCount();
		IntegerProperty maxRetryCount();
		BooleanProperty isDone();
		ObservableList<Throwable> getErrors();//@formatter:on
	}
}
