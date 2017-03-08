package api.launcher.io;

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

	public T getInstance() {return reference != null ? reference.get() : null;}

	public void destroy() throws Exception {}

	public final void init(IOGuardContext context)
	{
		Objects.requireNonNull(context);
		if (this.context != null) throw new IllegalStateException();
		this.context = context;
		onInit();
	}

	protected void onInit() {}

	public final T load() throws IOException
	{
		if (reference != null)
		{
			T inst = reference.get();
			if (inst != null) return inst;
		}
		T load;
		try {load = loadInstance();}
		catch (Exception e)
		{
			e.printStackTrace();
			load = defaultInstance();
		}
		reference = new WeakReference<>(load);
		deploy();
		return load;
	}

	protected abstract void deploy() throws IOException;

	public boolean isActive() {return reference != null && reference.get() != null;}
}
