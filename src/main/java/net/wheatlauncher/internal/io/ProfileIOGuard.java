package net.wheatlauncher.internal.io;

import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ProfileIOGuard extends IOGuard<LaunchProfileManager>
{
	private static final String PROFILE_NAME = "profile.json";

	private Path getProfileRoot(String name)
	{
		return getRoot().resolve(name);
	}

	private Path getProfileSetting(String name) {return getRoot().resolve(name).resolve(PROFILE_NAME);}

	public void saveProfileSetting(String profileName, GameSettingInstance instance)
	{
		getQueue().enqueue(() ->
		{
			LaunchProfileManager manager = getInstance();
			if (manager == null) throw new IllegalStateException();

			Optional<LaunchProfile> optional = manager.getProfile(profileName);
			if (!optional.isPresent()) throw new IllegalArgumentException();

			Path profileRoot = getProfileRoot(profileName);
			instance.getGameSettingType().save(new MinecraftDirectory(profileRoot.toFile()), instance);
			return null;
		});
	}

	public void saveProfile(String name) throws IOException
	{
		LaunchProfileManager manager = getInstance();
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
	}

	private void onDeleteProfile(String profile)
	{
		Path profileDir = getRoot().resolve(profile);
		if (!Files.exists(profileDir)) return;
//		Path profileJSON = profileDir.resolve(PROFILE_NAME);
//		Files.delete(profileJSON);
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
	public void forceSave(Path path) throws IOException
	{
		for (String s : getInstance().getAllProfiles().keySet())
			saveProfile(s);
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

			IOException exception = null;
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
					if (exception == null) exception = e;
					else exception.addSuppressed(e);
					//report
				}
		}
		return manager;
	}

	@Override
	protected void deploy(BiConsumer<Observable[], IOGuardManger.IOTask> register)
	{
		LaunchProfileManager instance = getInstance();
		instance.getAllProfiles().addListener((MapChangeListener<String, LaunchProfile>) change ->
		{
			if (change.wasAdded())
			{
				register.accept(grab(change.getValueAdded()), new ProfileSaveTask(change.getKey()));
			}
			else if (change.wasRemoved())
			{
			}
		});
	}

	private class ProfileSaveTask implements IOGuardManger.IOTask
	{
		private String profile;

		public ProfileSaveTask(String profile)
		{
			this.profile = profile;
		}

		@Override
		public void performance(Path root) throws IOException
		{
			LaunchProfileManager manager = getInstance();
			if (manager == null) throw new IllegalStateException();

			Optional<LaunchProfile> optional = manager.getProfile(this.profile);
			if (!optional.isPresent()) throw new IllegalArgumentException();

			LaunchProfile profile = optional.get();
			Path profileDir = getRoot().resolve(this.profile);
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
		}

		@Override
		public boolean canMerge(IOGuardManger.IOTask task)
		{
			if (task instanceof ProfileSaveTask)
				if (Objects.equals(((ProfileSaveTask) task).profile, this.profile)) return true;
			return false;
		}
	}

	private Observable[] grab(LaunchProfile profile)
	{
		return new Observable[]{profile.javaEnvironmentProperty(),
								profile.versionProperty(),
								profile.memoryProperty(),
								profile.minecraftLocationProperty(),
								profile.resolutionProperty()};
	}

}
