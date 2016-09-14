package net.wheatlauncher.utils.resource;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.CombinedDownloadCallback;

import java.io.File;
import java.io.IOException;

/**
 * @author ci010
 */
public interface RemoteRepository<T>
{
	String getName();

	Class<T> getType();

	boolean hasResource(String path) throws IOException;

	void fetch(String path, CombinedDownloadCallback<File> callback) throws IOException;
}
