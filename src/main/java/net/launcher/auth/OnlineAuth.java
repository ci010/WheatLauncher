package net.launcher.auth;

import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import net.launcher.utils.Logger;
import net.launcher.utils.Patterns;
import net.launcher.utils.State;
import net.launcher.utils.StrictProperty;
import net.wheatlauncher.Core;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.yggdrasil.YggdrasilAuthenticator;
import org.to2mbn.jmccc.auth.yggdrasil.core.RemoteAuthenticationException;

import java.net.UnknownHostException;
import java.util.TimerTask;

/**
 * @author ci010
 */
class OnlineAuth extends AuthenticationIndicator
{
	private TimerTask task;

	@Override
	public String id()
	{
		return "online";
	}

	@Override
	protected StrictProperty.Validator<String> accountValid()
	{
		return (stateHandler, v) ->
		{
			if (v == null || v.isEmpty())
				stateHandler.setValue(State.of(State.Values.FAIL, "null"));
			else if (Patterns.EMAIL.matcher(v).matches())
				stateHandler.setValue(State.of(State.Values.PASS));
			else stateHandler.setValue(State.of(State.Values.FAIL, "invalid"));
		};
	}

	@Override
	protected StrictProperty.Validator<String> passwordValid()
	{
		return ((stateHandler, v) ->
		{
			if (v == null)
				stateHandler.setValue(State.of(State.Values.FAIL, "null"));
			else if (v.length() < 6)
				stateHandler.setValue(State.of(State.Values.FAIL, "invalid"));
			else
				stateHandler.setValue(State.of(State.Values.PASS));
		});
	}

	@Override
	protected void auth(String validAccount, String validPassword, WritableValue<State> state, WritableValue<AuthInfo> out)
	{
		Logger.trace("try online auth");
		state.setValue(State.of(State.Values.PENDING, "verifying"));
		if (task != null)
		{
			task.cancel();
			Core.INSTANCE.getTimer().schedule(task = new Delay(validAccount, validPassword, state, out), 500);
		}
		else
			Core.INSTANCE.getTimer().schedule(task = new Delay(validAccount, validPassword, state, out), 500);
	}

	protected AuthInfo authImpl(String account, String password) throws AuthenticationException
	{
		return YggdrasilAuthenticator.password(account, password).auth();
	}

	private class Delay extends TimerTask
	{
		private String account, password;
		WritableValue<State> handler;
		WritableValue<AuthInfo> out;

		public Delay(String account, String password, WritableValue<State>
				handler, WritableValue<AuthInfo> out)
		{
			this.account = account;
			this.password = password;
			this.handler = handler;
			this.out = out;
		}

		@Override
		public void run()
		{
			try
			{
				AuthInfo auth = authImpl(account, password);
				Platform.runLater(() ->
				{
					handler.setValue(State.of(State.Values.PASS));
					out.setValue(auth);
				});
				Logger.trace("online auth isPass!");
			}
			catch (AuthenticationException e)
			{
				if (e instanceof RemoteAuthenticationException && e.getMessage().contains("Invalid username or password"))
				{
					Platform.runLater(() -> handler.setValue(State.of(State.Values
							.FAIL, "invalid", "credentials")));
				}
				else if (e.getCause() instanceof UnknownHostException)
				{
					Platform.runLater(() -> handler.setValue(State.of(State.Values
							.FAIL, "invalid", "unknownhost")));
				}
				else
				{
					Logger.trace(e);
				}
				//TODO handle different situation
			}

		}
	}

}
