package net.launcher.model.fx.profile;

import javafx.concurrent.Task;
import net.launcher.model.fx.TaskProvider;
import net.launcher.model.fx.module.GlobalModule;
import net.launcher.model.fx.module.InstanceProvider;
import org.to2mbn.jmccc.util.Builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class ModuleProfile extends GlobalModule
{
	@Override
	protected void onInit()
	{

	}

	@Override
	public InstanceProvider createInstanceProvider()
	{
		return new InstanceProvider()
		{
			public List<Class<?>> getAllInstanceBuilderType()
			{
				return Collections.singletonList(
						LaunchProfiler.class
				);
			}

			public Builder<?> getInstanceBuilder(Class<?> type)
			{
				if (type != LaunchProfiler.class) return null;
				return ProfilerImpl::new;
			}

			public Task<Void> saveInstanceTask(Object o)
			{
				if (!(o instanceof LaunchProfiler)) return null;
				LaunchProfiler profiler = (LaunchProfiler) o;
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

	@Override
	public List<TaskProvider> getAllTaskProviders()
	{
		return Arrays.asList(

		);
	}
}
