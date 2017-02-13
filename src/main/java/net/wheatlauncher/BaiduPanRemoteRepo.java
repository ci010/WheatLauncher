package net.wheatlauncher;


/**
 * @author ci010
 */
public class BaiduPanRemoteRepo //extends ArchiveRepositoryBase.EmbeddedRemoteArchiveRepository<ForgeMod.ForgeMod[]>
{
	private String urlRoot = "http://bdbea3.duapp.com/pcs_download.php?id=3281&link=%2Fapps%2Fhgf_blog%2F";

//	public BaiduPanRemoteRepo(Path root)
//	{
//		super(root);
//	}
//
//	protected String pathToURL(String path) throws IOException
//	{
//		path = urlRoot + path.replace("/", "&2F").replace(" ", "+");
//		HttpURLConnection connection =
//				(HttpURLConnection) new URL(path).openConnection(getProxy() == null ? Proxy.NO_PROXY : getProxy());
//		connection.setReadTimeout(20000);
//		connection.setConnectTimeout(10000);
//		connection.setRequestProperty("Accept", "*/*");
//		connection.setRequestProperty("Connection", "keep-alive");
//		connection.setRequestProperty("Accept-Encoding", "gzip");
//		connection.setRequestMethod("GET");
//		connection.connect();
//
//		String location = connection.getHeaderField("Location"); //just redirect
//		connection.disconnect();
//		return location;
//	}
}
