package net.launcher;

import api.launcher.Plugin;

import java.util.ResourceBundle;

/**
 * @author ci010
 */
public class PluginContainer
{
	private PluginMetaData metaData;
	private Plugin plugin;
	private ResourceBundle bundle;
	private ClassLoader classLoader;

	PluginContainer(PluginMetaData metaData, Plugin plugin, ResourceBundle bundle, ClassLoader classLoader)
	{
		this.metaData = metaData;
		this.plugin = plugin;
		this.bundle = bundle;
		this.classLoader = classLoader;
	}

	public PluginMetaData getMetaData() {return metaData;}

	public Plugin getPlugin() {return plugin;}

	public ResourceBundle getBundle() {return bundle;}

	public ClassLoader getClassLoader() {return classLoader;}
}
