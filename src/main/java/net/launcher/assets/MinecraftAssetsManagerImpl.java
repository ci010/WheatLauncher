package net.launcher.assets;

import api.launcher.MinecraftAssetsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.TreeMap;

/**
 * @author ci010
 */
class MinecraftAssetsManagerImpl implements MinecraftAssetsManager
{
	private ObservableList<MinecraftVersion> versions = FXCollections.observableArrayList();
	private ObservableMap<String, MinecraftVersion> map = FXCollections.observableMap(new TreeMap<>());
	private AssetsRepository repository;

	MinecraftAssetsManagerImpl(ObservableList<MinecraftVersion> versions, ObservableMap<String, MinecraftVersion> map, AssetsRepository repository)
	{
		this.versions = versions;
		this.map = map;
		this.repository = repository;
	}

	@Override
	public ObservableList<MinecraftVersion> getVersions() {return versions;}

	@Override
	public boolean contains(String version)
	{
		return getVersion(version) != null;
	}

	@Override
	public MinecraftVersion getVersion(String version)
	{
		if (version == null) return null;
		return map.get(version);
	}

	public ObservableMap<String, MinecraftVersion> getMap() {return map;}

	@Override
	public AssetsRepository getRepository() {return repository;}
}
