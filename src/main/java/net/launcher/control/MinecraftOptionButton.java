package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.launcher.game.GameSettings;

import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author ci010
 */
public class MinecraftOptionButton<T> extends JFXButton
{
	protected static final String STYLE_CLASS = "options-button";
	private ObjectBinding<GameSettings.Option> propertyBinding;

	public MinecraftOptionButton()
	{
		initialize();
	}

	private String localizedValue()
	{
		if (propertyBinding == null) return "Unknown";
		if (propertyBinding.get() == null) return "Unknown";
		String s = propertyBinding.get().getId() + "." + propertyBinding.get().getValue();
		Object userData = getProperties().get("lang");
		if (userData != null && userData instanceof ResourceBundle)
			return ((ResourceBundle) userData).getString(s);
		return s;
	}

	private void next()
	{
		if (propertyBinding != null && propertyBinding.get() != null)
			propertyBinding.get().nextValue();
	}

	private void initialize()
	{
		this.setOnAction(event -> next());
		this.getStyleClass().add(STYLE_CLASS);
	}

	private StringProperty key = new SimpleStringProperty();

	public T getProperty()
	{
		return (T) propertyBinding.get().getValue();
	}

	public void setPropertyBinding(ObjectBinding<GameSettings.Option> binding)
	{
		this.propertyBinding = binding;
		this.propertyBinding.addListener(observable ->
				this.textProperty().bind(Bindings.createStringBinding(() ->
								this.key.get() + ":" + localizedValue(),
						key, (Observable) propertyBinding.get())));
		this.propertyBinding.invalidate();
	}

	public String getKey()
	{
		return key.get();
	}

	public StringProperty keyProperty()
	{
		return key;
	}

	public void setKey(String key)
	{
		Objects.requireNonNull(key);
		this.key.set(key);
	}
}
