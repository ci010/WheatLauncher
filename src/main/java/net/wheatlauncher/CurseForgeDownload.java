package net.wheatlauncher;

import javafx.concurrent.Task;
import net.launcher.services.curseforge.CurseForgeProjectArtifact;
import org.to2mbn.jmccc.mcdownloader.download.Downloader;

import java.nio.file.Path;

/**
 * @author ci010
 */
public class CurseForgeDownload
{
	Downloader downloader;

	public static Task<Void> createDonwloadTask(Path path, CurseForgeProjectArtifact artifact)
	{
		return new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				return null;
			}
		};
	}
}
