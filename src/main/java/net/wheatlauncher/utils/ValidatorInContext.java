package net.wheatlauncher.utils;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.launcher.utils.Logger;
import net.launcher.utils.State;

/**
 * @author ci010
 */
public abstract class ValidatorInContext extends ValidatorBase implements ChangeListener<State>
{
	@Override
	protected void eval()
	{}

	@Override
	public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue)
	{
		if (newValue.getState() == State.Values.FAIL)
		{
			String s = getContext() + "." + newValue.getCause();
			Logger.trace("fail with message " + s);
			this.message.set(LanguageMap.INSTANCE.translate(getContext() + "." + newValue.getCause()));
			this.hasErrors.set(true);
		}
		else if (newValue.getState() == State.Values.PASS)
			this.hasErrors.set(false);
	}

	protected abstract String getContext();
}
