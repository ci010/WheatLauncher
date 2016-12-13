package net.launcher.services.curseforge;

import java.util.Date;
import java.util.Optional;

/**
 * @author ci010
 */
public class CurseForgeProjectArtifact
{
	private String downloadURL;
	private String fileName;
	private String mcVersion;
	private String releaseType;
	private long fileSize;
	private Date date;

	CurseForgeProjectArtifact(String downloadURL, String fileName, String mcVersion, String releaseType, long fileSize, Date date)
	{
		this.downloadURL = downloadURL;
		this.fileName = fileName;
		this.mcVersion = mcVersion;
		this.releaseType = releaseType;
		this.fileSize = fileSize;
		this.date = date;
	}


	public String getDownloadURL()
	{
		return downloadURL;
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getGameVersion()
	{
		return mcVersion;
	}

	public String getReleaseType()
	{
		return releaseType;
	}

	public long getFileSize()
	{
		return fileSize;
	}

	public Optional<Date> getDate()
	{
		return Optional.ofNullable(date);
	}

	@Override
	public String toString()
	{
		return "CurseForgeProjectArtifact{" +
				"downloadURL='" + downloadURL + '\'' +
				", fileName='" + fileName + '\'' +
				", mcVersion='" + mcVersion + '\'' +
				", releaseType='" + releaseType + '\'' +
				", fileSize=" + fileSize +
				", date=" + date +
				'}';
	}
}
