package net.launcher.services.skin.query;

import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author ci010
 */
public class SkinMeService implements SkinQueryService
{
	private HttpRequester requester;
	private ExecutorService service;

	public SkinMeService(HttpRequester requester, ExecutorService service)
	{
		this.requester = requester;
		this.service = service;
	}

	@Override
	public Future<Session> session(String keyword, Callback<Session> sessionCallback, TextureType textureType)
	{
		FutureTask<Session> future = SkinMeSession.create(keyword, (byte) 1, service, requester, sessionCallback);
		service.submit(future);
		return future;
	}

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

}
