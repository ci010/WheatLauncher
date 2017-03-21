package net.launcher.model;

import java.util.Map;

/**
 * @author ci010
 */
public class MinecraftVersionBase implements MinecraftVersion
{
	private String versionId;
	private Map<String, String> metaData;

	public MinecraftVersionBase(String versionId, Map<String, String> metaData)
	{
		this.versionId = versionId;
		this.metaData = metaData;
	}

	@Override
	public String getVersionId()
	{
		return versionId;
	}

	@Override
	public Map<String, String> getMetadata()
	{
		return metaData;
	}
}
