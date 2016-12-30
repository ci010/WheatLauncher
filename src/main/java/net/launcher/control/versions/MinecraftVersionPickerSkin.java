package net.launcher.control.versions;

import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
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
		this.textField = new JFXTextField();
		this.textField.setEditable(false);
		this.textField.setText("Unknown");

		registerChangeListener(parent.valueProperty(), "VALUE");
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
		if ("VALUE".equals(p))
		{
			updateDisplayNode();
			if (content != null && this.popup.isShowing())
				this.popup.hide();
		}
		else
			super.handleControlPropertyChanged(p);
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
				return object.getVersion();
			}

			@Override
			public RemoteVersion fromString(String string)
			{
				return parent.dataListProperty().get().getVersions().get(string);
			}
		};
	}
}
