package net.wheatlauncher.internal.io;

import javafx.beans.Observable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author ci010
 */
public interface IOGuardContext
{
	void saveAll() throws Exception;

	void loadAll() throws IOException;

	<T> T load(Class<T> tClass) throws IOException;

	@SuppressWarnings("unchecked")
	<T> IOGuard<T> getGuard(Class<T> clz);

	Path getRoot();

	void enqueue(IOTask task);

	void registerSaveTask(IOTask task, Observable... observables);

	interface IOTask
	{
		void performance(Path root) throws Exception;

		default boolean canMerge(IOTask task) {return task.getClass().equals(this.getClass());}
	}
}
