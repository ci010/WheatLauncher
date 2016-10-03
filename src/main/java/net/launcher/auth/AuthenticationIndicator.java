package net.launcher.auth;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import net.launcher.LaunchProfile;
import net.launcher.utils.Logger;
import net.launcher.utils.State;
import net.launcher.utils.StrictProperty;
import org.to2mbn.jmccc.auth.AuthInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ci010
 */
public abstract class AuthenticationIndicator
{
	private ObjectProperty<AuthInfo> info = new SimpleObjectProperty<>();
	private ObjectProperty<State> state = new SimpleObjectProperty<>(State.of(State.Values.FAIL));
	private Map<LaunchProfile, InvalidationListener> listenerMap = new HashMap<>();

	private InvalidationListener createListner(final LaunchProfile profile)
	{
		return (observable ->
		{
			Logger.trace("account/email invalid");
			if (profile.accountProperty().state().getValue() == null || profile.javaProperty().state().getValue() == null)
				return;
			Logger.trace("setting not null");
			if (profile.javaProperty().state().getValue().getState().isPass())
				if (passwordValid() == null)
					auth(profile.accountProperty().getValue(), profile.passwordProperty().getValue(), state, info);
				else if (profile.passwordProperty().state().getValue().getState().isPass())
					auth(profile.accountProperty().getValue(), profile.passwordProperty().getValue(), state, info);
		});
	}

	public void watch(LaunchProfile profile)
	{
		AuthenticationIndicator value = profile.authProperty().getValue();
		if (this != value)
		{
			InvalidationListener listener = createListner(profile);
			listenerMap.put(profile, listener);
			this.state.addListener((observable ->
					Logger.trace("ac:" + profile.accountProperty().state().getValue().getState() + ", pw:" +
							profile.accountProperty().state().getValue().getState()
							+ ", auth:" + state.getValue().getState())
			));
			profile.accountProperty().validator().bind(Bindings.createObjectBinding(this::accountValid));
			profile.passwordProperty().validator().bind(Bindings.createObjectBinding(this::passwordValid));
			profile.accountProperty().addListener(listener);
			profile.passwordProperty().addListener(listener);
			profile.authProperty().setValue(this);
		}
	}

	public void unWatch(LaunchProfile profile)
	{
		InvalidationListener listener = listenerMap.get(profile);
		if (listener == null) return;
		profile.accountProperty().removeListener(listener);
		profile.passwordProperty().removeListener(listener);
	}

	public ObservableValue<AuthInfo> authProperty()
	{
		return info;
	}

	public ObservableValue<State> stateProperty()
	{
		return state;
	}

	public abstract String id();

	protected abstract StrictProperty.Validator<String> accountValid();

	protected abstract StrictProperty.Validator<String> passwordValid();

	protected abstract void auth(String validAccount, String validPassword, WritableValue<State> writableValue,
								 WritableValue<AuthInfo> out);

}
