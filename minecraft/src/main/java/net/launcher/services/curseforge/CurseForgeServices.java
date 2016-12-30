package net.launcher.services.curseforge;

import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;

import java.io.IOException;

/**
 * @author ci010
 */
public class CurseForgeServices
{
	public static CurseForgeService newService(CurseForgeProjectType projectType, HttpRequester requester) throws IOException
	{
		CurseForgeRequesterImpl curseForgeRequester = new CurseForgeRequesterImpl(requester, projectType);
		curseForgeRequester.init();//init
		return curseForgeRequester;
	}

	public static CurseForgeService newService(CurseForgeProjectType projectType) throws IOException
	{
		return newService(projectType, new HttpRequester());
	}

	private CurseForgeServices() {}
}
