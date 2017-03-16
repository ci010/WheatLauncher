package net.launcher.fx.impl;

import api.launcher.EventBus;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.launcher.fx.Shell;
import net.launcher.fx.TaskProvider;
import net.launcher.fx.View;
import net.launcher.fx.module.ComponentProvider;
import net.launcher.fx.module.InstanceProvider;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author ci010
 */
public class ShellImpl implements Shell
{
	private Map<Class, ComponentProvider> componentProviderMap;
	private Map<Class, InstanceProvider> instanceProviderMap;
	private View<TaskProvider> taskProviders;
	private EventBus bus;

	@Override
	public <T> T getInstance(Class<T> tClass)
	{
		InstanceProvider instanceProvider = instanceProviderMap.get(tClass);
		if (instanceProvider != null)
			return (T) instanceProvider.getInstance(tClass);
		return null;
	}

	@Override
	public <T> View<T> getView(Class<T> type)
	{
		ComponentProvider componentProvider = componentProviderMap.get(type);
		if (componentProvider != null)
			return componentProvider.getComponent(type);
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
}
