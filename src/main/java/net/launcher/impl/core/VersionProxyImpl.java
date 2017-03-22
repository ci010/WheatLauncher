package net.launcher.impl.core;

import api.launcher.profile.VersionProxy;
import api.launcher.version.MinecraftVersion;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.TreeMap;

/**
 * @author ci010
 */
class VersionProxyImpl implements VersionProxy
{
	private ObjectProperty<MinecraftVersion> version;
	private StringProperty versionId;
	private ObservableMap<String, String> metaData;

	VersionProxyImpl()
	{
		version = new SimpleObjectProperty<>();
		versionId = new SimpleStringProperty();
		metaData = FXCollections.observableMap(new TreeMap<>());

		versionId.bind(Bindings.createStringBinding(() ->
		{
			MinecraftVersion v = version.get();
			if (v != null) return v.getVersionId();
			return "";
		}, version));
	}

	@Override
	public String getVersionId()
	{
		return versionId.get();
	}

	@Override
	public ReadOnlyStringProperty versionIdProperty()
	{
		return versionId;
	}

	void load(MinecraftVersion version)
	{
		this.version.set(version);
		if (version != null) Bindings.bindContent(version.getMetadata(), metaData);
	}

	@Override
	public ObservableMap<String, String> getMetadata()
	{
		return metaData;
	}
}
