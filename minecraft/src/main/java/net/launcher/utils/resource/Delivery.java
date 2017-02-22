package net.launcher.utils.resource;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/**
 * @author ci010
 */
public class Delivery<T>
{
	private Collection<T> items;
	private Collection<Path> paths;

	public Delivery(Collection<T> items, Collection<Path> paths)
	{
		this.items = Collections.unmodifiableCollection(items);
		this.paths = Collections.unmodifiableCollection(paths);
	}

	public Collection<Path> getResourceVirtualPaths() {return paths;}

	public Collection<T> get() {return items;}
}
