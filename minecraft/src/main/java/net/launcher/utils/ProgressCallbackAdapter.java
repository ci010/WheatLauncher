package net.launcher.utils;

/**
 * @author ci010
 */
public abstract class ProgressCallbackAdapter<T> implements ProgressCallback<T>
{
	@Override
	public void updateProgress(long done, long total, String message)
	{}

	@Override
	public void done(T result)
	{}

	@Override
	public void failed(Throwable e)
	{}

	@Override
	public void cancelled()
	{}
}
