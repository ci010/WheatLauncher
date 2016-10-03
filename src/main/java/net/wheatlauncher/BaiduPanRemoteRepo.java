package net.wheatlauncher;

import net.launcher.game.Mod;
import net.launcher.utils.resource.RemoteArchiveRepository;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * @author ci010
 */
public class BaiduPanRemoteRepo extends RemoteArchiveRepository<Mod.Release[]>
{
	private String urlRoot = "http://bdbea3.duapp.com/pcs_download.php?id=3281&link=%2Fapps%2Fhgf_blog%2F";

	public BaiduPanRemoteRepo(File root)
	{
		super(root);
	}

	protected String parseToIndexURL(String url) throws IOException
	{
		HttpURLConnection connection =
				(HttpURLConnection) new URL(url).openConnection(getProxy() == null ? Proxy.NO_PROXY : getProxy());
		connection.setReadTimeout(20000);
		connection.setConnectTimeout(10000);
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Accept-Encoding", "gzip");
		connection.setRequestMethod("GET");
		connection.connect();

		String location = connection.getHeaderField("Location"); //just redirect
		connection.disconnect();
		return location;
	}

	@Override
	protected String toURL(String path)
	{
		return urlRoot + path.replace("/", "&2F").replace(" ", "+");
	}
}
