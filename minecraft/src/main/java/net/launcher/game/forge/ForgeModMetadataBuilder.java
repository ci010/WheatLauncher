package net.launcher.game.forge;/**
 * @author ci010
 */

import javafx.util.Builder;
import net.launcher.utils.StringUtils;

public class ForgeModMetadataBuilder implements Builder<ForgeModMetaData>
{
	public static ForgeModMetadataBuilder create() {return new ForgeModMetadataBuilder();}

	@Override
	public ForgeModMetaData build()
	{
		return new MetaDataImpl(modId, description, updateJSON, url, logoFile, authorList, screenshots,
				credits, parent, version, name, mcVersion, fingerprint, dependencies, acceptableMinecraftVersion,
				acceptableRemoteVersions, acceptableSaveVersions, clientOnly, severOnly);
	}

	private ForgeModMetadataBuilder() {}

	public void setModId(String modId)
	{
		this.modId = modId;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setUpdateJSON(String updateJSON)
	{
		this.updateJSON = updateJSON;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public void setLogoFile(String logoFile)
	{
		this.logoFile = logoFile;
	}

	public void setAuthorList(String[] authorList)
	{
		this.authorList = authorList;
	}

	public void setScreenshots(String[] screenshots)
	{
		this.screenshots = screenshots;
	}

	public void setCredits(String credits)
	{
		this.credits = credits;
	}

	public void setParent(String parent)
	{
		this.parent = parent;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setMcVersion(String mcVersion)
	{
		this.mcVersion = mcVersion;
	}

	public void setFingerprint(String fingerprint)
	{
		this.fingerprint = fingerprint;
	}

	public void setDependencies(String dependencies)
	{
		this.dependencies = dependencies;
	}

	public void setAcceptableRemoteVersions(String acceptableRemoteVersions)
	{
		this.acceptableRemoteVersions = acceptableRemoteVersions;
	}

	public void setAcceptableSaveVersions(String acceptableSaveVersions)
	{
		this.acceptableSaveVersions = acceptableSaveVersions;
	}

	public ForgeModMetadataBuilder setAcceptableMinecraftVersion(String acceptableMinecraftVersion)
	{
		this.acceptableMinecraftVersion = acceptableMinecraftVersion;
		return this;
	}

	public void setClientOnly(boolean clientOnly)
	{
		this.clientOnly = clientOnly;
	}

	public void setSeverOnly(boolean severOnly)
	{
		this.severOnly = severOnly;
	}

	private String modId = StringUtils.EMPTY;
	private String description = StringUtils.EMPTY;
	private String updateJSON = StringUtils.EMPTY;
	private String url = StringUtils.EMPTY;

	private String logoFile = StringUtils.EMPTY;
	private String[] authorList;
	private String[] screenshots;
	private String credits = StringUtils.EMPTY;
	private String parent = StringUtils.EMPTY;
	private String version = StringUtils.EMPTY,
			name = StringUtils.EMPTY,
			mcVersion = StringUtils.EMPTY,
			fingerprint = StringUtils.EMPTY,
			dependencies = StringUtils.EMPTY,
			acceptableRemoteVersions = StringUtils.EMPTY,
			acceptableMinecraftVersion = StringUtils.EMPTY,
			acceptableSaveVersions = StringUtils.EMPTY;

	private boolean clientOnly, severOnly;
}
