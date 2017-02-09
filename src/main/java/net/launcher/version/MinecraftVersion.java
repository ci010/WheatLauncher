package net.launcher.version;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.TreeMap;

/**
 * @author ci010
 */
public class MinecraftVersion
{
	private StringProperty versionID = new SimpleStringProperty();
	private BooleanProperty inStorage = new SimpleBooleanProperty();
	private ObservableMap<String, Object> metadata = FXCollections.observableMap(new TreeMap<>());

	public MinecraftVersion(String versionID, boolean inStorage)
	{
		this.versionID.set(versionID);
		this.inStorage.set(inStorage);
	}

	public MinecraftVersion() {}

	public String getVersionID()
	{
		return versionID.get();
	}

	public ReadOnlyStringProperty versionIDProperty()
	{
		return versionID;
	}

	void setVersionID(String versionID)
	{
		this.versionID.set(versionID);
	}

	public boolean isInStorage()
	{
		return inStorage.get();
	}

	public ReadOnlyBooleanProperty inStorageProperty()
	{
		return inStorage;
	}

	void setInStorage(boolean inStorage)
	{
		this.inStorage.set(inStorage);
	}

	public ObservableMap<String, Object> getMetadata() {return metadata;}

	@Override
	public String toString() {return versionID.get();}
}
