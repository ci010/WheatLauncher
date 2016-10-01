package net.launcher.io;

import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author ci010
 */
public abstract class LoadHandler<T>
{
	private Map<SourceObject, T> cachedMap = new TreeMap<>();
	private File root;

	public LoadHandler(File root)
	{
		this.root = root;
	}

	public void setCached(boolean b)
	{
		if (b) cachedMap = Collections.emptyMap();
		else if (cachedMap == Collections.EMPTY_MAP) cachedMap = new TreeMap<>();
	}

	public Map<SourceObject, T> getCachedMap()
	{
		return cachedMap;
	}

	public File getRoot()
	{
		return root;
	}

	public Optional<T> fetch(SourceObject... srcs) throws IOException
	{
		return this.load(null, srcs);
	}

	public Optional<T> load(T instance, SourceObject... srcs) throws IOException
	{
		if (srcs == null) return Optional.empty();
		for (SourceObject src : srcs)
		{
			if (instance == null) instance = cachedMap.get(src);
			instance = loadFromString(instance, src, IOUtils.toString(new File(root, src.getPath()))).orElse(null);
			if (instance != null && cachedMap != Collections.EMPTY_MAP) cachedMap.put(src, instance);
		}
		return Optional.ofNullable(instance);
	}

	public Optional<T> loadFromString(T instance, SourceObject object, String data) throws IOException
	{
		return Optional.ofNullable(this.load(object, object.getType().deserialize(data), instance));
	}

	protected abstract T load(SourceObject object, Map<String, String> dataMap, T instance) throws IOException;

	public void reloadAll() throws IOException
	{
		for (Map.Entry<SourceObject, T> entry : cachedMap.entrySet()) fetch(entry.getKey()).ifPresent(entry::setValue);
	}
}
