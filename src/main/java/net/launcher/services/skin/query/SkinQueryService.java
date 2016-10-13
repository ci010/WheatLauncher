package net.launcher.services.skin.query;

import org.to2mbn.jmccc.auth.yggdrasil.core.texture.TextureType;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Callback;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.Shutdownable;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public interface SkinQueryService extends Shutdownable
{
	Future<Session> session(String keyword, Callback<Session> sessionCallback);

	interface Session
	{
		String sessionSource();

		TextureType skinType();

		int totalPages();

		void query(int index, Callback<List<Skin>> callback);
	}
}
