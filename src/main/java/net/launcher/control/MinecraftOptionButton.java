package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ci010
 */
public class MinecraftOptionButton extends JFXButton
{
	private static final String STYLE_CLASS = "options-button";

	public MinecraftOptionButton()
	{
		initialize();
	}

	private int nextIndex()
	{
		return index = (index + 1) % options.size();
	}

	private void initialize()
	{
		this.textProperty().bind(Bindings.createStringBinding(() -> this.key.get() + ":" + this.getValue(), key,
				value));
		this.setOnAction(event ->
		{
			if (options != null && !getOptions().isEmpty())
				value.set(options.get(nextIndex()));
		});
		this.getStyleClass().add(STYLE_CLASS);
	}

	private StringProperty key = new SimpleStringProperty(), value = new SimpleStringProperty();

	private List<String> options;
	private int index = -1;

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

	public String getValue() {return value.get();}

	public ReadOnlyStringProperty valueProperty() {return value;}

	public List<String> getOptions() {return options;}

	public void setOptions(List<String> options)
	{
		this.options = (options == null ? Collections.emptyList() : new ArrayList<>(options));
		this.index = 0;
		if (!this.options.isEmpty()) this.value.set(this.options.get(index));
	}
}
