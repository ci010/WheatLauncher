package net.wheatlauncher.control.utils;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import net.wheatlauncher.utils.LanguageMap;

import java.util.Objects;

/**
 * @author ci010
 */
public class ValidatorDelegate extends ValidatorBase
{
	public Delegate getDelegate()
	{
		return delegate.get();
	}

	public ObjectProperty<Delegate> delegateProperty()
	{
		return delegate;
	}

	public void setDelegate(Delegate delegate)
	{
		Objects.requireNonNull(delegate);
		this.delegate.set(delegate);
	}

	private ObjectProperty<Delegate> delegate = new SimpleObjectProperty<>((s) ->
	{
	});

	@Override
	protected void eval()
	{
		Node node = srcControl.get();
		if (node instanceof TextInputControl)
		{
			TextInputControl control = (TextInputControl) node;
			String text = control.getText();
			try
			{
				delegate.get().accept(text);
				hasErrors.set(false);
			}
			catch (Exception e)
			{
				message.set(LanguageMap.INSTANCE.translate(e.getMessage()));
				hasErrors.set(true);
			}
		}
	}

	public interface Delegate
	{
		void accept(String s) throws Exception;
	}
}
