package net.launcher.assets;

import api.launcher.MinecraftAssetsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import net.launcher.game.Language;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Version;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

/**
 * @author ci010
 */
class MinecraftAssetsManagerImpl implements MinecraftAssetsManager
{
	private ObservableList<MinecraftVersion> versions = FXCollections.observableArrayList();
	private ObservableMap<String, MinecraftVersion> map = FXCollections.observableMap(new TreeMap<>());
	private IOGuardMinecraftAssetsManager repository;

	MinecraftAssetsManagerImpl(ObservableList<MinecraftVersion> versions, ObservableMap<String, MinecraftVersion> map, IOGuardMinecraftAssetsManager repository)
	{
		this.versions = versions;
		this.map = map;
		this.repository = repository;
	}

	@Override
	public ObservableList<MinecraftVersion> getVersions() {return versions;}

	@Override
	public ObservableList<String> getAllVersions()
	{
		return null;
	}


	@Override
	public Version buildVersion(String version) throws IOException
	{
		return null;
	}

	@Override
	public boolean contains(String version)
	{
		return getVersion(version) != null;
	}

	@Override
	public String getLatest()
	{
		return null;
	}

	@Override
	public MinecraftVersion getVersion(String version)
	{
		if (version == null) return null;
		return map.get(version);
	}

	@Override
	public MinecraftVersion getLatestRelease()
	{
		return repository.getLatestRelease();
	}

	@Override
	public Version buildVersion(MinecraftVersion version) throws IOException {return repository.buildVersion(version);}

	@Override
	public Task<MinecraftVersion> fetchVersion(MinecraftVersion version) {return repository.fetchVersion(version);}

	@Override
	public Task<List<MinecraftVersion>> refreshVersion() {return repository.refreshVersion();}

	@Override
	public Task<Language[]> getLanguages(MinecraftVersion version) {return repository.getLanguages(version);}

	@Override
	public Task<Void> importMinecraft(MinecraftDirectory directory) {return repository.importMinecraft(directory);}

	@Override
	public Task<Void> exportVersion(MinecraftVersion version, Path target) {return repository.exportVersion(version, target);}

	public ObservableMap<String, MinecraftVersion> getMap() {return map;}

}
