package test.wheatlauncher;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpUtils;
import org.to2mbn.jmccc.util.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;

/**
 * @author ci010
 */
public class TestSkinme
{
	@Test
	public void testSkinPreview() throws IOException
	{
		Proxy proxy = Proxy.NO_PROXY;
		String url = "http://crafat.skinme.cc/renders/body/skinme651470";
		URL url1 = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) url1.openConnection(proxy);
		connection.setRequestMethod("GET");
		connection.connect();
		byte[] bytes = IOUtils.toByteArray(connection.getInputStream());
		Pair<Integer, Integer> a = PngTest.wh(bytes);
		byte[] bytes1 = IOUtils.toByteArray(url1.openStream());
		Pair<Integer, Integer> b = PngTest.wh(bytes1);
		assert a.equals(b);
	}

	@Test
	public void testMCNet() throws IOException
	{
		String keyword = "";
		String search = "http://www.minecraftskins.net/search/" + keyword;
		HttpRequester httpRequester = new HttpRequester();
		String get = httpRequester.request("GET", "http://www.minecraftskins.net/winterboy/", Collections
				.singletonMap("Content-GameSetting", HttpUtils.CONTENT_TYPE_JSON));
		Document parse = Jsoup.parse(get);
		Element fa = parse.getElementsByClass("card-content").get(0);
		String author = fa.child(0).text().substring("Designed by ".length());
		String descrip = fa.child(1).text();
		System.out.println(author);
		System.out.println(descrip);
	}

	@Test
	public void testDown() throws IOException, Base64DecodingException
	{
		HttpRequester httpRequester = new HttpRequester();
		URL url = new URL("http://www.minecraftskins.net/winterboy/download");
		byte[] bytes = IOUtils.toByteArray(url.openStream());
		Pair<Integer, Integer> wh1 = PngTest.wh(bytes);
		System.out.println(wh1);
//		String get = httpRequester.request("GET", "http://www.minecraftskins.net/winterboy/download");
//		Document parse = Jsoup.parse(get);
//		Element element = parse.getElementsByTag("body").get(0);
//		Pair<Integer, Integer> wh = PngTest.wh(element.text().getBytes());
//		System.out.println(wh);
//		System.out.println(element.text());
	}

}
