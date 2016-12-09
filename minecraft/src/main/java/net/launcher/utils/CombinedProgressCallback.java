package net.launcher.utils;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

/**
 * @author ci010
 */
public interface CombinedProgressCallback<T> extends Callback<T>
{
	ProgressCallback<T> newSubProgress();
}
