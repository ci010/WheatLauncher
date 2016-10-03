package net.launcher.game.mod;

/**
 * @author ci010
 */
public class ModMetaDataImpl implements ModMetaData
{
	private ModInfo info;
	private RuntimeAnnotation annotation;

	public ModMetaDataImpl(ModInfo info, RuntimeAnnotation annotation)
	{
		this.info = info;
		this.annotation = annotation;
	}

	@Override
	public String getDescription() {return info.getDescription();}

	@Override
	public String getUpdateJSON() {return info.getUpdateJSON();}

	@Override
	public String getUrl() {return info.getUrl();}

	@Override
	public String getLogoFile() {return info.getLogoFile();}

	@Override
	public String[] getAuthorList() {return info.getAuthorList();}

	@Override
	public String getCredits() {return info.getCredits();}

	@Override
	public String getParent() {return info.getParent();}

	@Override
	public String[] getScreenshots() {return info.getScreenshots();}

	@Override
	public String getMcVersion() {return annotation.getMcVersion();}

	@Override
	public String getFingerprint() {return annotation.getFingerprint();}

	@Override
	public String getUpdateJson() {return annotation.getUpdateJson();}

	@Override
	public String getDependencies() {return annotation.getDependencies();}

	@Override
	public boolean isRemoteVersion() {return annotation.isRemoteVersion();}

	@Override
	public boolean isSaveVersion() {return annotation.isSaveVersion();}

	@Override
	public boolean isClientOnly() {return annotation.isClientOnly();}

	@Override
	public boolean isSeverOnly() {return annotation.isSeverOnly();}

	@Override
	public String getCollapsedName()
	{
		return null;
	}

	@Override
	public String getCollapsedVersion()
	{
		return null;
	}

	public boolean isNameCollapsed()
	{
		return !annotation.getName().equals(info.getName());
	}

	public boolean isVersionCollapsed()
	{
		return !annotation.getVersion().equals(info.getVersion());
	}

	public ModInfo getInfo()
	{
		return info;
	}

	public RuntimeAnnotation getAnnotation()
	{
		return annotation;
	}
}
