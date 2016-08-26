package net.wheatlauncher.launch;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.beans.value.WritableObjectValue;
import javafx.beans.value.WritableValue;
import net.wheatlauncher.utils.Condition;
import net.wheatlauncher.utils.Logger;
import net.wheatlauncher.utils.SimpleStrictProperty;
import net.wheatlauncher.utils.StrictProperty;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.AuthenticationException;
import org.to2mbn.jmccc.auth.Authenticator;
import org.to2mbn.jmccc.auth.yggdrasil.CharacterSelector;
import org.to2mbn.jmccc.auth.yggdrasil.YggdrasilAuthenticator.PasswordProvider;

/**
 * @author ci010
 */
public class ConditionAuth extends Condition implements Authenticator, PasswordProvider
{
	private StrictProperty<String>
			account = new SimpleStrictProperty<>(this, "account"),
			password = new SimpleStrictProperty<>(this, "password");
	private ObjectProperty<AuthInfo> infoObjectProperty = new SimpleObjectProperty<>();
	private ObjectProperty<StrictProperty.State> authState = new SimpleObjectProperty<>(StrictProperty.State.of(StrictProperty.EnumState.FAIL));

	private SimpleStrictProperty<AuthInfo> authInfo = new SimpleStrictProperty<>();
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
		this.account.setValue(null);
		this.password.setValue(null);
		this.infoObjectProperty.set(null);
		this.authState.set(StrictProperty.State.of(StrictProperty.EnumState.FAIL, "null"));
		this.account.validator().setValue(setting.accountValid());
		this.password.validator().setValue(setting.passwordValid());
		this.setting = setting;
		this.settingName.setValue(setting.getId());
	}

	public ConditionAuth()
	{
		super("auth");
		this.add(account, password).add(authState);
		InvalidationListener listener = (observable -> {
			System.out.println("account/email invalid");
			if (account.state().getValue() == null || password.state().getValue() == null) return;
			if (setting == null) return;
			if (account.state().getValue().getState() == StrictProperty.EnumState.PASS &&
					password.state().getValue().getState() == StrictProperty.EnumState.PASS)
			{
				Logger.trace("account " + account().getValue() + " password " + password().getValue() + " pass!");
				setting.auth(account.getValue(), password.getValue(), authState, infoObjectProperty);
			}
		});
		account.addListener(listener);
		password.addListener(listener);
	}

	@Override
	public AuthInfo auth() throws AuthenticationException
	{
		return infoObjectProperty.getValue();
	}

	@Override
	public String getUsername() throws AuthenticationException
	{
		return account.getName();
	}

	@Override
	public String getPassword() throws AuthenticationException
	{
		return password.getName();
	}

	@Override
	public CharacterSelector getCharacterSelector()
	{
		return null;
	}

	public interface Setting
	{
		String getId();

		boolean isPasswordEnable();

		StrictProperty.Validator<String> accountValid();

		StrictProperty.Validator<String> passwordValid();

		/**
		 * This method will be called after account and password are all valid.
		 */
		void auth(String validAccount, String validPassword, WritableValue<StrictProperty.State>
				writableValue, WritableValue<AuthInfo> out);
	}
}
