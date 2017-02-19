package net.wheatlauncher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * @author ci010
 */
public class ControllerMain
{
	public StackPane root;
	public JFXSnackbar bar;
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

	public void initialize()
	{
	}

	public void onClose(ActionEvent event) {Platform.exit();}

	public void displayError(String message)
	{
		bar.enqueue(new JFXSnackbar.SnackbarEvent(message));
	}

	public void displayError(Throwable message)
	{
		bar.enqueue(new JFXSnackbar.SnackbarEvent(message.getMessage()));
	}
}
