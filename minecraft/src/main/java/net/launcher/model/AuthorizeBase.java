package net.launcher.model;

import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.Authenticator;

import java.util.LinkedList;
import java.util.List;

/**
 * @author ci010
 */
public abstract class AuthorizeBase implements Authorize
{
	private List<String> history;

	public AuthorizeBase()
	{
		this.history = new LinkedList<>();
	}

	@Override
	public List<String> getAccountHistory() {return history;}

	protected class AuthenticatorWrapper implements Authenticator
	{
		private Authenticator delegate;

		public AuthenticatorWrapper(Authenticator delegate)
		{
			this.delegate = delegate;
		}

		@Override
		public AuthInfo auth() throws AuthenticationException
		{
			AuthInfo auth = delegate.auth();
			history.add(0, getAccount());
			return auth;
		}
	}
}
