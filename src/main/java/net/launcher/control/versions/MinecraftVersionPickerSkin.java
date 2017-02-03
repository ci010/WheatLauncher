package net.launcher.control.versions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import de.jensd.fx.fontawesome.Icon;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.to2mbn.jmccc.mcdownloader.RemoteVersion;

/**
 * @author ci010
 */
public class MinecraftVersionPickerSkin extends ComboBoxPopupControl<RemoteVersion>
{
	protected MinecraftVersionPicker parent;

	private Node displayNode;
	private MinecraftVersionDisplayContent content;
	private JFXTextField textField = null;

	public MinecraftVersionPickerSkin(MinecraftVersionPicker parent)
	{
		super(parent, new MinecraftVersionPicker.Behavior(parent));
		this.parent = parent;
		registerChangeListener(parent.valueProperty(), "VALUE");
		JFXButton button = new JFXButton();
		button.setGraphic(new Icon("ANGLE_DOWN"));
		button.setMaxSize(10, 10);
		button.setFocusTraversable(false);
//		arrowButton.getChildren().add(button);
//		arrowButton.getChildren().remove(arrow);
//		arrow.setOnMouseReleased(event ->
//		{
//			if (!parent.isShowing())
//				parent.show();
//		});
	}

	@Override
	protected Node getPopupContent()
	{
		if (content == null) content = defaultContent();
		return content;
	}

	protected MinecraftVersionDisplayContent defaultContent()
	{
		return new MinecraftVersionDisplayContent(this.parent);
	}

	@Override
	protected TextField getEditor()
	{
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
	/*
	 *  added to fix android issue as the stack trace on android is
	 *  not the same as desktop
	 */
		if (caller.getClassName().equals(this.getClass().getName()))
			caller = Thread.currentThread().getStackTrace()[3];
		boolean parentListenerCall = caller.getMethodName().contains("lambda") && caller.getClassName().equals("com.sun.javafx.scene.control.skin.ComboBoxPopupControl");
		if (parentListenerCall) return null;
		if (textField == null)
		{
			this.textField = new JFXTextField();
			this.textField.setEditable(false);
			this.textField.setText("Unknown");
			this.textField.setMaxWidth(100);
		}
		return textField;
	}

	@Override
	public void show()
	{
		super.show();
		TableView.TableViewSelectionModel<?> model = this.content.versionTable.getSelectionModel();
		if (!this.content.versionTable.getItems().isEmpty() && model.isEmpty())
			model.select(0);
	}

	@Override
	protected void handleControlPropertyChanged(String p)
	{
		super.handleControlPropertyChanged(p);
		if ("SHOWING".equals(p))
		{
			if (getSkinnable().isShowing()) show();
		}
		else if ("VALUE".equals(p))
		{
			updateDisplayNode();
			if (content != null && this.parent.isShowing())
				parent.hide();
		}
	}

	@Override
	public Node getDisplayNode()
	{
		if (displayNode == null)
		{
			displayNode = getEditableInputNode();
			displayNode.getStyleClass().add("date-picker-display-node");
			updateDisplayNode();
		}
		return displayNode;
	}

	@Override
	protected StringConverter<RemoteVersion> getConverter()
	{
		return new StringConverter<RemoteVersion>()
		{
			@Override
			public String toString(RemoteVersion object)
			{
				if (object != null)
					return object.getVersion();
				return "Unknown";
			}

			@Override
			public RemoteVersion fromString(String string)
			{
				return parent.dataListProperty().get().getVersions().get(string);
			}
		};
	}
}
