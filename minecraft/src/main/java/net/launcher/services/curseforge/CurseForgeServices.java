package net.launcher.services.curseforge;

import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;

import java.io.IOException;

/**
 * @author ci010
 */
public abstract class CurseForgeServices
{
	public static CurseForgeService newService(CurseForgeProjectType projectType, HttpRequester requester) throws IOException
	{
		CurseForgeRequester curseForgeRequester = new CurseForgeRequester(requester, projectType);
		curseForgeRequester.setRequestingProjectType(projectType);//init
		return curseForgeRequester;
	}

	public static CurseForgeService newService(CurseForgeProjectType projectType) throws IOException
	{
		return newService(projectType, new HttpRequester());
	}
}
