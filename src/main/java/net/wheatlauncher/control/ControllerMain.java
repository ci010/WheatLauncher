package net.wheatlauncher.control;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.FXMLController;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import net.wheatlauncher.control.utils.ReloadableController;
import net.wheatlauncher.control.utils.WindowsManager;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
@FXMLController(value = "/fxml/Main.fxml", title = "Simple Launcher")
public class ControllerMain implements ReloadableController
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private StackPane root;

	@FXML
	private JFXButton close;

	@PostConstruct
	public void init() throws FlowException, FxmlLoadException
	{
		WindowsManager.Page page = flowContext.getRegisteredObject(WindowsManager.Page.class);
		AnimatedFlowContainer animatedFlowContainer = new AnimatedFlowContainer();
		WindowsManager.Page sub = page.createSubPage(ControllerLogin.class, animatedFlowContainer);
		sub.register(ControllerPreview.class);
		root.getChildren().add(0, animatedFlowContainer.getView());
	}

	@FXML
	public void onClose(ActionEvent event) {Platform.exit();}

	@Override
	public void reload()
	{

	}

	@Override
	public void unload()
	{

	}
}
