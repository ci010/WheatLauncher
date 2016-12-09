package net.launcher.auth;

import org.to2mbn.jmccc.auth.yggdrasil.core.AuthenticationService;
import org.to2mbn.jmccc.auth.yggdrasil.core.yggdrasil.YggdrasilAPIProvider;
import org.to2mbn.jmccc.auth.yggdrasil.core.yggdrasil.YggdrasilAuthenticationServiceBuilder;
import org.to2mbn.jmccc.internal.org.json.JSONObject;
import org.to2mbn.jmccc.util.UUIDUtils;

import java.util.UUID;

/**
 * @author ci010
 */
public class CustomYggdrasilAPI implements YggdrasilAPIProvider
{
	private String authenticate, refresh, validate, invalidate, signout, profileRoot, sessionProfileRoot;

	public static CustomYggdrasilAPI readFromJson(JSONObject jsonObject)
	{
		return null;
	}

	public static AuthenticationService createSerivice(CustomYggdrasilAPI api)
	{
		return YggdrasilAuthenticationServiceBuilder.create().apiProvider(api).build();
	}

	public CustomYggdrasilAPI(String authenticate, String refresh, String validate, String invalidate, String signout, String profileRoot, String sessionProfileRoot)
	{
		this.authenticate = authenticate;
		this.refresh = refresh;
		this.validate = validate;
		this.invalidate = invalidate;
		this.signout = signout;
		this.profileRoot = profileRoot;
		this.sessionProfileRoot = sessionProfileRoot;
	}

	@Override
	public String authenticate()
	{
		return authenticate;
	}

	@Override
	public String refresh()
	{
		return refresh;
	}

	@Override
	public String validate()
	{
		return validate;
	}

	@Override
	public String invalidate()
	{
		return invalidate;
	}

	@Override
	public String signout()
	{
		return signout;
	}

	@Override
	public String profile(UUID profileUUID)
	{
		return sessionProfileRoot + UUIDUtils.unsign(profileUUID);
	}

	@Override
	public String profileByUsername(String username)
	{
		return profileRoot + username;
	}
}
