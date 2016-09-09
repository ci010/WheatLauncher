package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.FXMLController;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.ViewConfiguration;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
		ViewConfiguration registeredObject = flowContext.getRegisteredObject(ViewConfiguration.class);
		Flow inner = new Flow(ControllerLogin.class, registeredObject);
		FlowHandler handler = new FlowHandler(inner, flowContext, registeredObject);
		PageManager manager = new PageManager(handler);
		flowContext.register(manager);
		flowContext.register(root);
		root.getChildren().add(0, handler.start(new AnimatedFlowContainer()));

		manager.register("login", (ViewContext<? extends ReloadableController>) handler.getCurrentView().getViewContext());
		manager.register("preview", ControllerPreview.class);

		close.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				Platform.exit();
		});
	}


	@FXML
	protected void onClick(MouseEvent event)
	{
		if (event.getButton() == MouseButton.PRIMARY)
			root.requestFocus();
	}
}
