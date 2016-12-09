package net.wheatlauncher.utils;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.launcher.utils.Logger;

/**
 * @author ci010
 */
public abstract class ValidatorInState extends ValidatorBase implements ChangeListener<State>
{
	@Override
	protected void eval()
	{}

	@Override
	public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue)
	{
		Logger.trace("state change");
		if (newValue.getState() == State.Values.FAIL)
		{
			String s = getContext() + "." + newValue.getCause();
			Logger.trace("fail with message " + s);
			this.message.set(LanguageMap.INSTANCE.translate(getContext() + "." + newValue.getCause()));
			this.hasErrors.set(true);
			this.validate();
		}
		else if (newValue.getState() == State.Values.PASS)
		{
			this.hasErrors.set(false);
			this.validate();
		}
	}

	protected abstract String getContext();
}
