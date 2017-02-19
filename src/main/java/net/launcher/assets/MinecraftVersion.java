package net.launcher.assets;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.TreeMap;

/**
 * @author ci010
 */
public class MinecraftVersion
{
	public enum State
	{
		DOWNLOADING, REMOTE, LOCAL
	}

	private StringProperty versionID = new SimpleStringProperty();
	private ObjectProperty<State> state = new SimpleObjectProperty<>(State.LOCAL);
	private ObservableMap<String, Object> metadata = FXCollections.observableMap(new TreeMap<>());

	public MinecraftVersion(String versionID, State inStorage)
	{
		this.versionID.set(versionID);
		state.set(inStorage);
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

	public State getState()
	{
		return state.get();
	}

	public ReadOnlyObjectProperty<State> stateProperty()
	{
		return state;
	}

	void setState(State state)
	{
		this.state.set(state);
	}

	public ObservableMap<String, Object> getMetadata() {return metadata;}

	@Override
	public String toString() {return versionID.get();}
}
