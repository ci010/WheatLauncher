package net.launcher.model.fx.profile;

import javafx.concurrent.Task;
import net.launcher.game.ModManifest;
import net.launcher.model.fx.TaskProvider;
import net.launcher.model.fx.TaskProviderBase;
import net.launcher.model.fx.module.InstanceProvider;
import net.launcher.model.fx.module.ProfileBaseModule;
import org.to2mbn.jmccc.util.Builder;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ci010
 */
public class ModuleSetting extends ProfileBaseModule
{
	private List<TaskProvider> tasks = Arrays.asList(
			new TaskProviderBase("save")
			{
				@Override
				public Task<?> createTask(String... args)
				{
					return null;
				}
			}

	);

	@Override
	protected void onInit()
	{

	}

	@Override
	public List<TaskProvider> getAllTaskProviders()
	{
		return Collections.emptyList();
	}

	@Override
	public InstanceProvider createInstanceProvider(Path profileLocation)
	{
		return new InstanceProvider()
		{
			private Path root = profileLocation;

			@Override
			public List<Class<?>> getAllInstanceBuilderType()
			{
				return Arrays.asList(
						SettingMinecraft.class,
						ModManifest.class
				);
			}

			@Override
			public Builder<?> getInstanceBuilder(Class<?> type)
			{
				if (type.equals(SettingMinecraft.class))
					return () -> null;
				if (type.equals(ModManifest.class))
					return () -> null;
				return null;
			}

			@Override
			public Task<Void> saveInstanceTask(Object o)
			{
				if (o instanceof SettingMinecraft)
					return new Task<Void>()
					{
						@Override
						protected Void call() throws Exception
						{
							return null;
						}
					};

				return null;
			}
		};
	}
}
