package net.launcher.assets;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import api.launcher.MinecraftAssetsManager;
import api.launcher.event.LaunchEvent;
import api.launcher.io.IOGuard;
import api.launcher.io.IOGuardContext;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import net.launcher.game.Language;
import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.resource.FetchOption;
import net.launcher.utils.resource.FetchUtils;
import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;
import org.to2mbn.jmccc.option.LaunchOption;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.util.IOUtils;
import org.to2mbn.jmccc.version.Asset;
import org.to2mbn.jmccc.version.Version;
import org.to2mbn.jmccc.version.parsing.Versions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class IOGuardMinecraftAssetsManager extends IOGuard<MinecraftAssetsManager>
		implements MinecraftAssetsManagerImpl.AssetsRepository
{
	private ObservableList<MinecraftVersion> versions = FXCollections.observableArrayList();
	private ObservableMap<String, MinecraftVersion> versionMap = FXCollections.observableMap(new TreeMap<>());

	@Override
	public void forceSave() throws IOException
	{
		MinecraftAssetsManager instance = this.getInstance();
		if (instance == null) return;
		Path data = getContext().getRoot().resolve("versions.dat");
		NBT.write(data, saveThis(), false);
	}

	@Override
	public MinecraftAssetsManager loadInstance() throws IOException
	{
		Path data = getContext().getRoot().resolve("versions.dat");
		NBTCompound compound = NBT.read(data, false).asCompound();
		this.lastModified = compound.get("lastModified").asString();
		this.cache = readVersionList(compound.get("cache").asCompound());
		//refreshVersion info
		return new MinecraftAssetsManagerImpl(versions, versionMap, this);
	}

	@Override
	public MinecraftAssetsManager defaultInstance()
	{
		return new MinecraftAssetsManagerImpl(versions, versionMap, this);
	}

	@Override
	protected void deploy()
	{
		MinecraftAssetsManager instance = this.getInstance();
		getContext().registerSaveTask(new SaveTask(), instance.getVersions());
		refreshVersion().run();
		ARML.bus().addEventHandler(LaunchEvent.LAUNCH_EVENT, event ->
		{
//			Prepare task = new Prepare(event.getOption(), event.getProfile());
//			ARML.core().getTaskCenter().listenTask(task);
//			task.run();
		});
	}

	@Override
	public Version buildVersion(MinecraftVersion version) throws IOException
	{
		if (version == null) return null;
		MinecraftDirectory minecraftDirectory = new MinecraftDirectory(getContext().getRoot().toFile());
		return Versions.resolveVersion(minecraftDirectory, version.getVersionID());
	}

	@Override
	public Task<MinecraftVersion> fetchVersion(MinecraftVersion version)
	{
		Objects.requireNonNull(version);
		return new Task<MinecraftVersion>()
		{
			@Override
			protected MinecraftVersion call() throws Exception
			{
				if (version.getState() == MinecraftVersion.State.LOCAL) return version;
				Platform.runLater(() ->
						version.setState(MinecraftVersion.State.DOWNLOADING));
				MinecraftDirectory minecraftDirectory = new MinecraftDirectory(getContext().getRoot().toFile());
				MinecraftDownloader downloader = MinecraftDownloaderBuilder.buildDefault();//ARML.core()
				// .getDownloadCenter().listenDownloader("minecraft.game",
//				MinecraftDownloaderBuilder.buildDefault());
				downloader.downloadIncrementally(minecraftDirectory, version.getVersionID(), new CallbackAdapter<Version>()
				{
					@Override
					public void failed(Throwable e)
					{
						Platform.runLater(() -> version.setState(MinecraftVersion.State.REMOTE));
						e.printStackTrace();
					}

					@Override
					public void cancelled()
					{
						Platform.runLater(() -> version.setState(MinecraftVersion.State.REMOTE));
					}

					@Override
					public void done(Version result)
					{
						Platform.runLater(() ->
						{
							version.setState(MinecraftVersion.State.LOCAL);
							locals.add(version.getVersionID());
						});
					}
				}).get();
				return version;
			}
		};
	}

	@Override
	public Task<List<MinecraftVersion>> refreshVersion()
	{
		return new Task<List<MinecraftVersion>>()
		{
			@Override
			protected List<MinecraftVersion> call() throws Exception
			{
				updateTitle("Refresh Minecraft version");
				updateProgress(0, 5);
				boolean localChange = updateLocal();
				updateProgress(1, 5);
				boolean remoteChange = updateRemoteVersion();
				updateProgress(2, 5);

				MinecraftAssetsManagerImpl manager = (MinecraftAssetsManagerImpl) getInstance();
				List<MinecraftVersion> versionList = new ArrayList<>();
				if (localChange)
					for (String version : locals)
					{
						MinecraftVersion contained = manager.getVersion(version);
						if (contained != null)
						{
							contained.setState(MinecraftVersion.State.LOCAL);
							continue;
						}
						MinecraftVersion v = new MinecraftVersion(version, MinecraftVersion.State.LOCAL);
						if (cache != null)
						{
							RemoteVersion remote = cache.getVersions().get(version);
							if (remote != null) v.getMetadata().put("remote", remote);
						}
						versionList.add(v);
					}
				updateProgress(3, 5);

				if (!remoteChange)
				{
					for (String version : cache.getVersions().keySet().stream().filter(s -> !locals.contains(s)).collect(Collectors.toList()))
					{
						MinecraftVersion contained = manager.getVersion(version);
						if (contained != null)
						{
							if (contained.getState() == MinecraftVersion.State.LOCAL)
								contained.setState(MinecraftVersion.State.REMOTE);
							continue;
						}
						MinecraftVersion v = new MinecraftVersion(version, MinecraftVersion.State.REMOTE);
						RemoteVersion remote = cache.getVersions().get(version);
						v.getMetadata().put("remote", remote);
						versionList.add(v);
					}
				}
				updateProgress(4, 5);

				Runnable task = () ->
				{
					if (!versionList.isEmpty())
					{
						versions.setAll(versionList);
						versionMap.clear();
						for (MinecraftVersion version : versions)
							versionMap.put(version.getVersionID(), version);
						versions.sort((o1, o2) ->
						{
							Object remote1 = o1.getMetadata().get("remote");
							Object remote2 = o2.getMetadata().get("remote");
							if (remote1 != null && remote2 != null)
							{
								RemoteVersion ver1 = (RemoteVersion) remote1;
								RemoteVersion ver2 = (RemoteVersion) remote2;
								return ver2.getReleaseTime().compareTo(ver1.getReleaseTime());
							}
							String v1 = o2.getVersionID(), v2 = o1.getVersionID();
							if (!Character.isDigit(v1.charAt(0)))
								if (!Character.isDigit(v2.charAt(0))) return v1.compareTo(v2);
								else return -1;
							if (!Character.isDigit(v1.charAt(2)))
								if (!Character.isDigit(v2.charAt(2))) return v1.compareTo(v2);
								else return -1;
							return new ComparableVersion(v1).compareTo(new ComparableVersion(v2));
						});
					}
					updateProgress(5, 5);

				};
				if (Platform.isFxApplicationThread())
					task.run();
				else PlatformImpl.runAndWait(task);
				return versionList;
			}
		};
	}

	@Override
	public Task<Language[]> getLanguages(MinecraftVersion version)
	{
		return new Task<Language[]>()
		{
			@Override
			protected Language[] call() throws Exception
			{
				if (version == null) return new Language[0];
				MinecraftDirectory root = new MinecraftDirectory(getContext().getRoot().toFile());
				Version v = Versions.resolveVersion(root, version.getVersionID());
				Set<Asset> assets = Versions.resolveAssets(root, v);
				if (assets == null) return new Language[0];
				List<File> collect = assets
						.stream().filter(asset -> asset.getVirtualPath().endsWith("pack.mcmeta"))
						.map(root::getAsset).collect(Collectors.toList());
				Deserializer<Language[], JSONObject> deserializer = Language.deserializer();
				return deserializer.deserialize(IOUtils.toJson(collect.get(0)));
			}
		};
	}

	@Override
	public Task<Void> importMinecraft(MinecraftDirectory directory)
	{
		return null;
	}

	@Override
	public Task<Void> exportVersion(MinecraftVersion version, Path target)
	{
		return null;
	}

	private Set<String> locals = Collections.synchronizedSet(new TreeSet<>());
	private String lastModified;
	private RemoteVersionList cache;

	private boolean updateLocal()
	{
		MinecraftDirectory minecraftDirectory = new MinecraftDirectory(getContext().getRoot().toFile());
		Set<String> versions = Versions.getVersions(minecraftDirectory);
		boolean changed = !versions.equals(locals);
		if (changed)
		{
			locals.clear();
			locals.addAll(versions);
		}
		return changed;
	}

	private boolean updateRemoteVersion() throws IOException
	{
		HttpRequester requester = new HttpRequester();

		Map<String, String> header;
		if (lastModified == null)
			header = Collections.emptyMap();
		else
			header = Collections.singletonMap("If-Modified-Since", lastModified);
		IOException e = null;
		String get = null;
		try {get = requester.request("GET", "https://launchermeta.mojang.com/mc/game/version_manifest.json", header);}
		catch (IOException ex) {e = ex;}
		if (get == null || get.equals("")) return false;
		cache = RemoteVersionList.fromJson(new JSONObject(get));
		lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).format(Calendar.getInstance()
				.getTime());
		this.getContext().enqueue(new SaveTask());
		if (e != null) throw e;
		return true;
	}

	public void onLaunch(LaunchOption option, LaunchProfile profile) throws Exception
	{
		MinecraftDirectory root = new MinecraftDirectory(getContext().getRoot().toFile());
		Path from;
		Path to;

		to = option.getMinecraftDirectory().getAssets().toPath();
		from = root.getAssets().toPath();
		Files.deleteIfExists(to);
		FetchUtils.fetch(from, to, FetchOption.HARD_LINK);

		to = option.getMinecraftDirectory().getLibraries().toPath();
		from = root.getLibraries().toPath();
		Files.deleteIfExists(to);
		FetchUtils.fetch(from, to, FetchOption.HARD_LINK);
	}

	public void onClose(LaunchOption option, LaunchProfile profile)
	{

	}

	class SaveTask implements IOGuardContext.IOTask
	{
		@Override
		public void performance(Path root) throws IOException
		{
			Path resolve = root.resolve("versions.dat");
			MinecraftAssetsManager instance = getInstance();
			if (instance != null)
				NBT.write(resolve, saveThis(), false);
		}

		@Override
		public boolean isEquivalence(IOGuardContext.IOTask task) {return task == this || task instanceof SaveTask;}

	}

	private NBTCompound saveThis()
	{
		return NBT.compound().put("lastModified", lastModified)
				.put("cache", writeVersionList(cache));
	}

	private NBT writeVersionList(RemoteVersionList versionList)
	{
		return NBT.compound().put("latestSnapshot", versionList.getLatestSnapshot())
				.put("latestRelease", versionList.getLatestRelease())
				.put("versions", NBT.list(versionList.getVersions().values().stream().map(this::writeVersion).collect
						(Collectors.toList())));
	}

	private RemoteVersionList readVersionList(NBTCompound compound)
	{
		return new RemoteVersionList(compound.get("latestSnapshot").asString(), compound.get("latestRelease")
				.asString(), compound.get("versions").asList().stream().map(NBT::asCompound).map(this::readVersion).collect(
				Collectors.toMap(RemoteVersion::getVersion, Function.identity())));
	}

	private NBT writeVersion(RemoteVersion version)
	{
		return NBT.compound().put("version", version.getVersion())
				.put("uploadTime", version.getUploadTime().getTime())
				.put("releaseTime", version.getReleaseTime().getTime())
				.put("type", version.getType())
				.put("url", version.getUrl());
	}

	private RemoteVersion readVersion(NBTCompound compound)
	{
		return new RemoteVersion(compound.get("version").asString(),
				new Date(compound.get("uploadTime").asLong()),
				new Date(compound.get("releaseTime").asLong()),
				compound.get("type").asString(),
				compound.get("url").asString());
	}
}
