package net.wheatlauncher;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import net.wheatlauncher.launch.LaunchProfile;
import net.wheatlauncher.mod.ModRepository;
import net.wheatlauncher.utils.Logger;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CombinedDownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.util.IOUtils;
import org.to2mbn.jmccc.util.Platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ci010
 */
public enum Core
{
	INSTANCE;
	private RemoteVersionList versionList;

	private ExecutorService executorService = Executors.newCachedThreadPool();
	private Timer timer = new Timer(true);

	private final File root;
	private final boolean isRootValid;

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
	}

	private ListProperty<MinecraftDirectory> mcHistory = new SimpleListProperty<>(FXCollections.observableArrayList
			(new ArrayList<MinecraftDirectory>()
			{
				@Override
				public boolean add(MinecraftDirectory minecraftDirectory)
				{
					return !contains(minecraftDirectory) && super.add(minecraftDirectory);
				}
			}));
	private ListProperty<JavaEnvironment> javaHistory = new SimpleListProperty<>(FXCollections.observableArrayList());
	private MapProperty<String, LaunchProfile> profileMapProperty = new SimpleMapProperty<>
			(FXCollections.observableHashMap());
	private ModRepository modRepository;
	private ObjectProperty<LaunchProfile> selectLaunchProperty = new SimpleObjectProperty<LaunchProfile>()
	{
		@Override
		public void set(LaunchProfile newValue)
		{
			newValue.onApply();
			super.set(newValue);
		}
	};

	{
		selectLaunchProperty.addListener((observable, oldValue, newValue) -> {
			if (oldValue != null)
			{

			}
			newValue.onApply();
			newValue.minecraftLocationProperty().addListener(observable1 -> {
			});
		});
	}

	private Map<String, LaunchProfile> profileMap = new HashMap<>();

	public LaunchProfile newProfile(String name, LaunchProfile parent)
	{
		return null;
	}

	public LaunchProfile getCurrentProfile()
	{
		return selectLaunchProfile().get();
	}

	public ObjectProperty<LaunchProfile> selectLaunchProfile()
	{
		return selectLaunchProperty;
	}

	public ExecutorService getService()
	{
		return executorService;
	}

	public ListProperty<MinecraftDirectory> getMinecraftLocationHistory()
	{
		return mcHistory;
	}

	public ListProperty<JavaEnvironment> getJavaHistory()
	{
		return javaHistory;
	}

	public MapProperty<String, LaunchProfile> profileMapProperty()
	{
		return profileMapProperty;
	}

	public File getRoot()
	{
		return root;
	}

	public File getFileFromRoot(String fileName)
	{
		File rt = getRoot();
		if (!rt.exists())
			if (!rt.mkdir())
				return null;
		File f = new File(rt, fileName);
		if (!f.exists())
			return null;
		return f;
	}

	public RemoteVersionList getVersionList()
	{
		return versionList;
	}

	public Timer getTimer()
	{
		return timer;
	}

	void onDestroy()
	{

	}

	void onInit()
	{

		Logger.trace(root);
		File configFile = getFileFromRoot("wheat.json");
		JSONObject root;
		if (configFile == null)
			root = getDefaultConfig();
		else try {root = IOUtils.toJson(configFile);}
		catch (IOException e) {root = getDefaultConfig();}
		this.loadJson(root);

		MinecraftDownloaderBuilder.buildDefault().fetchRemoteVersionList(new CombinedDownloadCallback<RemoteVersionList>()
		{
			@Override
			public <R> DownloadCallback<R> taskStart(DownloadTask<R> task) {return null;}

			@Override
			public void done(RemoteVersionList result)
			{
				versionList = result;
			}

			@Override
			public void failed(Throwable e) {}

			@Override
			public void cancelled() {}
		});
	}

	private void loadJson(JSONObject root)
	{
		this.loadMcHistory(root.getJSONArray("minecraft-history"));
		this.loadJavaLoc(root.getJSONArray("java-history"));
		JSONArray profiles = root.getJSONArray("profiles");
		for (int i = 0; i < profiles.length(); i++)
		{
			LaunchProfile deserialize = LaunchProfile.SERIALIZER.deserialize(profiles.getJSONObject(i));
			if (deserialize != null)
				this.profileMap.put(deserialize.getName(), deserialize);
		}
		String selecting = root.getString("selecting");
		Logger.trace("selecting profile");
		this.selectLaunchProfile().setValue(profileMap.get(selecting));

//		JSONObject repositories = root.getJSONObject("repositories");
		////// These methods will be more generic...
//		loadModLoc(repositories.getJSONObject("mods"));
		/////
	}

	private void loadMcHistory(JSONArray arr)
	{
		for (int i = 0; i < arr.length(); i++)
		{
			File file = new File(arr.getString(i));
			if (file.isFile())
				javaHistory.add(new JavaEnvironment(file));
		}
	}

	private void loadJavaLoc(JSONArray arr)
	{
		for (int i = 0; i < arr.length(); i++)
		{
			File file = new File(arr.getString(i));
			if (file.isFile())
				mcHistory.add(new MinecraftDirectory(file));
		}
	}

	public void tryLaunch()
	{
		selectLaunchProfile().get().launch();
	}

	private JSONObject getDefaultConfig()
	{
		JSONObject root = new JSONObject();
		JSONArray profiles = new JSONArray();

		JSONObject profileSetting = new JSONObject();
		profileSetting.put("name", "default");
		profileSetting.put("minecraft", new File(".minecraft").getAbsolutePath());
		profileSetting.put("java", JavaEnvironment.getCurrentJavaPath().getAbsolutePath());
		profileSetting.put("memory", 512);
		profileSetting.put("online-mode", "disable");

		profiles.put(profileSetting);

		JSONArray javas = new JSONArray();
		javas.put(profileSetting.getString("java"));

		root.put("profiles", profiles);
		root.put("minecraft-history", new JSONArray());
		root.put("java-history", javas);
		root.put("selecting", "default");

//		JSONObject repository = new JSONObject();
//		root.put("repository", repository);

		return root;
	}
}
