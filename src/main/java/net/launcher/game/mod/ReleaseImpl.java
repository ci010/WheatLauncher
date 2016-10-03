package net.launcher.game.mod;

import net.launcher.game.Mod;

/**
 * @author ci010
 */
public class ReleaseImpl implements Mod.Release
{
	private String modId;
	private String name;
	private String nickName;
	private String version;
	private ModMetaDataImpl metaData;

	public ReleaseImpl(String modid, ModMetaDataImpl metaData)
	{
		this.modId = modid;
		this.name = metaData.getInfo().getName() == null ? metaData.getAnnotation().getName() : metaData.getInfo().getName();
		this.version = metaData.getInfo().getVersion() == null ? metaData.getAnnotation().getVersion() : metaData.getInfo().getVersion();
		this.nickName = "";
		this.metaData = metaData;
	}

	@Override
	public String getModId()
	{
		return modId;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getNickName()
	{
		return nickName;
	}

	@Override
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	@Override
	public ModMetaDataImpl getMetaData()
	{
		return metaData;
	}
}
