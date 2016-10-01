package net.wheatlauncher;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import net.launcher.utils.Logger;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.launch.LaunchException;
import org.to2mbn.jmccc.launch.Launcher;
import org.to2mbn.jmccc.launch.LauncherBuilder;
import org.to2mbn.jmccc.option.LaunchOption;
import org.to2mbn.jmccc.util.IOUtils;
import org.to2mbn.jmccc.util.Platform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public enum Core
{
	INSTANCE;
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private Timer timer = new Timer(true);

	private final boolean isRootValid;

	//////////
	//Thread//
	//////////
	public ExecutorService getService()
	{
		return executorService;
	}

	public Timer getTimer()
	{
		return timer;
	}

	/////////
	//Logic//
	/////////


	private ListProperty<LaunchProfile> profileListProperty =
			new SimpleListProperty<>(FXCollections.observableArrayList());

	private ObjectProperty<LaunchProfile> selectLaunchProperty = new SimpleObjectProperty<>();

	{
		selectLaunchProperty.addListener((observable, oldValue, newValue) ->
		{
			Objects.requireNonNull(newValue);
			if (oldValue != null)
				oldValue.onUnapply();
			newValue.onApply();
		});
	}

	public LaunchProfile newProfileAndSelect(String name)
	{
		LaunchProfile profile = new LaunchProfile(name);
		tryReg(profile);
		profile.nameProperty().addListener((observable, oldValue, newValue) ->
		{
			if (oldValue != null)
			{
				File oldFile = new File(getProfilesRoot(), oldValue + File.separator + "profile.json");
				oldFile.renameTo(new File(getProfilesRoot(), newValue + File.separator + "profile.json"));
			}
		});
		selectLaunchProperty.set(profile);
		return profile;
	}

	public void selectProfile(String name)
	{
		LaunchProfile profile = getProfile(name);
		if (profile != null)
			selectLaunchProperty.setValue(profile);
		else
		{
			Logger.trace("no profile named " + name + "!");
			//TODO store exception
			if (this.launchProfileListProperty().size() == 0)
				newProfileAndSelect("default");
			else
				selectLaunchProperty.setValue(this.launchProfileListProperty().get(0));
		}
	}

	public LaunchProfile getCurrentProfile()
	{
		return selectedProfileProperty().get();
	}

	public LaunchProfile getProfile(String name)//O(n) since I think it just will not have so much profiles...
	{
		for (LaunchProfile profile : profileListProperty.get())
			if (profile.nameProperty().get().equals(name)) return profile;
		return null;
	}

	private void tryReg(LaunchProfile profile)
	{
		if (getProfile(profile.nameProperty().get()) != null)
			throw new IllegalArgumentException("duplicated launch profile!");
		profile.nameProperty().addListener((observable, oldValue, newValue) ->
		{
			if (oldValue != null)
			{
				File oldFile = new File(getProfilesRoot(), oldValue + File.separator + "profile.json");
				oldFile.renameTo(new File(getProfilesRoot(), newValue + File.separator + "profile.json"));
			}
		});
		profileListProperty.get().add(profile);
	}

	public ReadOnlyListProperty<LaunchProfile> launchProfileListProperty() {return profileListProperty;}

	public ReadOnlyObjectProperty<LaunchProfile> selectedProfileProperty() {return selectLaunchProperty;}

	////////
	//File//
	////////
	private final File root;

	{
		File root;
		switch (Platform.CURRENT)
		{
			case WINDOWS:
				String appdata = System.getenv("APPDATA");
				root = new File(appdata == null ? System.getProperty("user.home", ".") : appdata, ".launcher/");
				break;
			case LINUX:
				root = new File(System.getProperty("user.home", "."), ".launcher/");
				break;
			case OSX:
				root = new File("Library/Application Support/launcher/");
				break;
			default:
				root = new File(System.getProperty("user.home", ".") + "/");
		}
		if (isRootValid = root.exists() || root.mkdir())
			this.root = root;
		else
			this.root = new File("").getAbsoluteFile();
		getArchivesRoot().mkdirs();
		getBackupRoot().mkdirs();
		getProfilesRoot().mkdirs();
	}

	public File getRoot() {return root;}

	public File getArchivesRoot()
	{
		return new File(root, "archives");
	}

	public File getBackupRoot()
	{
		return new File(root, "backup");
	}

	public File getProfilesRoot() { return new File(root, "profiles"); }

	////////
	//Load//
	////////
	void onDestroy()
	{
		this.save();
		executorService.shutdown();
		timer.cancel();
		timer.purge();
		Logger.trace("Shutdown");
	}

	void onInit()
	{
		Logger.trace("Start init");
		this.load();
		this.save();

//		timer.scheduleAtFixedRate(new TimerTask()
//		{
//			@Override
//			public void run()
//			{
//
//			}
//		}, 10000, 100000);
	}

	private void load()
	{
		File configFile = new File(root, "config.json");
		if (!configFile.isFile())
			newProfileAndSelect("default");
		else try
		{
			this.loadJson(IOUtils.toJson(configFile));
		}
		catch (IOException e)
		{
			newProfileAndSelect("default");
		}
	}

	private void saveProfile(LaunchProfile launchProfile)
	{

	}

	private Future<Void> save()
	{
		return executorService.submit(() ->
		{
			JSONObject root = new JSONObject();
			JSONArray arr = new JSONArray();
			for (LaunchProfile profile : this.launchProfileListProperty())
			{
				arr.put(profile.nameProperty().getValue());
			}
			root.put("selecting", this.selectLaunchProperty.get().nameProperty().getValue());
			root.put("profiles", arr);

			try (FileWriter writer = new FileWriter(new File(getRoot(), "config.json")))
			{
				root.write(writer);
			}

			for (LaunchProfile profile : this.launchProfileListProperty())
			{
				JSONObject serialize = LaunchProfile.SERIALIZER.serialize(profile);
				try (FileWriter writer = new FileWriter(new File(getProfilesRoot(),
						profile.nameProperty().get() + File.separator + "profile.json")))
				{
					serialize.write(writer);
				}
			}
			return null;
		});

	}

	private void loadJson(JSONObject root)
	{
		JSONArray profiles = root.getJSONArray("profiles");
		for (int i = 0; i < profiles.length(); i++)
		{
			String profileName = profiles.getString(i);
			File file = new File(getProfilesRoot(), profileName + File.separator + "profile.json");
			try
			{
				JSONObject profileObj = IOUtils.toJson(file);
				LaunchProfile deserialize = LaunchProfile.SERIALIZER.deserialize(profileObj);
				if (deserialize != null) tryReg(deserialize);
			}
			catch (IOException ignored)
			{
				//TODO store exception
			}
		}
		String selecting = root.getString("selecting");
		Logger.trace("selecting profile " + selecting);
		this.selectProfile(selecting);
	}

	public void tryLaunch()
	{
		this.save();

		LaunchProfile profile = getCurrentProfile();
		Launcher launcher = LauncherBuilder.buildDefault();
		try
		{
			LaunchOption option = new LaunchOption(
					profile.versionProperty().getValue(),
					profile.getAuth(),
					profile.minecraftLocationProperty().getValue());
			option.setWindowSize(profile.windowSizeProperty().getValue());
			launcher.launch(option);
		}
		catch (LaunchException | IOException e)
		{
			e.printStackTrace();
		}
	}
}
