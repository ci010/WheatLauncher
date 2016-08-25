package net.wheatlauncher;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import net.wheatlauncher.launch.LaunchProfile;
import net.wheatlauncher.mod.ModRepository;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CombinedDownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask;
import org.to2mbn.jmccc.mcdownloader.provider.forge.ForgeVersionList;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.File;
import java.io.IOException;
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
	private ForgeVersionList forgeVersionList;

	private ExecutorService serviceIO = Executors.newCachedThreadPool();
	private Timer timer = new Timer(true);

	private ListProperty<MinecraftDirectory> mcHistory = new SimpleListProperty<MinecraftDirectory>(FXCollections.<MinecraftDirectory>observableArrayList());
	private ListProperty<JavaEnvironment> javaHistory = new SimpleListProperty<JavaEnvironment>(FXCollections.<JavaEnvironment>observableArrayList());
	private MapProperty<String, LaunchProfile> profileMapProperty = new SimpleMapProperty<String, LaunchProfile>
			(FXCollections.<String, LaunchProfile>observableHashMap());
	private MapProperty<String, ModRepository.Entry> modRepo = new SimpleMapProperty<String, ModRepository.Entry>(FXCollections
			.<String, ModRepository.Entry>observableHashMap());
	private ObjectProperty<LaunchProfile> selectLaunchProperty = new SimpleObjectProperty<LaunchProfile>()
	{
		@Override
		public void set(LaunchProfile newValue)
		{
			newValue.init();
			super.set(newValue);
		}
	};

	{
		selectLaunchProperty.addListener((observable, oldValue, newValue) -> {
			if (oldValue != null)
			{

			}
			newValue.init();
			newValue.minecraftLocationProperty().addListener(observable1 -> {
			});
		});
	}

	private Map<String, LaunchProfile> profileMap;

	public LaunchProfile getCurrentProfile()
	{
		return selectLaunchProfile().get();
	}

	public ObjectProperty<LaunchProfile> selectLaunchProfile()
	{
		return selectLaunchProperty;
	}

	public ExecutorService getIOService()
	{
		return serviceIO;
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
		return new File("%appdata%/.launcher");
	}

	public RemoteVersionList getVersionList()
	{
		return versionList;
	}

	public ForgeVersionList getForgeVersionList()
	{
		if (forgeVersionList == null)
		{}
		return forgeVersionList;
	}

	public Timer getTimer()
	{
		return timer;
	}

	void onDestroy()
	{

	}

	private void loadModLoc(JSONObject obj)//this will move to ModRepo class
	{
		for (String modid : obj.keySet())
		{
			JSONObject mod = obj.getJSONObject(modid);
			for (String mcVersion : mod.keySet())
			{
				JSONObject versionMod = obj.getJSONObject(mcVersion);

			}
		}
	}

	void onInit()
	{
		File rt = getRoot();
		if (!rt.exists())
			rt.mkdir();
		File cfg = new File(rt, "config.json");
		if (!cfg.exists())
			try
			{
				cfg.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		JSONObject root;
		if (!cfg.exists())
			root = getDefaultConfig();
		else try {root = IOUtils.toJson(cfg);}
		catch (IOException e) {root = getDefaultConfig();}
		this.loadJson(root);

		System.out.println(cfg.getAbsolutePath());
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
		JSONArray profiles = root.getJSONArray("profiles");
		JSONObject repositories = root.getJSONObject("repositories");
		////// These methods will be more generic...
		loadMcHistory(root.getJSONArray("history"));
		loadJavaLoc(repositories.getJSONArray("java"));
		loadModLoc(repositories.getJSONObject("mods"));
		/////
		for (int i = 0; i < profiles.length(); i++)
		{
			JSONObject profile = profiles.getJSONObject(i);
			profile.getString("minecraft");
		}
	}

	private void loadMcHistory(JSONArray locs)
	{
		for (int i = 0; i < locs.length(); i++)
		{
			String loc = locs.getString(i);
			File file = new File(loc);
			if (file.isFile())
				javaHistory.add(new JavaEnvironment(file));
			else
			{
				//TODO log
			}
		}
	}

	private void loadJavaLoc(JSONArray locs)
	{
		for (int i = 0; i < locs.length(); i++)
		{
			String loc = locs.getString(i);
			File file = new File(loc);
			if (file.isFile())
				mcHistory.add(new MinecraftDirectory(file));
			else
			{
				//TODO log
			}
		}
	}

	private JSONObject getDefaultLaunchProfileSetting()
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.append("name", "default");
		jsonObject.append("minecraft", new File(".minecraft").getAbsolutePath());
		jsonObject.append("java", JavaEnvironment.getCurrentJavaPath().getAbsolutePath());
		jsonObject.append("memory", 512);
		jsonObject.append("online-mode", "enable");
		return jsonObject;
	}

	private JSONObject getDefaultConfig()
	{
		JSONObject jsonObject = new JSONObject();
		JSONArray profiles = new JSONArray();
		JSONObject defaultLaunchProfileSetting = getDefaultLaunchProfileSetting();
		profiles.put(defaultLaunchProfileSetting);
		jsonObject.append("profiles", profiles);
		JSONObject repository = new JSONObject();
		jsonObject.append("repository", repository);
		JSONArray javas = new JSONArray();
		javas.put(defaultLaunchProfileSetting.getString("java"));
		repository.put("java", javas);
		jsonObject.append("history", new JSONArray());
		return jsonObject;
	}
}
