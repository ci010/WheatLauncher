package net.launcher.game.liteloader;

import net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning.ComparableVersion;

/**
 * @author ci010
 */
public class LiteMod
{
	private LiteModMetaData metaData;
	private ComparableVersion mcVersion;

	public LiteMod(LiteModMetaData metaData)
	{
		this.metaData = metaData;
		this.mcVersion = new ComparableVersion(metaData.getMcVersion());
	}

	public LiteModMetaData getMetaData() {return metaData;}

	public ComparableVersion getMcVersion() {return mcVersion;}
}
