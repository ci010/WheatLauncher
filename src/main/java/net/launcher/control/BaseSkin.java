package net.launcher.control;

import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TextField;

/**
 * @author ci010
 */
public abstract class BaseSkin<T> extends ComboBoxPopupControl<T>
{
	protected JFXTextField textField;
	protected Node displayNode;

	public BaseSkin(ComboBoxBase<T> comboBoxBase, ComboBoxBaseBehavior<T> behavior)
	{
		super(comboBoxBase, behavior);
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
			if (getSkinnable() != null && getSkinnable().isShowing())
				getSkinnable().hide();
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
}
