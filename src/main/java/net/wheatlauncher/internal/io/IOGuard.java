package net.wheatlauncher.internal.io;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * @author ci010
 */
public abstract class IOGuard<T>
{
	private WeakReference<T> reference;
	private IOGuardContext context;

	protected IOGuardContext getContext() {return context;}

	protected abstract void forceSave() throws IOException;

	public abstract T loadInstance() throws IOException;

	public abstract T defaultInstance();

	protected T getInstance() {return reference != null ? reference.get() : null;}

	public final void init(IOGuardContext context)
	{
		Objects.requireNonNull(context);
		if (this.context != null)
			throw new IllegalStateException();
		this.context = context;
	}

	public final T load() throws IOException
	{
		T load;
		try
		{
			load = loadInstance();
		}
		catch (Exception e)
		{
			load = defaultInstance();
		}
		reference = new WeakReference<>(load);
		deploy();
		return load;
	}

	protected abstract void deploy();

	public boolean isActive() {return reference != null && reference.get() != null;}
}
