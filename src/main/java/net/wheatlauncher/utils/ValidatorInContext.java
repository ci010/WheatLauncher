package net.wheatlauncher.utils;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author ci010
 */
public abstract class ValidatorInContext extends ValidatorBase implements ChangeListener<StrictProperty.State>
{
	@Override
	protected void eval()
	{}

	@Override
	public void changed(ObservableValue<? extends StrictProperty.State> observable, StrictProperty.State oldValue, StrictProperty.State newValue)
	{
		if (newValue.getState() == StrictProperty.EnumState.FAIL)
		{
			this.message.set(LanguageMap.INSTANCE.translate(getContext() + "." + newValue.getCause()));
			this.hasErrors.set(true);
		}
		else if (newValue.getState() == StrictProperty.EnumState.PASS)
			this.hasErrors.set(false);
	}

	protected abstract String getContext();
}
