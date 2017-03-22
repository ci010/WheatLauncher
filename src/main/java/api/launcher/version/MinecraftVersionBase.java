package api.launcher.version;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;

/**
 * @author ci010
 */
public class MinecraftVersionBase implements MinecraftVersion
{
	private String versionId;
	private ObservableMap<String, String> metaData;

	public MinecraftVersionBase(String versionId, Map<String, String> metaData)
	{
		this.versionId = versionId;
		this.metaData = FXCollections.observableMap(metaData);
	}

	@Override
	public String getVersionId()
	{
		return versionId;
	}

	@Override
	public ObservableMap<String, String> getMetadata()
	{
		return metaData;
	}
}
