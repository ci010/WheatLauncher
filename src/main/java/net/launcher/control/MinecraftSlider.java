package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * @author ci010
 */
@DefaultProperty("key")
public class MinecraftSlider extends StackPane
{
	private ObjectProperty<JFXSlider> slider = new SimpleObjectProperty<>(new JFXSlider());
	private ObjectProperty<JFXButton> button = new SimpleObjectProperty<>(new JFXButton());
	private StringProperty key = new SimpleStringProperty();

	public MinecraftSlider()
	{
		init();
	}

	public JFXSlider getSlider()
	{
		return slider.get();
	}

	public ObjectProperty<JFXSlider> sliderProperty()
	{
		return slider;
	}

	public void setSlider(JFXSlider slider)
	{
		this.slider.set(slider);
	}

	public JFXButton getButton()
	{
		return button.get();
	}

	public ObjectProperty<JFXButton> buttonProperty()
	{
		return button;
	}

	public void setButton(JFXButton button)
	{
		this.button.set(button);
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
		this.key.set(key);
	}

	protected void init()
	{
		slider.addListener(o -> reload());
		button.addListener(o -> reload());
		reload();
		getStyleClass().addAll("minecraft-slider");
		button.get().getStyleClass().add("options-button");
	}

	protected void reload()
	{
		this.getButton().textProperty().bind(Bindings.createStringBinding(() ->
						getKey() + ":" + (getConverter() != null ?
								getConverter().call(getSlider().getValue()) : String.valueOf((int) getSlider().getValue())),
				key, getSlider().valueProperty()));
		this.hoverProperty().addListener(observable ->
		{
			if (this.isHover())
			{
				getButton().setDisable(true);
				if (!this.getChildren().contains(getSlider()))
					this.getChildren().add(getSlider());
			}
			else
			{
				getButton().setDisable(false);
				if (this.getChildren().contains(getSlider()))
					this.getChildren().remove(getSlider());
			}
		});
		this.getChildren().setAll(getButton());
	}

	private ObjectProperty<Callback<Double, String>> converter = new SimpleObjectProperty<>();

	public Callback<Double, String> getConverter()
	{
		return converter.get();
	}

	public ObjectProperty<Callback<Double, String>> converterProperty()
	{
		return converter;
	}

	public void setConverter(Callback<Double, String> converter)
	{
		this.converter.set(converter);
	}
}
