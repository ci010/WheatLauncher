package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXDialog;
import javafx.scene.layout.StackPane;

import java.util.Stack;

/**
 * @author ci010
 */
public class DialogStack
{
	private Stack<JFXDialog> stack = new Stack<>();

	public void pop()
	{
		stack.pop().close();
	}

	public void push(JFXDialog jfxDialog)
	{
		stack.push(jfxDialog);
		jfxDialog.show();
	}

	public void push(JFXDialog jfxDialog, StackPane dialogContainer)
	{
		stack.push(jfxDialog);
		jfxDialog.show(dialogContainer);
	}
}
