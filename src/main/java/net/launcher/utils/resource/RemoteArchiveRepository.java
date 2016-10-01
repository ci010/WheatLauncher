package net.launcher.utils.resource;

import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.mcdownloader.download.Downloader;
import org.to2mbn.jmccc.mcdownloader.download.DownloaderBuilders;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ci010
 */
public abstract class RemoteArchiveRepository<T>
{
	private Proxy proxy;
	private Map<String, JSONObject> cachedJson = new HashMap<>();

	private File root;

	public RemoteArchiveRepository(File localRoot)
	{
		this.root = localRoot;
	}

	public Proxy getProxy()
	{
		return proxy;
	}

	public void setProxy(Proxy proxy)
	{
		this.proxy = proxy;
	}

	protected abstract String parseToIndexURL(String url) throws IOException;

	protected abstract String toURL(String path);

	private String toIndexPath(String path)
	{
		return "index/" + path + ".json";
	}

	private String toCachePath(String hash, ResourceType type)
	{
		return "cache/" + hash + type.getSuffix();
	}

	public boolean hasResource(String path) throws IOException
	{
		if (cachedJson.containsKey(path)) return true;
		String indexPath = toIndexPath(path);
		String location = this.parseToIndexURL(toURL(indexPath));

		HttpRequester requester = new HttpRequester(proxy == null ? Proxy.NO_PROXY : proxy);
		JSONObject result = new JSONObject(requester.request("GET", location));

		boolean has = result.has("hash") && result.has("type") && result.has("data");
		if (has) cachedJson.put(path, result);
		return has;
	}

	public void fetch(String path, DownloadCallback<Void> callback) throws IOException, URISyntaxException
	{
		if (this.hasResource(path))
		{
			JSONObject jsonObject = cachedJson.get(path);
			ResourceType type = ResourceType.valueOf(jsonObject.getString("type"));
			String hash = jsonObject.getString("hash");
			String cachePath = toCachePath(hash, type);
			Downloader build = DownloaderBuilders.downloader().build();
			build.download(new FileDownloadTask(new URL(toURL(cachePath)).toURI(), new File(root, cachePath)), callback);
		}
		else throw new IOException();
	}
}
