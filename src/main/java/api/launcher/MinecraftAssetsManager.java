package api.launcher;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import net.launcher.assets.MinecraftVersion;
import net.launcher.game.Language;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Version;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * The manager that handle the common centralized assets of Minecraft
 *
 * @author ci010
 */
public interface MinecraftAssetsManager
{
	/**
	 * @return All the minecraft version.
	 */
	ObservableList<MinecraftVersion> getVersions();

	ObservableList<String> getAllVersions();

	Version buildVersion(String version) throws IOException;

	/**
	 * @param version The version string.
	 * @return if the repository contains this version.
	 */
	boolean contains(String version);

	String getLatest();

	/**
	 * @param version The version string
	 * @return The minecraft version instance
	 */
	MinecraftVersion getVersion(String version);

	MinecraftVersion getLatestRelease();

	/**
	 * Build a version for launcher by the {@link MinecraftVersion}.
	 *
	 * @param version the version instance
	 * @return the parsed version info
	 */
	Version buildVersion(MinecraftVersion version) throws IOException;

	/**
	 * @param version the version wants to fetch
	 */
	Task<MinecraftVersion> fetchVersion(MinecraftVersion version);

	/**
	 * Update the version cache from both local and remote
	 */
	Task<List<MinecraftVersion>> refreshVersion();

	Task<Language[]> getLanguages(MinecraftVersion version);

	Task<Void> importMinecraft(MinecraftDirectory directory);

	Task<Void> exportVersion(MinecraftVersion version, Path target);
}
