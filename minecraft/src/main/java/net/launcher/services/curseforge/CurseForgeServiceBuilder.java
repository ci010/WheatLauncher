package net.launcher.services.curseforge;

import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.util.Builder;

import java.util.Objects;

/**
 * @author ci010
 */
public class CurseForgeServiceBuilder implements Builder<CurseForgeService>
{
	private CurseForgeProjectType projectType;
	private HttpRequester requester;

	public static CurseForgeServiceBuilder create(CurseForgeProjectType projectType)
	{
		return new CurseForgeServiceBuilder(projectType);
	}

	public CurseForgeServiceBuilder setRequester(HttpRequester requester)
	{
		Objects.requireNonNull(requester);
		this.requester = requester;
		return this;
	}

	@Override
	public CurseForgeService build()
	{
		return new CurseForgeRequesterImpl(requester == null ? new HttpRequester() : requester, projectType);
	}


	private CurseForgeServiceBuilder(CurseForgeProjectType projectType) {this.projectType = projectType;}
}
