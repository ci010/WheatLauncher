package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.StackPane;
import net.launcher.setting.OptionInt;
import net.launcher.setting.SettingProperty;
import net.launcher.setting.SettingType;

/**
 * @author ci010
 */
@DefaultProperty("key")
public class MinecraftSlider extends StackPane
{
	private ObjectProperty<JFXSlider> slider = new SimpleObjectProperty<>(new JFXSlider());
	private ObjectProperty<JFXButton> button = new SimpleObjectProperty<>(new JFXButton());
	private StringProperty key = new SimpleStringProperty();

	private ObjectBinding<SettingProperty.Limited<Number>> propertyBinding;

	public void setPropertyBinding(ObjectBinding<SettingProperty.Limited<Number>> binding)
	{
		if (binding == null) return;
		if (propertyBinding != null)
			slider.get().valueProperty().unbindBidirectional(propertyBinding.get());
		slider.get().valueProperty().bindBidirectional(binding.get());

		this.propertyBinding = binding;
		this.propertyBinding.addListener(observable ->
				button.get().textProperty().bind(Bindings.createStringBinding(() ->
						this.key.get() + ":" + propertyBinding.get().getValue(), key, propertyBinding.get())));
		this.propertyBinding.invalidate();

		SettingType.Option<Number> option = binding.get().getOption();
		if (option instanceof OptionInt)
		{
			int step = ((OptionInt) option).getStep();
			JFXSlider jfxSlider = slider.get();
			jfxSlider.setMajorTickUnit(step);
			jfxSlider.setMin(((OptionInt) option).getMin());
			jfxSlider.setMax(((OptionInt) option).getMax());
		}
	}

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
}
