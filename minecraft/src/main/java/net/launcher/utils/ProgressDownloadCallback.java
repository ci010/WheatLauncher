package net.launcher.utils;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;

/**
 * @author ci010
 */
public interface ProgressDownloadCallback<T> extends ProgressCallback<T>, DownloadCallback<T>
{
}
