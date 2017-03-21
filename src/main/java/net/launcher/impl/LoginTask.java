package net.launcher.impl;

import javafx.concurrent.Task;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.Authenticator;

/**
 * @author ci010
 */
public class LoginTask extends Task<AuthInfo>
{
	private Authenticator authenticator;

	public LoginTask(Authenticator authenticator)
	{
		this.authenticator = authenticator;
		updateTitle("Login");
	}

	@Override
	protected AuthInfo call() throws Exception
	{
		return authenticator.auth();
	}
}
