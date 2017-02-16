package net.wheatlauncher.control;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * @author ci010
 */
public class ControllerMain
{
	//	@FXMLViewFlowContext
//	private ViewFlowContext flowContext;
	@FXML
	private JFXButton close;

//	@PostConstruct
//	public void init() throws FlowException, FxmlLoadException
//	{
//		WindowsManager.Page page = flowContext.getRegisteredObject(WindowsManager.Page.class);
//		AnimatedFlowContainer animatedFlowContainer = new AnimatedFlowContainer();
//		WindowsManager.Page sub = page.createSubPage(ControllerLogin.class, animatedFlowContainer);
//		sub.register(ControllerPreview.class);
//		root.getChildren().add(0, animatedFlowContainer.getView());
//	}

	public void onClose(ActionEvent event) {Platform.exit();}
}
