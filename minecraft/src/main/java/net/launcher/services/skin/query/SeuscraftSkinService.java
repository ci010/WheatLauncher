package net.launcher.services.skin.query;

import net.launcher.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Texture;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.Textures;
import org.to2mbn.jmccc.util.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author ci010
 */
public class SeuscraftSkinService extends AbstractSkinQueryService
{
	private String root = "https://seuscraft.com";
	private HttpRequester requester;

	public SeuscraftSkinService(HttpRequester requester, ExecutorService service)
	{
		super("seuscraft", service);
		this.requester = requester;
	}

	@Override
	protected List<Skin> parseList(Document document) throws IOException
	{
		Elements skins = document.getElementsByClass("skin");
		List<Skin> list = new ArrayList<>(skins.size());
		for (Element element : skins)
		{
			String prevTxt = element.child(2).attr("src");
			prevTxt = root + prevTxt.substring(0, prevTxt.indexOf("?"));
			Texture preview = Textures.createTexture(prevTxt, Collections.emptyMap());

			String href = root + element.child(1).attr("href");
			Texture download = Textures.createTexture(href + "/download", Collections.emptyMap());

			byte[] bytes = IOUtils.toByteArray(download.openStream());
			ByteBuffer wrap = ByteBuffer.wrap(bytes);
			for (int i = 0; i < 4; i++)
				wrap.getInt();
			Dimension dimension = new Dimension(wrap.getInt(), wrap.getInt());
			download = Textures.createTexture(bytes, Collections.emptyMap());
			Document detail = Jsoup.parse(this.requester.request("GET", href));
			String descrip = StringUtils.EMPTY, name = StringUtils.EMPTY;
			for (Element e : detail.getElementsByClass("skin-details"))
			{
				name = e.child(0).text();
				descrip = e.child(1).text();
			}
			list.add(new Skin(name, "unknown", descrip, dimension, TextureType.SKIN, preview, download));
		}
		return list;
	}

	@Override
	protected String queryPage(String keyword, int index) throws IOException
	{
		String url = root + "/find/skins/search/" + keyword + "/page/" + index;
		return requester.request("GET", url);
	}

	@Override
	protected int parseSize(Document document)
	{
		Element element = document.getElementsByClass("ellipsis").get(0);
		int size = 1;
		try
		{
			size = Integer.parseInt(element.attr("data-total"));
		}
		catch (Exception ignored) {}
		return size;
	}
}
