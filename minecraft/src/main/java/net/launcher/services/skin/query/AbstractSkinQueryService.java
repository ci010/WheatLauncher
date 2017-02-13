package net.launcher.services.skin.query;

import net.launcher.utils.Tasks;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callbacks;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public abstract class AbstractSkinQueryService implements SkinQueryService
{
	protected ExecutorService service;
	private String id;

	@Override
	public void shutdown()
	{
		service.shutdown();
	}

	@Override
	public boolean isShutdown()
	{
		return service.isShutdown();
	}

	public AbstractSkinQueryService(String id, ExecutorService service)
	{
		Objects.requireNonNull(service);
		Objects.requireNonNull(id);
		this.service = service;
		this.id = id;
	}

	protected abstract List<Skin> parseList(Document document) throws IOException;

	protected abstract String queryPage(String keyword, int index) throws IOException;

	protected Session createSession(String keyword, int pageSize, int current, List<Skin> cache)
	{
		return new ImplSession(pageSize, keyword, current, cache);
	}

	protected abstract int parseSize(Document document);

	@Override
	public Future<Session> session(final String keyword, Callback<Session> sessionCallback, TextureType textureType)
	{
		return service.submit(Tasks.wrap(() ->
		{
			Document parse = Jsoup.parse(queryPage(keyword, 1));
			return createSession(keyword, parseSize(parse), 1, Collections.unmodifiableList(parseList(parse)));
		}, sessionCallback));
	}

	private class ImplSession implements Session
	{
		private int size;
		private String keyword;

		private int current;
		private List<Skin> cache;

		public ImplSession(int size, String keyword, int current, List<Skin> cache)
		{
			this.size = size;
			this.keyword = keyword;
			this.current = current;
			this.cache = cache;
		}

		@Override
		public String sessionSource()
		{
			return id;
		}

		@Override
		public TextureType skinType()
		{
			return TextureType.SKIN;
		}

		@Override
		public int totalPages()
		{
			return size;
		}

		@Override
		public void query(int index, Callback<List<Skin>> callback)
		{
			if (index == current)//TODO this should add lock?
			{
				callback.done(cache);
				return;
			}
			service.submit(Tasks.wrap(() -> parseList(Jsoup.parse(AbstractSkinQueryService.this.queryPage
					(keyword, index))), Callbacks.group(callback, new CallbackAdapter<List<Skin>>()
			{
				@Override
				public void done(List<Skin> result)
				{
					cache = result; //TODO should work with lock?
					current = index;
				}
			})));
		}
	}
}
