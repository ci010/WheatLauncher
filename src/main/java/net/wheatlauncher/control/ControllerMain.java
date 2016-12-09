package net.wheatlauncher.control;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.FXMLController;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
@FXMLController(value = "/fxml/Main.fxml", title = "Simple Launcher")
public class ControllerMain
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private Pane root;

	@FXML
	private JFXButton close;

	@PostConstruct
	public void init() throws FlowException, FxmlLoadException
	{
		WindowsManager.Page page = flowContext.getRegisteredObject(WindowsManager.Page.class);
		WindowsManager.Page sub = page.createSubPage(ControllerLogin.class, new AnimatedFlowContainer());
		sub.register(ControllerPreview.class);

		close.setOnMouseClicked(event ->
		{
			if (event.getButton() == MouseButton.PRIMARY)
				Platform.exit();
		});
	}
}
