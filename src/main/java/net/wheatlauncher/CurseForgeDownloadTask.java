package net.wheatlauncher;

import javafx.concurrent.Task;
import net.launcher.services.curseforge.CurseForgeProjectArtifact;
import org.to2mbn.jmccc.mcdownloader.download.Downloader;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.FileDownloadTask;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author ci010
 */
public class CurseForgeDownloadTask extends Task<Path> implements DownloadCallback<Void>
{
	private Downloader downloader;
	private CurseForgeProjectArtifact artifact;

	private Path target;

	public CurseForgeDownloadTask(Downloader downloader, CurseForgeProjectArtifact artifact, Path target)
	{
		this.artifact = artifact;
		this.downloader = downloader;
		this.target = target;
		this.updateTitle("DownloadCurseForgeProject");
	}

	@Override
	protected Path call() throws Exception
	{
		HttpURLConnection conn = (HttpURLConnection) new URL(artifact.getDownloadURL()).openConnection();
		conn.setInstanceFollowRedirects(true);
		String newUrl = conn.getHeaderField("Location");
		URLConnection urlConnection = new URL(newUrl).openConnection();
		urlConnection.setAllowUserInteraction(true);
		newUrl = urlConnection.getHeaderField("Location");
		Path resolve = target.resolve(artifact.getFileName());
		if (Files.exists(resolve)) throw new FileAlreadyExistsException(resolve.toString());
		int i = newUrl.lastIndexOf('/');
		newUrl = newUrl.substring(0, i) + URLEncoder.encode(newUrl.substring(i), "GBK");
		downloader.download(new FileDownloadTask(newUrl, resolve.toFile()), this).get();
		return target;
	}

	@Override
	public void done(Void result) {}

	@Override
	public void failed(Throwable e) {}

	@Override
	public void cancelled() {super.cancelled();}

	@Override
	public void updateProgress(long done, long total) {super.updateProgress(done, total);}

	@Override
	public void retry(Throwable e, int current, int max) {}
}
