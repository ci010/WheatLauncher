package net.launcher.impl.core;

import api.launcher.View;
import api.launcher.auth.AuthorizeProxy;
import api.launcher.event.RegisterAuthEvent;
import api.launcher.profile.Profile;
import api.launcher.version.MinecraftVersion;
import api.launcher.version.MinecraftVersionBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.launcher.game.ServerInfo;
import net.launcher.game.ServerInfoBase;
import net.launcher.game.WorldInfo;
import net.launcher.game.nbt.NBT;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.game.nbt.NBTList;
import net.launcher.utils.serial.BiSerializer;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersionList;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.parsing.Versions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class CoreAlgHelper
{
	static List<ServerInfo> loadServer(Path rot) throws Exception
	{
		NBTCompound read = NBT.read(rot.resolve("servers.dat"), false).asCompound();
		BiSerializer<ServerInfo, NBTCompound> serializer = ServerInfoBase.serializer();
		return read.get("servers").asList().stream().map(NBT::asCompound)
				.map(serializer::deserialize).collect(Collectors.toList());
	}

	static List<WorldInfo> loadWorld(Path rot) throws Exception
	{
		List<WorldInfo> infos = new ArrayList<>();
		for (Path save : Files.walk(rot.resolve("saves"), 2)
				.filter(path -> path.getFileName().toString().equals("level.dat")).collect(Collectors.toList()))
			infos.add(WorldInfo.deserialize(save));
		return infos;
	}

	static ObservableList<MinecraftVersion> loadVersions(NBTCompound compound)
	{
		NBTList versions = compound.get("versions").asList();
		return FXCollections.observableArrayList(versions.stream().map(nbt ->
		{
			NBTCompound v = nbt.asCompound();
			return new MinecraftVersionBase(v.get("id").asString(), v.get("meta").asCompound()
					.asRaw().entrySet().stream().collect(Collectors
							.toMap(Map.Entry::getKey, value -> value.getValue().toString())));
		}).collect(Collectors.toList()));
	}

	static String updateVersion(Path root, String lastModified, View<MinecraftVersion> view,
								ObservableList<MinecraftVersion> src) throws Exception
	{
		MinecraftDirectory minecraftDirectory = new MinecraftDirectory(root.toFile());
		Set<String> localVersions = Versions.getVersions(minecraftDirectory);

		String content = null;
		try
		{
			content = new HttpRequester().request("GET", "https://launchermeta.mojang.com/mc/game/version_manifest.json",
					lastModified == null ? Collections.emptyMap() : Collections.singletonMap("If-Modified-Since", lastModified));
		}
		catch (IOException ignored) {}
		if (content == null || content.equals(""))
		{
			for (MinecraftVersion version : view)
				if (localVersions.contains(version.getVersionId()))
				{
					version.getMetadata().put("state", "local");
					localVersions.remove(version.getVersionId());
				}
			for (String version : localVersions)
				view.add(new MinecraftVersionBase(version, new TreeMap<>(Collections.singletonMap("state", "local"))));
		}
		else
		{
			lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
					.format(Calendar.getInstance().getTime());
			RemoteVersionList list = RemoteVersionList.fromJson(new JSONObject(content));
			List<MinecraftVersion> versions = list.getVersions().values().stream().map(version ->
			{
				Map<String, String> map = new TreeMap<>();
				decorateVersionMap(map, version);
				if (localVersions.contains(version.getVersion()))
				{
					map.put("state", "local");
					localVersions.remove(version.getVersion());
				}
				return new MinecraftVersionBase(version.getVersion(), map);
			}).collect(Collectors.toList());
			if (!localVersions.isEmpty())
				for (String s : localVersions)
					versions.add(new MinecraftVersionBase(s, new TreeMap<>(Collections.singletonMap("state", "local"))));
			src.setAll(versions);
		}
		return lastModified;
	}

	static private void decorateVersionMap(Map<String, String> map, RemoteVersion version)
	{
		map.put("type", version.getType());
		map.put("url", version.getUrl());
		map.put("releaseTime", DateFormat.getDateInstance().format(version.getReleaseTime()));
		map.put("uploadTime", DateFormat.getDateInstance().format(version.getUploadTime()));
		map.put("state", "remote");
	}

	static List<Profile> updateLocalProf(Path rot) throws IOException
	{
		List<Profile> profilesList = new ArrayList<>();
		Files.walkFileTree(rot.resolve("profiles"), Collections.emptySet(), 2, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
			{
				Path prof = dir.resolve("profile.dat");
				if (!Files.exists(prof)) return super.preVisitDirectory(dir, attrs);
				ObjectInputStream prfStream = new ObjectInputStream(Files.newInputStream(prof));
				Profile deserialize = null;
				try
				{
					deserialize = (Profile) prfStream.readObject();
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				if (deserialize == null) return super.preVisitDirectory(dir, attrs);
				profilesList.add(deserialize);
				return super.preVisitDirectory(dir, attrs);
			}
		});
		return profilesList;
	}

	static AuthorizeProxy loadProxy(Path r, RegisterAuthEvent event) throws IOException
	{
		AuthorizeProxy authorizeProxy = new AuthorizeProxyImpl();
		Path path = r.resolve("auth.dat");
		NBTCompound compound = NBT.read(path, false).asCompound();
		String account = compound.get("account").asString("");
		String auth = compound.get("auth").asString("");
		authorizeProxy.setAccount(account);
		authorizeProxy.load(event.getRegistered().get(auth));
		return authorizeProxy;
	}
}
