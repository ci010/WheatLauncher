package net.launcher.services.skin.query;

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
public class MinecraftSkinNetService extends AbstractSkinQueryService implements SkinQueryService
{
	private String root = "http://www.minecraftskins.net/";
	private HttpRequester requester;

	protected MinecraftSkinNetService(HttpRequester requester, ExecutorService service)
	{
		super("minecraftskinnet", service);
		this.requester = requester;
	}

	@Override
	protected List<Skin> parseList(Document document) throws IOException
	{
		Elements titles = document.getElementsByClass("card-title");
		List<Skin> skins = new ArrayList<>(titles.size());
		for (Element title : titles)
		{
			String name = title.text();
			String id = name.toLowerCase().replace(" ", "");

			String get = requester.request("GET", root + id);
			Document skinDetail = Jsoup.parse(get);
			Element card_content = skinDetail.getElementsByClass("card-content").get(0);
			String author = card_content.child(0).text().substring("Designed by ".length());
			String describe = card_content.child(1).text();

			Texture texture = Textures.createTexture(root + id + "/download", Collections.emptyMap());
			byte[] bytes = IOUtils.toByteArray(texture.openStream());
			ByteBuffer wrap = ByteBuffer.wrap(bytes);
			for (int i = 0; i < 4; i++)
				wrap.getInt();
			Dimension dimension = new Dimension(wrap.getInt(), wrap.getInt());
			skins.add(new Skin(name, author, describe, dimension, TextureType.SKIN,
					Textures.createTexture(root + "static/preview/" + id + "@2x.png", Collections.emptyMap()),
					Textures.createTexture(bytes, Collections.emptyMap())));
		}
		return skins;
	}

	@Override
	protected String queryPage(String keyword, int index) throws IOException
	{
		String url = "http://www.minecraftskins.net/search/" + keyword + "/" + index;
		return requester.request("GET", url);
	}

	@Override
	protected int parseSize(Document document)
	{
		Elements count = document.getElementsByClass("count");
		int size = 1;
		if (!count.isEmpty())
			size = Integer.parseInt(count.get(0).child(0).text().substring("1 of ".length()));
		return size;
	}

}
