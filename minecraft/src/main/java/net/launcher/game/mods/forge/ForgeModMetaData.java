package net.launcher.game.mods.forge;

import net.launcher.game.mods.ModContainer;
import net.launcher.game.mods.ModTypes;
import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.launcher.game.mods.internal.net.minecraftforge.fml.common.versioning.VersionRange;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author ci010
 */
public interface ForgeModMetaData
{
	static ModContainer<ForgeModMetaData> createMod(ForgeModMetaData metaData, Function<String, VersionRange> rangeParser)
	{
		Objects.requireNonNull(metaData);
		Objects.requireNonNull(rangeParser);
		DefaultArtifactVersion version = new DefaultArtifactVersion(metaData.getVersion());
		VersionRange spec = Optional.ofNullable(rangeParser.apply(metaData.getAcceptMinecraftVersion()))
				.orElseGet(() -> rangeParser.apply("[" + metaData.getMcVersion() + "]"));
		//TODO handle the spac iff mcVersion is null
		return new ModContainer<>(metaData, version, metaData.getName(), metaData.getVersion(), metaData.getDescription
				(), spec, ModTypes.FORGE);
	}

	String getModId();

	String getName();

	String getDescription();

	String getVersion();

	String getMcVersion();

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
