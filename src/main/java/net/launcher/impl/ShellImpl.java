package net.launcher.impl;

import api.launcher.EventBus;
import api.launcher.Shell;
import api.launcher.TaskProvider;
import api.launcher.View;
import api.launcher.module.ComponentProvider;
import api.launcher.module.InstanceProvider;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 * @author ci010
 */
public class ShellImpl implements Shell
{
	private Map<String, ComponentProvider> componentProviderMap;
	private Map<String, InstanceProvider> instanceProviderMap;
	private View<TaskProvider> taskProviders;
	private EventBus bus;
	private ResourceBundle bundle;


	@Override
	public <T> T getInstance(Class<T> tClass)
	{
		InstanceProvider instanceProvider = instanceProviderMap.get(tClass.getName());
		if (instanceProvider != null)
			return (T) instanceProvider.getInstance(tClass);
		return null;
	}

	@Override
	public <T> T getInstance(String id, Class<T> type)
	{
		InstanceProvider instanceProvider = instanceProviderMap.get(type.getName() + ":" + id);
		if (instanceProvider != null)
			return (T) instanceProvider.getInstance(type);
		return null;
	}

	@Override
	public <T> View<T> getView(Class<T> type)
	{
		ComponentProvider componentProvider = componentProviderMap.get(type.getName());
		if (componentProvider != null)
			return componentProvider.getComponent(type);
		return null;
	}

	@Override
	public <T> View<T> getView(String id, Class<T> type)
	{
		return null;
	}

	@Override
	public View<TaskProvider> getAllTaskProviders()
	{
		return taskProviders;
	}

	@Override
	public <T> Task<T> execute(Task<T> task)
	{
		return null;
	}

	@Override
	public <T> T executeImmediately(Task<T> task)
	{
		return null;
	}

	@Override
	public <T> Task<T> execute(String title, Callable<T> task)
	{
		return null;
	}

	@Override
	public <T> T executeImmediately(String title, Callable<T> task)
	{
		return null;
	}

	@Override
	public ObservableList<Task<?>> getTaskRecords()
	{
		return null;
	}

	@Override
	public EventBus getEventBus()
	{
		return bus;
	}

	@Override
	public ResourceBundle getLanguageBundle()
	{
		return bundle;
	}
}
