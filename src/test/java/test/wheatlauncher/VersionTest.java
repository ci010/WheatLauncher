package test.wheatlauncher;

import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.junit.Test;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;

import java.util.concurrent.ExecutionException;

/**
 * @author ci010
 */
public class VersionTest
{
	@Test
	public void singleVersionTest() throws InvalidVersionSpecificationException
	{
		String version = "1.8.9", greater = "1.10";
		ComparableVersion comparableVersion = new ComparableVersion(version);
		ComparableVersion greaterV = new ComparableVersion(greater);
		ComparableVersion same = new ComparableVersion(version);
		assert comparableVersion.compareTo(same) == 0 && comparableVersion.compareTo(greaterV) < 0;
	}

	@Test
	public void testVersionRange() throws InvalidVersionSpecificationException
	{
		String versionString = "[1.8.9]";
		VersionRange versionRange = VersionParser.parseRange(versionString);
		assert versionRange != null;
	}

	@Test
	public void testVersionList() throws ExecutionException, InterruptedException
	{
		MinecraftDownloader downloader = MinecraftDownloaderBuilder.buildDefault();
		System.out.println(downloader.fetchRemoteVersionList(null).get());
	}
}
