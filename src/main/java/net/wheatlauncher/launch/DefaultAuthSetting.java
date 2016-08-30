package net.wheatlauncher.launch;

import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import net.wheatlauncher.Core;
import net.wheatlauncher.utils.Logger;
import net.wheatlauncher.utils.Patterns;
import net.wheatlauncher.utils.StrictProperty;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.OfflineAuthenticator;
import org.to2mbn.jmccc.auth.yggdrasil.YggdrasilAuthenticator;
import org.to2mbn.jmccc.auth.yggdrasil.core.RemoteAuthenticationException;

import java.net.UnknownHostException;
import java.util.TimerTask;

/**
 * @author ci010
 */
public enum DefaultAuthSetting implements ConditionAuth.Setting
{
	OFFLINE
			{
				@Override
				public boolean isPasswordEnable()
				{
					return false;
				}

				@Override
				public void auth(String validAccount, String validPassword, WritableValue<StrictProperty.State>
						handler, WritableValue<AuthInfo> out)
				{
					try
					{
						Logger.trace("try offline auth " + validAccount);
						AuthInfo auth = new OfflineAuthenticator(validAccount).auth();
						if (auth != null)
						{
							Logger.trace("offline auth passed!");
							out.setValue(auth);
							handler.setValue(StrictProperty.State.of(StrictProperty.EnumState.PASS));
						}
						else
						{
							Logger.trace("auth fail");
							handler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "fail"));
						}
					}
					catch (AuthenticationException e)
					{
						Logger.trace("auth fail");
						handler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "fail"));
					}
				}

				@Override
				public StrictProperty.Validator<String> accountValid()
				{
					return (stateHandler, v) -> {
						if (v == null || v.isEmpty())
							stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "null"));
						else stateHandler.setValue(StrictProperty.PASS);
					};
				}

				@Override
				public StrictProperty.Validator<String> passwordValid()
				{
					return ((stateHandler, v) -> {
						stateHandler.setValue(StrictProperty.PASS);
					});
				}
			},
	ONLINE
			{
				private TimerTask task;

				@Override
				public boolean isPasswordEnable()
				{
					return true;
				}

				@Override
				public void auth(String validAccount, String validPassword, WritableValue<StrictProperty.State>
						handler, WritableValue<AuthInfo> out)
				{
					Logger.trace("try online auth");
					handler.setValue(StrictProperty.State.of(StrictProperty.EnumState.PENDING, "verifying"));
					if (task != null)
					{
						task.cancel();
						Core.INSTANCE.getTimer().schedule(task = new Delay(validAccount, validPassword, handler, out), 500);
					}
					else
						Core.INSTANCE.getTimer().schedule(task = new Delay(validAccount, validPassword, handler, out), 500);
				}

				class Delay extends TimerTask
				{
					private String account, password;
					WritableValue<StrictProperty.State> handler;
					WritableValue<AuthInfo> out;

					public Delay(String account, String password, WritableValue<StrictProperty.State>
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
							AuthInfo auth = YggdrasilAuthenticator.password(account, password).auth();
							Platform.runLater(() -> {
								handler.setValue(StrictProperty.State.of(StrictProperty.EnumState.PASS));
								out.setValue(auth);
							});
							Logger.trace("online auth isPass!");
						}
						catch (AuthenticationException e)
						{
							if (e instanceof RemoteAuthenticationException && e.getMessage().contains("Invalid username or password"))
							{
								Platform.runLater(() -> handler.setValue(StrictProperty.State.of(StrictProperty.EnumState
										.FAIL, "invalid", "credentials")));
							}
							else if (e.getCause() instanceof UnknownHostException)
							{
								Platform.runLater(() -> handler.setValue(StrictProperty.State.of(StrictProperty.EnumState
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

				@Override
				public StrictProperty.Validator<String> accountValid()
				{
					return (stateHandler, v) -> {
						if (v == null || v.isEmpty())
							stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "null"));
						else if (Patterns.EMAIL.matcher(v).matches())
							stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.PASS));
						else stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "invalid"));
					};
				}

				@Override
				public StrictProperty.Validator<String> passwordValid()
				{
					return ((stateHandler, v) -> {
						if (v == null)
							stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "null"));
						else if (v.length() < 6)
							stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "invalid"));
						else
							stateHandler.setValue(StrictProperty.State.of(StrictProperty.EnumState.PASS));
					});
				}
			};

	@Override
	public String getId()
	{
		return this.name().toLowerCase();
	}
}
