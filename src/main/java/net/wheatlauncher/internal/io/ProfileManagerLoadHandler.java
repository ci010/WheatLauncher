package net.wheatlauncher.internal.io;

import net.launcher.GameSetting;
import net.launcher.ILaunchProfile;
import net.launcher.LaunchProfileManager;
import net.launcher.LaunchProfileMangerBuilder;
import net.launcher.auth.AuthenticationIndicatorFactory;
import net.launcher.io.LoadHandler;
import net.launcher.io.SourceObject;
import net.launcher.setting.Option;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @author ci010
 */
class ProfileManagerLoadHandler extends LoadHandler<LaunchProfileManager>
{
	private LoadHandler<ILaunchProfile> child;

	ProfileManagerLoadHandler(File root)
	{
		super(root);
		child = new LoadHandler<ILaunchProfile>(root)
		{
			@Override
			protected ILaunchProfile load(SourceObject object, Map<String, String> dataMap, ILaunchProfile profile)
			{
				if (ProfileMangerIO.PROFILE.isTypeOf(object))
				{
					String name = dataMap.get("name");
					profile.nameProperty().setValue(name);
					profile.javaProperty().setValue(new JavaEnvironment(new File(
							dataMap.getOrDefault("java", JavaEnvironment.getCurrentJavaPath().getAbsolutePath()))));
					profile.memoryProperty().setValue(Integer.valueOf(dataMap.getOrDefault("memory", "512")));

					profile.minecraftProperty().setValue(new MinecraftDirectory(new File(
							dataMap.getOrDefault("minecraft", ".minecraft"))));
					profile.authProperty().setValue(AuthenticationIndicatorFactory.get(dataMap.getOrDefault("auth", ""))
							.orElse(AuthenticationIndicatorFactory.OFFLINE));
					profile.accountProperty().setValue(dataMap.getOrDefault("account", ""));
					try
					{
						String winSize = dataMap.get("windows-size");
						if (winSize.equals("Fullscreen"))
							profile.resolutionProperty().setValue(WindowSize.fullscreen());
						else
						{
							String[] xes = winSize.split("x");
							profile.resolutionProperty().setValue(new WindowSize(Integer.valueOf(xes[0]), Integer.valueOf(xes[1])));
						}
					}
					catch (Exception ignored)
					{
						//TODO store exception
						profile.resolutionProperty().setValue(new WindowSize(856, 482));
					}
					if (dataMap.containsKey("version"))
						profile.versionProperty().setValue(dataMap.get("version"));
				}
				else GameSetting.getOptionBySource(object).forEach(option -> setupOption(profile, dataMap, option));
				return profile;
			}
		};
	}

	@Override
	protected LaunchProfileManager load(SourceObject object, Map<String, String> dataMap,
										LaunchProfileManager inst) throws IOException
	{
		LaunchProfileManager manager = inst == null ? LaunchProfileMangerBuilder.buildDefault() : inst;
		JSONArray arr = new JSONArray(dataMap.get("profiles"));
		IOException[] ex = new IOException[1];
		for (int i = 0; i < arr.length(); i++)
		{
			String profileName = arr.getString(i);
			ProfileMangerIO.PROFILE.createIfExist(getRoot(), profileName, "profile.json").ifPresent(o ->
			{
				ILaunchProfile profile = manager.newProfile(profileName);
				GameSetting.getSourcesView().forEach(prototype ->
				{
					try
					{
						Optional<SourceObject> sourceObject = prototype.createIfExist(getRoot(), profileName);
						if (sourceObject.isPresent())
							child.load(profile, sourceObject.get());
					}
					catch (IOException e)
					{
						if (ex[0] == null)
							ex[0] = e;
						else ex[0].addSuppressed(e);
					}
				});
				try//blocking load
				{
					child.load(profile, o);
				}
				catch (IOException e)
				{
					if (ex[0] == null)
						ex[0] = e;
					else ex[0].addSuppressed(e);
				}
			});
		}
		if (ex[0] != null) throw ex[0];
		String selecting = dataMap.get("selecting");
		manager.select(selecting);
		//		Logger.trace("selecting profile " + selecting);
		return manager;
	}

	private <T> void setupOption(ILaunchProfile profile, Map<String, String> dataMap, Option<T> option)
	{
		profile.getSetting(option).ifPresent(p -> p.setValue(option.deserialize(dataMap.get(option.getName()))));
	}
}
