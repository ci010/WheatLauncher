package net.launcher.io;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import net.launcher.utils.InvalidListenerCache;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;


/**
 * @author ci010
 */
public abstract class SaveHandler<T>
{
	private File root;
	private boolean isLazy = true;
	private List<BooleanProperty> watched = new ArrayList<>();
	private Stack<BooleanProperty> pendingRemove = new Stack<>();

	public SaveHandler(File root)
	{
		this.root = root;
	}

	public boolean isLazy()
	{
		return isLazy;
	}

	public void setLazy(boolean isLazy)
	{
		if (this.isLazy == isLazy) return;
		if (!this.isLazy)
			watched.forEach(b -> b.removeListener(cache.listener()));
		if (!isLazy)
			watched.forEach(b -> b.addListener(cache.listener(b)));
		this.isLazy = isLazy;
	}

	public File getRoot()
	{
		return root;
	}

	public void add(T instance)
	{
		Map<SourceObject, Observable[]> watched = new HashMap<>();
		this.onWatch(instance, watched);
		watched.forEach((object, observables) ->
		{
			SimpleBooleanProperty bool = create(object, instance);
			for (Observable observable : observables)
				observable.addListener(o -> bool.set(true));
			this.watched.add(bool);
		});
	}

	private InvalidListenerCache<BooleanProperty> cache = new InvalidListenerCache<>(bool -> ob ->
	{
		Bean cast = Bean.class.cast(bool.getBean());
		T inst = cast.instRef.get();
		if (inst != null)
			save(inst, cast.object, IOException::printStackTrace);
		else watched.remove(bool);
	});

	private void checkAndSave(BooleanProperty bool, Consumer<IOException> exceptionConsumer)
	{
		Bean cast = Bean.class.cast(bool.getBean());
		T inst = cast.instRef.get();
		if (inst == null) pendingRemove.push(bool);
		else
			save(inst, cast.object, exceptionConsumer);
	}

	private void clear()
	{
		while (!this.pendingRemove.isEmpty()) watched.remove(pendingRemove.pop());
	}

	private void save(T inst, SourceObject object, Consumer<IOException> exceptionConsumer)
	{
		Map<String, String> map = new HashMap<>();
		decorateMap(inst, object, map);
		saveToFile(object, map, exceptionConsumer);
	}

	private void saveToFile(SourceObject object, Map<String, String> map, Consumer<IOException> exHandler)
	{
		File path = new File(root, object.getPath());
		String serialize = object.getType().serialize(map);
		try (FileWriter writer = new FileWriter(path))
		{
			writer.write(serialize);
		}
		catch (IOException e)
		{
			exHandler.accept(e);
		}
	}

	private SimpleBooleanProperty create(SourceObject object, T inst)
	{
		SimpleBooleanProperty bool = new SimpleBooleanProperty(new Bean(object, inst), "");
		bool.addListener(cache.listener(bool));
		return bool;
	}

	private class Bean
	{
		SourceObject object;
		WeakReference<T> instRef;

		Bean(SourceObject object, T inst)
		{
			this.object = object;
			this.instRef = new WeakReference<>(inst);
		}
	}

	protected abstract void onWatch(T value, Map<SourceObject, Observable[]> map);

	protected abstract void decorateMap(T value, SourceObject src, Map<String, String> map);

	public void check() throws IOException
	{
		final IOException[] ex = new IOException[1];
		Consumer<IOException> consumer = e -> ex[0] = e;

		watched.stream().filter(ObservableBooleanValue::get).forEach(b -> checkAndSave(b, consumer));
		clear();
		if (ex[0] != null) throw ex[0];
	}

	public void forceSaveAll() throws IOException
	{
		final IOException[] ex = new IOException[1];
		Consumer<IOException> consumer = e -> ex[0] = e;
		watched.forEach(b -> checkAndSave(b, consumer));
		clear();
		if (ex[0] != null) throw ex[0];
	}
}
