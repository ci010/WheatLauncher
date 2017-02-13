package net.launcher.version;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.launcher.game.Language;
import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.ComparableVersion;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;
import org.to2mbn.jmccc.version.Version;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class MinecraftAssetsManager
{
	private ObservableList<MinecraftVersion> versions = FXCollections.observableArrayList();
	private ObservableMap<String, MinecraftVersion> map = FXCollections.observableMap(new TreeMap<>());

	private AssetsRepository repository;

	public interface AssetsRepository
	{
		Version buildVersion(MinecraftVersion version) throws IOException;

		void fetchVersion(MinecraftVersion version);

		/**
		 * Update the version cache from both local and remote
		 */
		void update() throws IOException;

		List<Language> getLanguages(MinecraftVersion version) throws IOException;
	}

	MinecraftAssetsManager(AssetsRepository repository)
	{
		this.repository = repository;
	}

	public ObservableList<MinecraftVersion> getVersions() {return versions;}

	void register(List<MinecraftVersion> versions)
	{
		this.versions.addAll(versions);
		for (MinecraftVersion version : versions)
			this.map.put(version.getVersionID(), version);
		this.versions.sort((o1, o2) ->
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

	public boolean contains(String version)
	{
		return getVersion(version) != null;
	}

	public MinecraftVersion getVersion(String version)
	{
		if (version == null) return null;
		return map.get(version);
	}

	public ObservableMap<String, MinecraftVersion> getMap() {return map;}

	public AssetsRepository getRepository() {return repository;}
}
