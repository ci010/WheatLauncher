package net.wheatlauncher.internal.io;

import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.profile.LaunchProfile;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.profile.LaunchProfileManagerBuilder;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingFactory;
import net.launcher.setting.GameSettingInstance;
import net.launcher.utils.Logger;
import net.launcher.utils.NIOUtils;
import net.launcher.utils.Tasks;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ProfileIOGuard extends IOGuard<LaunchProfileManager>
{
	private static final String PROFILE_NAME = "profile.json";

	private Path getProfilesRoot() {return getContext().getRoot().resolve("profiles");}

	private Path getProfileDir(String name) {return getProfilesRoot().resolve(name);}

	private Path getProfileSetting(String name) {return getProfileDir(name).resolve(PROFILE_NAME);}

	public void saveProfileSetting(String profileName, GameSettingInstance instance) throws IOException
	{
		LaunchProfileManager manager = getInstance();
		if (manager == null) throw new IllegalStateException();

		Optional<LaunchProfile> optional = manager.getProfile(profileName);
		if (!optional.isPresent()) throw new IllegalArgumentException();

		Path profileRoot = getProfileDir(profileName);
		instance.getGameSettingType().save(profileRoot, instance);
	}

	public void saveProfile(String name) throws IOException
	{
		LaunchProfileManager manager = getInstance();
		if (manager == null) throw new IllegalStateException();

		Optional<LaunchProfile> optional = manager.getProfile(name);
		if (!optional.isPresent()) throw new IllegalArgumentException();

		LaunchProfile profile = optional.get();
		Path profileDir = getProfileDir(name);
		if (!Files.exists(profileDir))
			Files.createDirectories(profileDir);
		Path profileJSON = profileDir.resolve(PROFILE_NAME);

		JSONObject object = new JSONObject();
		object.put("version", profile.getVersion());
		object.put("resolution", profile.getResolution().toString());
		object.put("memory", profile.getMemory());
		object.put("minecraft", profile.getMinecraftLocation().getAbsolutePath());
		object.put("java", profile.getJavaEnvironment().getJavaPath());
		object.put("name", profile.getDisplayName());
		object.put("id", profile.getId());

		NIOUtils.writeString(profileJSON, object.toString(4));
	}

	private LaunchProfile onNewProfile(String id)
	{
		Path profileRoot = getProfileDir(id);
		try {Files.createDirectories(profileRoot);}
		catch (IOException e) {throw new IllegalArgumentException(e);}
		return new LaunchProfile(id);
	}

	@Override
	public void forceSave() throws IOException
	{
		for (String s : getInstance().getAllProfiles().keySet()) saveProfile(s);
	}

	@Override
	public LaunchProfileManager loadInstance() throws IOException
	{
		Map<String, LaunchProfile> profileMap = new TreeMap<>();

		List<Path> walk = Files.walk(getProfilesRoot(), 1)
				.filter(path -> Files.isDirectory(path) && !path.equals(getProfilesRoot()))
				.collect(Collectors.toList());
		for (Path path : walk)
		{
			try
			{
				String dirName = path.getFileName().toString();
				Path profiling = getProfileSetting(dirName);

				JSONObject object = new JSONObject(NIOUtils.readToString(profiling));
				String id = object.getString("id");
				if (!Objects.equals(dirName, id))
				{
					//todo handle this exception
					continue;
				}
				LaunchProfile profile = new LaunchProfile(id);
				profileMap.put(id, profile);
				profile.setDisplayName(object.optString("name"));
				profile.setVersion(object.optString("version"));
				profile.setJavaEnvironment(new JavaEnvironment(new File(
						object.optString("java", JavaEnvironment.getCurrentJavaPath().getAbsolutePath()))));
				profile.setMemory(Integer.valueOf(object.optString("memory", "512")));
				profile.setMinecraftLocation(new MinecraftDirectory(new File(
						object.optString("minecraft", ".minecraft"))));
				if (!Tasks.optional(() ->
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
				}).isPresent()) profile.setResolution(new WindowSize(856, 482));

				IOException exception = null;
				for (Map.Entry<String, GameSetting> entry : GameSettingFactory.getAllSetting().entrySet())
					try
					{
						GameSetting value = entry.getValue();
						Optional<GameSettingInstance> whatever = Tasks.optional(() -> value.load(profile
								.getMinecraftLocation().getRoot().toPath()));
						if (whatever.isPresent())
							profile.addGameSetting(whatever.get());
						else
						{
							GameSettingInstance load = value.load(getProfileDir(dirName));
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
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		LaunchProfileManager manager = LaunchProfileManagerBuilder.create()
				.setProfileFactory(this::onNewProfile)
				.setInitState(profileMap)
//				.setDeleteGuard(this::onDeleteProfile)
				.build();
		try
		{
			NBTCompound read = NBT.read(getProfilesRoot().resolve("profiles.dat"), true).asCompound();
			manager.setSelectedProfile(read.get("select").asString());
		}
		catch (Exception e)
		{
			if (manager.getAllProfiles().isEmpty())
			{
				manager.setSelectedProfile(manager.newProfile("default").getId());
			}
			else
				manager.setSelectedProfile(manager.getAllProfiles().keySet().iterator().next());
		}
		Logger.trace("returning the manager instance");
		return manager;
	}

	@Override
	public LaunchProfileManager defaultInstance() {return LaunchProfileManagerBuilder.buildDefault();}


	private class ProfileSaveTask implements IOGuardContext.IOTask
	{
		private String profile;

		public ProfileSaveTask(String profile)
		{
			this.profile = profile;
		}

		@Override
		public void performance(Path root) throws IOException {saveProfile(profile);}

		@Override
		public boolean canMerge(IOGuardContext.IOTask task)
		{
			if (task instanceof ProfileSaveTask)
				if (Objects.equals(((ProfileSaveTask) task).profile, this.profile)) return true;
			return false;
		}
	}

	private class ProfileSaveSelectTask implements IOGuardContext.IOTask
	{
		@Override
		public void performance(Path root) throws IOException
		{
			NBT.overwrite(getProfilesRoot().resolve("profiles.dat"),
					NBT.compound().put("select", getInstance().getSelectedProfile()),
					true);
		}

	}

	@Override
	protected void deploy()
	{
		LaunchProfileManager instance = getInstance();
		instance.getAllProfiles().addListener((MapChangeListener<String, LaunchProfile>) change ->
		{
			String key = change.getKey();
			if (change.wasAdded())
			{
				getContext().registerSaveTask(grab(change.getValueAdded()), new ProfileSaveTask(key));
			}
			else if (change.wasRemoved())
			{
				getContext().enqueue((path) ->
				{
					Path profileJSON = getProfileSetting(key);
					Files.delete(profileJSON);
				});
			}
		});
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
