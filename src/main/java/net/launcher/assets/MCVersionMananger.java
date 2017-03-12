package net.launcher.assets;

import javafx.collections.ObservableList;

/**
 * @author ci010
 */
public interface MCVersionMananger
{
	ObservableList<McVersion> versionList();

	McVersion getVersion(String versionId);

	McVersion getLatestRelease();

	McVersion getLatestSnapshot();


}
