package net.launcher.services.curseforge;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;

import java.io.IOException;

/**
 * @author ci010
 */
public class CurseForgeRequester extends AbstractCurseForgeService
{
	private HttpRequester requester;

	public CurseForgeRequester(HttpRequester requester, CurseForgeProjectType requestingType)
	{
		super(requestingType);
		this.requester = requester;
	}

	@Override
	protected Document request(String url) throws IOException
	{
		return Jsoup.parse(requester.request("GET", url, null));
	}
}
