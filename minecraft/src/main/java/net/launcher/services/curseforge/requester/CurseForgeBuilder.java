package net.launcher.services.curseforge.requester;

import net.launcher.services.curseforge.CurseForgeProjectType;
import org.to2mbn.jmccc.auth.yggdrasil.core.io.HttpRequester;
import org.to2mbn.jmccc.util.Builder;

import java.util.Objects;

/**
 * @author ci010
 */
public class CurseForgeBuilder implements Builder<CurseForgeService>
{
	private CurseForgeProjectType projectType;
	private HttpRequester requester;

	public static CurseForgeBuilder create(CurseForgeProjectType projectType)
	{
		return new CurseForgeBuilder(projectType);
	}

	public CurseForgeBuilder setRequester(HttpRequester requester)
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


	private CurseForgeBuilder(CurseForgeProjectType projectType) {this.projectType = projectType;}
}
