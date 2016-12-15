package net.wheatlauncher.internal.io;

import javafx.beans.Observable;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * @author ci010
 */
public abstract class IOGuard<T>
{
	private WeakReference<T> reference;
	private Path path;

	protected Path getRoot() {return path;}

	public abstract void forceSave(Path path) throws IOException;

	public abstract T loadInstance() throws IOException;

	protected T getInstance() {return reference != null ? reference.get() : null;}

	public final T load(IOGuardManger manger) throws IOException
	{
		path = manger.getRoot();
		T load = loadInstance();
		reference = new WeakReference<>(load);
		deploy(manger::registerSaveTask);
		return load;
	}

	protected abstract void deploy(BiConsumer<Observable[], IOGuardManger.IOTask> register);

	public boolean isActive() {return reference.get() != null;}
}
