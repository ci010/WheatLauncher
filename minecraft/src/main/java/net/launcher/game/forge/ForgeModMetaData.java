package net.launcher.game.forge;

/**
 * @author ci010
 */
public interface ForgeModMetaData
{
	String getModId();

	String getName();

	String getDescription();

	String getVersion();

	String getAcceptMinecraftVersion();

	String getUpdateJSON();

	String getUrl();

	String getLogoFile();

	String[] getAuthorList();

	String getCredits();

	String getParent();

	String[] getScreenshots();

	String getFingerprint();

	String getDependencies();

	String acceptableRemoteVersions();

	String acceptableSaveVersions();

	boolean isClientOnly();

	boolean isSeverOnly();

	String getAlternativeName();

	String getCollapsedVersion();
}
