package net.launcher.version;

import javafx.application.Platform;
import javafx.scene.image.Image;
import net.launcher.Bootstrap;
import net.launcher.LaunchManager;
import net.launcher.Logger;
import net.launcher.game.Language;
import net.launcher.game.WorldInfo;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.profile.LaunchProfile;
import net.launcher.utils.resource.FetchUtils;
import net.launcher.utils.resource.Repository;
import net.launcher.utils.serial.Deserializer;
import net.wheatlauncher.internal.io.IOGuard;
import net.wheatlauncher.internal.io.IOGuardContext;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONException;
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
		implements MinecraftAssetsManager.AssetsRepository, LaunchManager
{
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
		return new MinecraftAssetsManager(this);
	}

	@Override
	public MinecraftAssetsManager defaultInstance()
	{
		return new MinecraftAssetsManager(this);
	}

	@Override
	protected void deploy()
	{
		MinecraftAssetsManager instance = this.getInstance();
		getContext().registerSaveTask(new SaveTask(), instance.getVersions());
	}

	@Override
	public Version buildVersion(MinecraftVersion version) throws IOException
	{
		if (version == null) return null;
		MinecraftDirectory minecraftDirectory = new MinecraftDirectory(getContext().getRoot().toFile());
		return Versions.resolveVersion(minecraftDirectory, version.getVersionID());
	}

	@Override
	public void fetchVersion(MinecraftVersion version)
	{
		if (version == null) return;
		if (version.getState() == MinecraftVersion.State.LOCAL) return;
		version.setState(MinecraftVersion.State.DOWNLOADING);
		MinecraftDirectory minecraftDirectory = new MinecraftDirectory(getContext().getRoot().toFile());
		MinecraftDownloader downloader = Bootstrap.getCore().getDownloadCenter().listenDownloader("minecraft.game", MinecraftDownloaderBuilder.buildDefault());
		downloader.downloadIncrementally(minecraftDirectory, version.getVersionID(), new CallbackAdapter<Version>()
		{
			@Override
			public void failed(Throwable e)
			{
				Platform.runLater(() ->
						version.setState(MinecraftVersion.State.REMOTE));
				e.printStackTrace();
			}

			@Override
			public void cancelled()
			{
				Platform.runLater(() ->
						version.setState(MinecraftVersion.State.REMOTE));
				Logger.trace("download version cancelled+" + version.getVersionID());
			}

			@Override
			public void done(Version result)
			{
				Platform.runLater(() ->
				{
					Logger.trace("download version compete+" + version.getVersionID());
					version.setState(MinecraftVersion.State.LOCAL);
					locals.add(version.getVersionID());
				});
			}
		});
	}

	@Override
	public void update() throws IOException
	{
		MinecraftAssetsManager manager = getInstance();
		List<MinecraftVersion> versionList = new ArrayList<>();

		boolean localChange = updateLocal();
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
		boolean remoteChange = updateRemoteVersion();
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
		if (!versionList.isEmpty())
			manager.refresh(versionList);
		List<WorldInfo> infos = new ArrayList<>();
		for (Path save : Files.walk(this.getContext().getRoot().resolve("saves"), 2)
				.filter(path -> path.getFileName().toString().equals("level.dat")).collect(Collectors.toList()))
			infos.add(WorldInfo.deserialize(save));
		if (!infos.isEmpty()) manager.refreshWorld(infos);
	}

	@Override
	public List<Language> getLanguages(MinecraftVersion version) throws IOException
	{
		if (version == null) return Collections.emptyList();
		MinecraftDirectory root = new MinecraftDirectory(getContext().getRoot().toFile());
		Version v = Versions.resolveVersion(root, version.getVersionID());
		Set<Asset> assets = Versions.resolveAssets(root, v);
		if (assets == null) return Collections.emptyList();
		List<File> collect = assets
				.stream().filter(asset -> asset.getVirtualPath().endsWith("pack.mcmeta"))
				.map(root::getAsset).collect(Collectors.toList());
		List<Language> languages = new ArrayList<>();
		Deserializer<Language[], JSONObject> deserializer = Language.deserializer();
		for (File file : collect)
			try {Collections.addAll(languages, deserializer.deserialize(IOUtils.toJson(file)));}
			catch (JSONException | IOException e) {e.printStackTrace();}
		return languages;
	}

	@Override
	public Image getIcon(WorldInfo worldInfo) throws IOException
	{
		return WorldInfo.getIcon(worldInfo, getContext().getRoot().resolve("saves"));
	}

	@Override
	public void saveWorldInfo(WorldInfo worldInfo) throws IOException
	{
		Path saves = getContext().getRoot().resolve("saves").resolve(worldInfo.getFileName()).resolve("level.dat");
		if (Files.exists(saves))
			WorldInfo.WRITER.writeTo(worldInfo, NBT.read(saves, true).asCompound());
		else throw new IOException();
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

	@Override
	public void onLaunch(LaunchOption option, LaunchProfile profile) throws Exception
	{
		MinecraftDirectory root = new MinecraftDirectory(getContext().getRoot().toFile());
		Path from;
		Path to;

		to = option.getMinecraftDirectory().getAssets().toPath();
		from = root.getAssets().toPath();
		Files.deleteIfExists(to);
		FetchUtils.fetch(from, to, Repository.FetchOption.HARD_LINK);

		to = option.getMinecraftDirectory().getLibraries().toPath();
		from = root.getLibraries().toPath();
		Files.deleteIfExists(to);
		FetchUtils.fetch(from, to, Repository.FetchOption.HARD_LINK);
	}

	@Override
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
		public boolean canMerge(IOGuardContext.IOTask task) {return task == this || task instanceof SaveTask;}

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
