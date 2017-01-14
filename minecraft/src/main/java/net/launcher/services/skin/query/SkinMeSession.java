package net.launcher.services.skin.query;

import net.launcher.utils.Tasks;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Textures;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

/**
 * @author ci010
 */
class SkinMeSession implements SkinQueryService.Session
{
	private String keyWord;
	private byte dataType;
	private ExecutorService service;
	private HttpRequester requester;
	private int capacity = -1;

	private int cachedIndex;
	private List<Skin> cache;

	static FutureTask<SkinQueryService.Session> create(String keyWord, byte dataType, ExecutorService service, HttpRequester requester,
													   Callback<SkinQueryService.Session> callback)
	{
		SkinMeSession skinMeSession = new SkinMeSession(keyWord, dataType, service, requester);
		FutureTask<SkinQueryService.Session> future = new FutureTask<>(() -> skinMeSession);
		skinMeSession.submit(1, new CallbackAdapter<List<Skin>>()
		{
			@Override
			public void done(List<Skin> result)
			{
				future.run();
				callback.done(skinMeSession);
			}

			@Override
			public void failed(Throwable e) {callback.failed(e);}
		});
		return future;
	}

	private SkinMeSession(String keyWord, byte dataType, ExecutorService service, HttpRequester requester)
	{
		this.keyWord = keyWord;
		this.dataType = dataType;
		this.service = service;
		this.requester = requester;
	}

	@Override
	public String sessionSource()
	{
		return "skinme";
	}

	@Override
	public TextureType skinType()
	{
		return TextureType.SKIN;
	}

	@Override
	public int totalPages()
	{
		return capacity;
	}

	private void submit(int index, Callback<List<Skin>> callback)
	{
		service.submit(Tasks.wrap(new QueryPage(index), callback));
	}

	@Override
	public void query(int index, Callback<List<Skin>> callback)
	{
		if (service.isTerminated()) throw new IllegalStateException("The viewSession service is already closed!");
		if (index >= capacity || index <= 0)
		{
			if (callback != null)
				callback.failed(new IndexOutOfBoundsException("The totalPages is only " + capacity));
			return;
		}
		if (index == cachedIndex && callback != null)
			callback.done(cache);
		else submit(index, callback);
	}

	private class QueryPage implements Callable<List<Skin>>
	{
		private int index;

		QueryPage(int index)
		{
			this.index = index;
		}

		@Override
		public List<Skin> call() throws Exception
		{
			String data = "type=" + dataType + "&&name=" + keyWord;
			if (index != 1)
				data = data.concat("&pageNumber=" + index);
			String url = "http://www.skinme.cc/skinme/skin/charCore";
			String content = requester.request("GET", url + "?" + data, Collections.singletonMap("Content-GameSetting",
					"application/x-www-form-urlencoded; charset=UTF-8"));
			Document doc = Jsoup.parse(content);
			Elements elementsByClass = doc.getElementsByClass("search-skin-content");

			if (capacity == -1)
			{
				try
				{
					String searchSplitPage = doc.getElementsByClass("searchSplitPage").get(0).child(0).text();
					String[] split1 = searchSplitPage.split(";");
					for (String s : split1)
						if (s.startsWith("var totalPages = "))
							capacity = Integer.parseInt(s.substring("var totalPages = ".length()));
				}
				catch (Exception ignored) {}
			}
			List<Skin> lst = new ArrayList<>(elementsByClass.size());
			for (Element ele : elementsByClass)
			{
				String preview = ele.child(0).child(0).attr("src");
				preview = preview.substring(0, preview.indexOf("?"));
				Document innerDoc = Jsoup.parse(requester.request("GET", ele.child(0).attr("href")));
				if (innerDoc == null) continue;
				String pngLoc = innerDoc.getElementsByClass("char_3d_preview_right").get(0).getElementsByAttribute("href").get(0).attr("href");
				String author = "", size = "", name = "";
				int width = 64, height = 32;

				Elements tag = innerDoc.getElementsByClass("skindeail_table").get(0).getElementsByTag("td");
				for (Element element : tag)
				{
					String val = element.val();
					if (val.startsWith("作者：")) author = val.substring("作者：".length());
					else if (val.startsWith("名称：")) name = val.substring("名称：".length());
//					else if (val.startsWith("上传时间：")) upload = val.substring("上传时间：".length());
					else if (val.startsWith("图片尺寸：")) size = val.substring("图片尺寸：".length());
				}
				try
				{
					String[] split = size.split(":");
					width = Integer.parseInt(split[0]);
					height = Integer.parseInt(split[1]);
				}
				catch (Exception ignore) {}
				cachedIndex = index;
				lst.add(new Skin(name, author, name, new Dimension(width, height), TextureType.SKIN,
						Textures.createTexture(preview, Collections.emptyMap()),
						Textures.createTexture(pngLoc, Collections.emptyMap())));
			}
			return cache = lst;
		}
	}
}
