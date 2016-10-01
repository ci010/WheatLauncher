package net.wheatlauncher;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import net.launcher.utils.Logger;
import net.launcher.utils.SimpleStrictProperty;
import net.launcher.utils.State;
import net.launcher.utils.StrictProperty;
import net.wheatlauncher.utils.Condition;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.Authenticator;

/**
 * @author ci010
 */
public class ConditionAuth extends Condition implements Authenticator
{
	private SimpleStrictProperty<String>
			account = new SimpleStrictProperty<>(this, "account"),
			password = new SimpleStrictProperty<>(this, "password");
	private ObjectProperty<AuthInfo> infoObjectProperty = new SimpleObjectProperty<>();
	private ObjectProperty<State> authState = new SimpleObjectProperty<>(State.of(State.Values.FAIL));

	private Setting setting;
	private BooleanProperty onlineMode = new SimpleBooleanProperty(true);
	private StringProperty settingName = new SimpleStringProperty();

	public BooleanProperty onlineMode()
	{
		return onlineMode;
	}

	public StrictProperty<String> account()
	{
		return account;
	}

	public ObservableValue<String> settingName() {return settingName;}

	public boolean isPasswordEnable()
	{
		return setting.isPasswordEnable();
	}

	public StrictProperty<String> password()
	{
		return password;
	}

	public void apply(Setting setting)
	{
		this.infoObjectProperty.set(null);
		this.authState.set(State.of(State.Values.FAIL, "null"));
		this.account.validator().setValue(setting.accountValid());
		this.password.validator().setValue(setting.passwordValid());
		this.account.setValue("");
		this.password.setValue("");

		this.setting = setting;
		this.settingName.setValue(setting.getId());
	}

	public ReadOnlyObjectProperty<AuthInfo> authInfoProperty()
	{
		return infoObjectProperty;
	}

	public String getCurrentUsername()
	{
		if (infoObjectProperty != null)
			return infoObjectProperty.get().getUsername();
		return null;
	}

	ConditionAuth()
	{
		super("auth");
		this.authState.addListener((observable ->
				Logger.trace("ac:" + account.state().getValue().getState() + ", pw:" + password.state().getValue().getState()
						+ ", auth:" + authState.getValue().getState())
		));
		this.add(account, password).add(authState);
		InvalidationListener listener = (observable ->
		{
			Logger.trace("account/email invalid");
			if (account.state().getValue() == null || password.state().getValue() == null) return;
			if (setting == null) return;
			Logger.trace("setting not null");
			if (account.state().getValue().getState().isPass())
				if (!isPasswordEnable())
					setting.auth(account.getValue(), password.getValue(), authState, infoObjectProperty);
				else if (password.state().getValue().getState().isPass())
					setting.auth(account.getValue(), password.getValue(), authState, infoObjectProperty);
		});
		account.addListener(listener);
		password.addListener(listener);
	}

	@Override
	public AuthInfo auth() throws AuthenticationException
	{
		return infoObjectProperty.getValue();
	}
//
//	@Override
//	public String getUsername() throws AuthenticationException
//	{
//		return account.getId();
//	}
//
//	@Override
//	public String getPassword() throws AuthenticationException
//	{
//		return password.getId();
//	}
//
//	@Override
//	public CharacterSelector getCharacterSelector()
//	{
//		return null;
//	}

	public interface Setting
	{
		String getId();

		boolean isPasswordEnable();

		StrictProperty.Validator<String> accountValid();

		StrictProperty.Validator<String> passwordValid();

		/**
		 * This method will be called after account and password are all valid.
		 */
		void auth(String validAccount, String validPassword, WritableValue<State>
				writableValue, WritableValue<AuthInfo> out);
	}
}
