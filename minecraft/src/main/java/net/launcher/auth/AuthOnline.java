package net.launcher.auth;

import net.launcher.utils.Patterns;
import net.launcher.utils.StringUtils;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.YggdrasilAuthenticator;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;
import org.to2mbn.jmccc.auth.yggdrasil.core.yggdrasil.YggdrasilProfileServiceBuilder;

/**
 * @author ci010
 */
@Authorize.ID("online")
public class AuthOnline implements Authorize
{
	@Override
	public void validateUserName(String name)
	{
		if (StringUtils.isEmpty(name))
			throw new NullPointerException();
		else if (!Patterns.EMAIL.matcher(name).matches())
			throw new IllegalArgumentException("auth.username.email");
	}

	@Override
	public void validatePassword(String password)
	{
		if (StringUtils.isEmpty(password))
			throw new NullPointerException();
		else if (password.length() < 6)
			throw new IllegalArgumentException("auth.password.length");
	}


	@Override
	public AuthInfo auth(String account, String password) throws AuthenticationException
	{
		YggdrasilAuthenticator auth;
		auth = YggdrasilAuthenticator.password(account, password);
//		ViewSession viewSession = auth.viewSession();
//		profile.setAccessToken(viewSession.getAccessToken());
//		if (profile.getClientToken() != null)
//			profile.setClientToken(viewSession.getClientToken());
		return auth.auth();
	}

	@Override
	public ProfileService createProfileService()
	{
		return YggdrasilProfileServiceBuilder.buildDefault();
	}
}
