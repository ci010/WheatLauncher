package net.launcher.auth;

import net.launcher.utils.StrictProperty;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.YggdrasilAuthenticator;
import org.to2mbn.jmccc.auth.yggdrasil.core.AuthenticationService;

/**
 * @author ci010
 */
public abstract class CustomYggdrasilAuth extends OnlineAuth
{
	private String id;
	private StrictProperty.Validator<String> accountValid, pswValid;
	private AuthenticationService service;

	public CustomYggdrasilAuth(String id, StrictProperty.Validator<String> accountValid, StrictProperty.Validator<String> pswValid,
							   AuthenticationService service)
	{
		this.id = id;
		this.accountValid = accountValid;
		this.pswValid = pswValid;
		this.service = service;
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public StrictProperty.Validator<String> accountValid()
	{
		return accountValid;
	}

	@Override
	public StrictProperty.Validator<String> passwordValid()
	{
		return pswValid;
	}

	@Override
	protected AuthInfo authImpl(String account, String password) throws AuthenticationException
	{
		return new YggdrasilAuthenticator(service).auth();
	}
}
