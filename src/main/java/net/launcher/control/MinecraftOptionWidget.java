package net.launcher.control;

import com.jfoenix.controls.JFXButton;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import static net.launcher.control.MinecraftOptionButton.STYLE_CLASS;


/**
 * @author ci010
 */
@DefaultProperty("key")
public abstract class MinecraftOptionWidget extends StackPane
{
	private ObjectProperty<JFXButton> button = new SimpleObjectProperty<>();
	private StringProperty key = new SimpleStringProperty();
	private ObjectProperty<Callback<String, String>> valuePredicate = new SimpleObjectProperty<>();
	private Node content;
	protected StringProperty value = new SimpleStringProperty();

	public MinecraftOptionWidget() {init();}

	public String getValue()
	{
		return value.get();
	}

	public ReadOnlyStringProperty valueProperty()
	{
		return value;
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

	public Callback<String, String> getValuePredicate()
	{
		return valuePredicate.get();
	}

	public ObjectProperty<Callback<String, String>> valuePredicateProperty()
	{
		return valuePredicate;
	}

	protected abstract Node createContent();

	protected boolean shouldHide()
	{
		return !isHover();
	}

	public void setValuePredicate(Callback<String, String> valuePredicate)
	{
		this.valuePredicate.set(valuePredicate);
	}

	public void hide()
	{
		getButton().setDisable(false);
		content.setVisible(false);
	}

	public void show()
	{
		getButton().setDisable(true);
		content.setVisible(true);
	}

	protected void init()
	{
		this.content = createContent();
		JFXButton button = new JFXButton();
		button.getStyleClass().add(STYLE_CLASS);
		this.setPickOnBounds(false);
		this.button.set(button);

		this.getChildren().add(button);
		this.content.setVisible(false);
		this.getChildren().add(content);
		this.getButton().textProperty().bind(Bindings.createStringBinding(() ->
				getKey() + ":" + value.get(), key, value));
		this.hoverProperty().addListener(o ->
		{
			if (shouldHide())
				hide();
			else show();
		});
	}
}
