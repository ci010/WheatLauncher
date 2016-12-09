package net.launcher.utils;

import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

/**
 * @author ci010
 */
public interface ProgressCallback<T> extends Callback<T>
{
	/**
	 * Calls when the progress of the operation updated.
	 *
	 * @param done    done progress
	 * @param total   total progress
	 * @param message the message of this state.
	 */
	void updateProgress(long done, long total, String message);
}
