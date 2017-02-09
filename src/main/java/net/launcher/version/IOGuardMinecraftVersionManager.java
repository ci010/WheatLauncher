package net.launcher.version;

import javafx.application.Platform;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.wheatlauncher.internal.io.IOGuard;
import net.wheatlauncher.internal.io.IOGuardContext;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CombinedDownloadCallback;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Version;
import org.to2mbn.jmccc.version.parsing.Versions;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class IOGuardMinecraftVersionManager extends IOGuard<MinecraftVersionManager> implements MinecraftVersionManager.VersionRepository
{
	@Override
	public void forceSave() throws IOException
	{
		MinecraftVersionManager instance = this.getInstance();
		if (instance == null) return;
		Path data = getContext().getRoot().resolve("versions.dat");
		NBT.write(data, saveThis(), false);
	}

	@Override
	public MinecraftVersionManager loadInstance() throws IOException
	{
		Path data = getContext().getRoot().resolve("versions.dat");
		NBTCompound compound = NBT.read(data, false).asCompound();
		this.lastModified = compound.get("lastModified").asString();
		this.cache = readVersionList(compound.get("cache").asCompound());
		return new MinecraftVersionManager(this);
	}

	@Override
	public MinecraftVersionManager defaultInstance()
	{
		return new MinecraftVersionManager(this);
	}

	@Override
	protected void deploy()
	{
		MinecraftVersionManager instance = this.getInstance();
		getContext().registerSaveTask(new SaveTask(), instance.getVersions());
	}

	@Override
	public Version buildVersion(MinecraftVersion version)
	{
		MinecraftDirectory minecraftDirectory = new MinecraftDirectory(getContext().getRoot().toFile());
		try
		{
			return Versions.resolveVersion(minecraftDirectory, version.getVersionID());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void fetchVersion(MinecraftVersion version, CombinedDownloadCallback<Version> callback)
	{
		if (version.isInStorage()) return;
		getContext().enqueue(root ->
		{
			MinecraftDirectory minecraftDirectory = new MinecraftDirectory(root.toFile());
			MinecraftDownloader downloader = MinecraftDownloaderBuilder.buildDefault();
			downloader.downloadIncrementally(minecraftDirectory, version.getVersionID(), callback).get();
			Platform.runLater(() ->
			{
				version.setInStorage(true);
				locals.add(version.getVersionID());
			});
		});
	}

	@Override
	public void update() throws IOException
	{
		MinecraftVersionManager manager = getInstance();
		List<MinecraftVersion> versionList = new ArrayList<>();

		boolean localChange = updateLocal();
		if (localChange)
			for (String version : locals)
			{
				MinecraftVersion contained = manager.getVersion(version);
				if (contained != null)
				{
					contained.setInStorage(true);
					continue;
				}
				MinecraftVersion v = new MinecraftVersion(version, true);
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
			Set<String> remotes = cache.getVersions().keySet();
			remotes.removeAll(locals);
			for (String version : remotes)
			{
				MinecraftVersion contained = manager.getVersion(version);
				if (contained != null)
				{
					if (contained.isInStorage())
						contained.setInStorage(false);
					continue;
				}
				MinecraftVersion v = new MinecraftVersion(version, false);
				RemoteVersion remote = cache.getVersions().get(version);
				v.getMetadata().put("remote", remote);
				versionList.add(v);
			}
		}
		if (!versionList.isEmpty())
			manager.register(versionList);
	}

	private Set<String> locals = Collections.synchronizedSet(new TreeSet<>());
	private String lastModified;
	private RemoteVersionList cache;

	private boolean updateLocal()
	{
		MinecraftDirectory minecraftDirectory = new MinecraftDirectory(getContext().getRoot().toFile());
		Set<String> versions = Versions.getVersions(minecraftDirectory);
		boolean changed = versions.equals(locals);
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
		String get = requester.request("GET", "https://launchermeta.mojang.com/mc/game/version_manifest.json", header);
		if (get == null || get.equals("")) return false;
		cache = RemoteVersionList.fromJson(new JSONObject(get));
		lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).format(Calendar.getInstance()
				.getTime());
		this.getContext().enqueue(new SaveTask());
		return true;
	}

	class SaveTask implements IOGuardContext.IOTask
	{
		@Override
		public void performance(Path root) throws IOException
		{
			Path resolve = root.resolve("versions.dat");
			MinecraftVersionManager instance = getInstance();
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
