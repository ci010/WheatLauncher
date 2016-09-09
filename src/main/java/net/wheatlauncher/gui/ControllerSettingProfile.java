package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import net.wheatlauncher.utils.Logger;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
public class ControllerSettingProfile
{
	public JFXButton cancelRename;
	public JFXButton acceptRename;
	public JFXButton cancelDelete;
	public JFXButton acceptDelete;
	public JFXButton cancelNewProfile;
	public JFXButton acceptNewProfile;

	public JFXTextField renameProfileTextField;
	public JFXTextField newProfileTextField;

	public JFXDialog renameProfileDialog;
	public JFXDialog newProfileDialog;
	public JFXDialog deleteProfileDialog;

	public Label renameProfile;
	public Label newProfile;
	public Label deleteProfile;

	public JFXPopup profilePopup;
	public StackPane dialogHolder;

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@PostConstruct
	public void setupProfileOptionDialogs()
	{
		Logger.trace("setup setting profile");
		dialogHolder.getChildren().remove(renameProfileDialog);
		dialogHolder.getChildren().remove(newProfileDialog);
		dialogHolder.getChildren().remove(deleteProfileDialog);

		PageManager manager = context.getRegisteredObject(PageManager.class);
		renameProfile.setOnMouseClicked(event -> renameProfileDialog.show((StackPane) manager.getCurrentSurfaceContainer()));
		newProfile.setOnMouseClicked(event -> newProfileDialog.show((StackPane) manager.getCurrentSurfaceContainer()));
		deleteProfile.setOnMouseClicked(event -> deleteProfileDialog.show((StackPane) manager.getCurrentSurfaceContainer()));
	}
}
