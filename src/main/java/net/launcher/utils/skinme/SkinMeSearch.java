package net.launcher.utils.skinme;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ci010
 */
public class SkinMeSearch
{
	private List<SkinMePreview> previewList = new ArrayList<>();
	private Map<SkinMePreview, SkinMeSkin> skinCache = new HashMap<>();
	private HttpRequester requester;

	public SkinMeSearch(HttpRequester requester)
	{
		this.requester = requester;
	}

	private void addPreview(String name, String id, String preview)
	{
		previewList.add(new SkinMePreview(name, id, preview));
	}

	public static SkinMeSearch search(HttpRequester requester, String keyWord) throws IOException
	{
		Map<String, String> header = new HashMap<>();
		header.put("id", "searchSkinForm");
		header.put("search", "123");
		String post = requester.requestWithPayload(
				"POST", "http://www.skinme.cc:80/skinme/skin/charCore", new byte[1024], "json", header);
		System.out.println(post);
//		String content = requester.request("GET", "http://www.skinme.cc/skinme/index/skinCore?searchVal=" + keyWord);
//		System.out.println(content);

//		Document doc = Jsoup.parseToIndexURL(content);
//		Element form = doc.getElementById("search-skin");

//		System.out.println(form);
		System.out.println("Start parsing");
//		System.out.println(elementsByClass);
//		SkinMeSearch search = new SkinMeSearch(requester);
//		for (Element elementsByClas : elementsByClass)
//		{
//			System.out.println(elementsByClas);
//			String preview = elementsByClas.getElementsByClass("imgSkin").get(0).attr("src");
//			Element element = elementsByClas.getElementsByClass("icon-heart").get(0);
//			String nameProperty = element.attr("data-nameProperty");
//			String id = element.attr("data-id");
//			search.addPreview(nameProperty, id, preview);
//		}
//
//		for (SkinMePreview skinMePreview : search.previewList)
//			System.out.println(skinMePreview);
//		return search;
		return null;
	}

	public SkinMeSkin getSkin(SkinMePreview preview) throws IOException
	{
		String url = "http://www.skinme.cc:80/skinme/skin?sourceId=" + preview.getId() + "&&type=1";
		String content = requester.request("GET", url);

		Document doc = Jsoup.parse(content);
		Elements tag = doc.getElementsByClass("skindeail_table").get(0).getElementsByTag("td");
		for (Element element : tag)
		{
			System.out.println(element);
		}
		return null;
	}

	public class SkinMePreview
	{
		private String name, id, url;

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			SkinMePreview that = (SkinMePreview) o;

			if (name != null ? !name.equals(that.name) : that.name != null) return false;
			if (id != null ? !id.equals(that.id) : that.id != null) return false;
			return url != null ? url.equals(that.url) : that.url == null;

		}

		@Override
		public int hashCode()
		{
			return previewList.indexOf(this);
		}

		public SkinMePreview(String name, String id, String url)
		{
			this.name = name;
			this.id = id;
			this.url = url;
		}

		public String getName()
		{
			return name;
		}

		public String getId()
		{
			return id;
		}

		public String getUrl()
		{
			return url;
		}

		@Override
		public String toString()
		{
			return "SkinMePreview{" +
					"url='" + url + '\'' +
					", id='" + id + '\'' +
					", nameProperty='" + name + '\'' +
					'}';
		}
	}
}
