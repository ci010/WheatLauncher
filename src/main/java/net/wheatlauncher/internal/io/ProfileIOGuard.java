package net.wheatlauncher.internal.io;

import net.launcher.profile.LaunchProfile;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.profile.LaunchProfileManagerBuilder;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingFactory;
import net.launcher.setting.GameSettingInstance;
import net.launcher.utils.CallbacksOption;
import net.launcher.utils.NIOUtils;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ProfileIOGuard extends IOGuard<LaunchProfileManager>
{
	private static final String PROFILE_NAME = "profile.json";

	public ProfileIOGuard(Path root) {super(root, Executors.newCachedThreadPool());}

	private Path getProfileRoot(String name)
	{
		return getRoot().resolve(name);
	}

	private Path getProfileSetting(String name) {return getRoot().resolve(name).resolve(PROFILE_NAME);}

	public void saveProfileSetting(String profileName, GameSettingInstance instance)
	{
		getService().submit(() ->
		{
			LaunchProfileManager manager = reference.get();
			if (manager == null) throw new IllegalStateException();

			Optional<LaunchProfile> optional = manager.getProfile(profileName);
			if (!optional.isPresent()) throw new IllegalArgumentException();

			Path profileRoot = getProfileRoot(profileName);
			instance.getGameSettingType().save(new MinecraftDirectory(profileRoot.toFile()), instance);
			return profileRoot;
		});
	}

	public void saveProfile(String name)
	{
		getService().submit(() ->
		{
			LaunchProfileManager manager = reference.get();
			if (manager == null) throw new IllegalStateException();

			Optional<LaunchProfile> optional = manager.getProfile(name);
			if (!optional.isPresent()) throw new IllegalArgumentException();

			LaunchProfile profile = optional.get();
			Path profileDir = getRoot().resolve(name);
			if (!Files.exists(profileDir))
				Files.createDirectories(profileDir);
			Path profileJSON = profileDir.resolve(PROFILE_NAME);

			JSONObject object = new JSONObject();
			object.put("version", profile.getVersion());
			object.put("resolution", profile.getResolution().toString());
			object.put("memory", profile.getMemory());
			object.put("minecraft", profile.getMinecraftLocation().getAbsolutePath());
			object.put("java", profile.getJavaEnvironment().getJavaPath());

			NIOUtils.writeString(profileJSON, object.toString(4));
			return object;
		});
	}

	private void onDeleteProfile(String profile)
	{
		getService().submit(() ->
		{
			Path profileDir = getRoot().resolve(profile);
			if (!Files.exists(profileDir))
				return profileDir;
			Path profileJSON = profileDir.resolve(PROFILE_NAME);
			Files.delete(profileJSON);
			return profileDir;
		});
	}

	private void onRenameProfile(String name, String newName)
	{
		Path profileRoot = getRoot().resolve(name);
		if (!Files.exists(profileRoot))
			throw new IllegalArgumentException();
		try
		{
			Files.move(profileRoot, getRoot().resolve(newName));
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	private String onNewProfile(String name)
	{
		Path profileRoot = getProfileRoot(name);
		try
		{
			Files.createDirectories(profileRoot);
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException(e);
		}
		return name;
	}

	@Override
	public LaunchProfileManager loadInstance() throws IOException
	{
		LaunchProfileManager manager = LaunchProfileManagerBuilder.create()
				.setProfileFactory(LaunchProfileManagerBuilder.defaultProfileFactory().compose(this::onNewProfile))
				.setDeleteGuard(this::onDeleteProfile)
				.setRenameGuard(this::onRenameProfile)
				.build();

		List<Path> walk = Files.walk(getRoot(), 1).filter(path -> Files.isDirectory(path)).collect(Collectors.toList());
		for (Path path : walk)
		{
			String rootName = path.getFileName().toString();
			LaunchProfile profile = manager.newProfile(rootName);

			JSONObject object = new JSONObject(NIOUtils.readToString(path.resolve("profile.json")));
			profile.setVersion(object.optString("version"));
			profile.setJavaEnvironment(new JavaEnvironment(new File(
					object.optString("java", JavaEnvironment.getCurrentJavaPath().getAbsolutePath()))));
			profile.setMemory(Integer.valueOf(object.optString("memory", "512")));
			profile.setMinecraftLocation(new MinecraftDirectory(new File(
					object.optString("minecraft", ".minecraft"))));
			if (!CallbacksOption.whatever(() ->
			{
				String winSize = object.optString("resolution", "856x482");
				if (winSize.equals("Fullscreen"))
					profile.setResolution(WindowSize.fullscreen());
				else
				{
					String[] xes = winSize.split("x");
					profile.setResolution(new WindowSize(Integer.valueOf(xes[0]), Integer.valueOf(xes[1])));
				}
				return winSize;
			}).isPresent())
				profile.setResolution(new WindowSize(856, 482));

			for (Map.Entry<String, GameSetting> entry : GameSettingFactory.getAllSetting().entrySet())
				try
				{
					GameSetting value = entry.getValue();
					Optional<GameSettingInstance> whatever = CallbacksOption.whatever(() -> value.load(profile.getMinecraftLocation()));
					if (whatever.isPresent())
						profile.addGameSetting(whatever.get());
					else
					{
						GameSettingInstance load = value.load(new MinecraftDirectory(getProfileRoot(rootName).toFile()));
						if (load != null)
							profile.addGameSetting(load);
					}
				}
				catch (IOException e)
				{
					//report
				}
		}

		reference = new WeakReference<>(manager);
		return manager;
	}

}
