package net.launcher.game.mod;

/**
 * @author ci010
 */
public interface ModMetaData
{
	String getDescription();

	String getUpdateJSON();

	String getUrl();

	String getLogoFile();

	String[] getAuthorList();

	String getCredits();

	String getParent();

	String[] getScreenshots();

	String getMcVersion();

	String getFingerprint();

	String getUpdateJson();

	String getDependencies();

	boolean isRemoteVersion();

	boolean isSaveVersion();

	boolean isClientOnly();

	boolean isSeverOnly();

	String getCollapsedName();

	String getCollapsedVersion();
}
