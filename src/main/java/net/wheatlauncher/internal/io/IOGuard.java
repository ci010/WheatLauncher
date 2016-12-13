package net.wheatlauncher.internal.io;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * @author ci010
 */
public abstract class IOGuard<T>
{
	private Path root;
	protected WeakReference<T> reference;
	protected ExecutorService service;

	public IOGuard(Path root, ExecutorService service)
	{
		this.root = root;
		this.service = service;
	}

	public abstract T loadInstance() throws IOException;

	protected T getInstance() {return reference.get();}

	protected ExecutorService getService() {return service;}

	public Path getRoot() {return root;}

	public T load() throws IOException
	{
		T load = loadInstance();
		reference = new WeakReference<T>(load);
		return load;
	}

	public boolean isClosed() {return reference.get() != null;}

}
