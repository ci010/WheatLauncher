package net.launcher.model.fx.auth;

import javafx.concurrent.Task;
import net.launcher.model.Authorize;
import net.launcher.model.fx.TaskProvider;
import net.launcher.model.fx.View;
import net.launcher.model.fx.module.ComponentProvider;
import net.launcher.model.fx.module.GlobalModule;
import net.launcher.model.fx.module.InstanceProvider;
import org.to2mbn.jmccc.util.Builder;

import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class ModuleAuthorize extends GlobalModule
{
	@Override
	protected void onInit()
	{

	}

	@Override
	public List<TaskProvider> getAllTaskProviders()
	{
		return null;
	}

	@Override
	public ComponentProvider createComponentProvider()
	{
		return new ComponentProvider()
		{
			@Override
			public List<Class<?>> getAllComponentTypes()
			{
				return Collections.singletonList(Authorize.class);
			}

			@Override
			public <T> View<T> getComponent(Class<T> type)
			{
				return null;
			}

			@Override
			public Task<Void> saveComponentTask(View<?> o)
			{
				return null;
			}
		};
	}

	@Override
	public InstanceProvider createInstanceProvider()
	{
		return new InstanceProvider()
		{
			@Override
			public List<Class<?>> getAllInstanceBuilderType()
			{
				return Collections.singletonList(
						Authorizer.class
				);
			}

			@Override
			public Builder<?> getInstanceBuilder(Class<?> type)
			{
				return null;
			}

			@Override
			public Task<Void> saveInstanceTask(Object o)
			{
				if (!(o instanceof Authorizer))
					return null;
				return new Task<Void>()
				{
					@Override
					protected Void call() throws Exception
					{
						return null;
					}
				};
			}
		};
	}
}
