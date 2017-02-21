package net.launcher.control;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

/**
 * @author ci010
 */
@DefaultProperty("popupContent")
public class ComboBoxDelegate<T> extends ComboBoxBase<T>
{
	private ObjectProperty<Node> popupContent = new SimpleObjectProperty<>();
	private ObjectProperty<StringConverter<T>> stringConverter = new SimpleObjectProperty<>();

	public Node getPopupContent()
	{
		return popupContent.get();
	}

	public ObjectProperty<Node> popupContentProperty()
	{
		return popupContent;
	}

	public void setPopupContent(Node popupContent)
	{
		this.popupContent.set(popupContent);
	}

	public StringConverter getStringConverter()
	{
		return stringConverter.get();
	}

	public ObjectProperty<StringConverter<T>> stringConverterProperty()
	{
		return stringConverter;
	}

	public void setStringConverter(StringConverter stringConverter)
	{
		this.stringConverter.set(stringConverter);
	}

	@Override
	public ObservableList<Node> getChildren()
	{
		return super.getChildren();
	}

	@Override
	protected Skin<?> createDefaultSkin()
	{
		return new ComboBoxSkinSimple<T>(this, new Behavior(this))
		{
			@Override
			protected Node getPopupContent()
			{
				return popupContent.get() == null ? new StackPane() : popupContent.get();
			}

			@Override
			protected StringConverter<T> getConverter()
			{
				return stringConverter.get();
			}
		};
	}

	private class Behavior extends ComboBoxBaseBehavior<T>
	{
		public Behavior(ComboBoxBase<T> comboBox)
		{
			super(comboBox, ComboBoxBaseBehavior.COMBO_BOX_BASE_BINDINGS);
		}
	}
}
