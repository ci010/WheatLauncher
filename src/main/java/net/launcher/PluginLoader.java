package net.launcher;

import net.launcher.api.Plugin;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class PluginLoader
{
	private Path root;
	private List<PluginContainer> containers;
	private Map<String, PluginContainer> map;

	public PluginLoader(Path root)
	{
		this.root = root;
		this.containers = new ArrayList<>();
		this.map = new TreeMap<>();
	}

	public List<PluginContainer> getContainers() {return containers;}

	public Map<String, PluginContainer> getMap() {return map;}

	public void reload() throws IOException
	{
		containers.clear();
		Path plugins = root.resolve("plugins");
		List<Path> collect = Files.list(plugins).filter(path -> path.endsWith(".jar")).collect(Collectors.toList());
		for (Path path : collect)
			try
			{
				containers.add(load(path));
			}
			catch (Exception e)
			{
				//TODO report exception
			}
	}

	private PluginContainer load(Path jar) throws IOException, ClassNotFoundException, IllegalAccessException,
			InstantiationException
	{
		URLClassLoader loader = new URLClassLoader(new URL[]{
				jar.toUri().toURL()}, PluginLoader.class.getClassLoader());
		ResourceBundle lang = ResourceBundle.getBundle("assets.lang.lang", Locale.getDefault(), loader, new
				ResourceBundle.Control()
				{
					@Override
					public List<Locale> getCandidateLocales(String name, Locale locale)
					{
						return Collections.singletonList(Locale.ROOT);
					}
				});
		PluginMetaData metaData = PluginMetaData.deserializer().deserialize(new JSONObject(
				IOUtils.toString(loader.getResourceAsStream("plugin.json"))));
		Class<?> aClass = loader.loadClass(metaData.getPluginClass());
		if (Plugin.class.isAssignableFrom(aClass))
			return new PluginContainer(metaData, (Plugin) aClass.newInstance(), lang, loader);
		return null;
	}
}
