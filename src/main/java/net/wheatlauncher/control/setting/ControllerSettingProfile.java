package net.wheatlauncher.control.setting;

import com.jfoenix.controls.*;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import net.launcher.utils.Logger;
import net.wheatlauncher.control.ReloadableController;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
public class ControllerSettingProfile implements ReloadableController
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
	public JFXListView<Label> optionList;

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@PostConstruct
	public void setupProfileOptionDialogs()
	{
		Logger.trace("init");

		dialogHolder.getChildren().remove(renameProfileDialog);
		dialogHolder.getChildren().remove(newProfileDialog);
		dialogHolder.getChildren().remove(deleteProfileDialog);

		cancelRename.setOnMouseClicked(event -> renameProfileDialog.close());
		cancelDelete.setOnMouseClicked(event -> deleteProfileDialog.close());
		cancelNewProfile.setOnMouseClicked(event -> newProfileDialog.close());

		renameProfile.setOnMouseClicked(event ->
		{
			optionList.getSelectionModel().clearSelection();
			profilePopup.close();
			renameProfileDialog.show(context.getRegisteredObject(StackPane.class));
		});
		newProfile.setOnMouseClicked(event ->
		{
			optionList.getSelectionModel().clearSelection();
			profilePopup.close();
			newProfileDialog.show(context.getRegisteredObject(StackPane.class));
		});
		deleteProfile.setOnMouseClicked(event ->
				{
					optionList.getSelectionModel().clearSelection();
					profilePopup.close();
					deleteProfileDialog.show(context.getRegisteredObject(StackPane.class));
				}
		);
	}

	@Override
	public void reload()
	{
		Logger.trace("reload");
	}

	@Override
	public void unload()
	{

	}

	public void beforeRename(MouseEvent mouseEvent) {
		Logger.trace("");

		renameProfileDialog.requestLayout();
		renameProfileTextField.requestLayout();
	}
}
