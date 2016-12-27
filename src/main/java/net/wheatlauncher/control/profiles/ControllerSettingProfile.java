package net.wheatlauncher.control.profiles;

import com.jfoenix.controls.*;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import net.launcher.Bootstrap;
import net.launcher.profile.LaunchProfileManager;
import net.launcher.utils.Logger;
import net.launcher.utils.StringUtils;
import net.wheatlauncher.control.utils.ReloadableController;
import net.wheatlauncher.control.utils.WindowsManager;

import javax.annotation.PostConstruct;
import java.util.Objects;

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

		acceptRename.disableProperty().bind(Bindings.createBooleanBinding(
				() -> StringUtils.isEmpty(renameProfileTextField.getText()),
				renameProfileTextField.textProperty()));
		renameProfile.setOnMouseClicked(event ->
		{
			optionList.getSelectionModel().clearSelection();
			profilePopup.close();
			renameProfileDialog.show(context.getRegisteredObject(StackPane.class));
			renameProfileDialog.setOnDialogOpened(e -> beforeRename());
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

	private void beforeRename()
	{
		renameProfileTextField.requestFocus();
		renameProfileDialog.requestLayout();
		renameProfileTextField.requestLayout();
	}

	public void acceptRename(ActionEvent event)
	{
		LaunchProfileManager manager = Bootstrap.getCore().getProfileManager();
		try
		{
			if (!Objects.equals(manager.selecting().getDisplayName(), renameProfileTextField.getText()))
				manager.selecting().setDisplayName(this.renameProfileTextField.getText());
		}
		catch (Exception e)
		{
			context.getRegisteredObject(WindowsManager.Page.class).displayError(e);
		}
		renameProfileTextField.clear();
		renameProfileDialog.close();
	}

	public void acceptCreate(ActionEvent event)
	{
		LaunchProfileManager manager = Bootstrap.getCore().getProfileManager();
		try
		{
			manager.newProfile(newProfileTextField.getText());
			newProfileDialog.close();
			newProfileTextField.clear();
		}
		catch (Exception e)
		{
			context.getRegisteredObject(WindowsManager.Page.class).displayError(e);
		}
	}

	public void acceptDelete(ActionEvent event)
	{
		LaunchProfileManager manager = Bootstrap.getCore().getProfileManager();
		try
		{
			manager.deleteProfile(manager.getSelectedProfile());
			deleteProfileDialog.close();
		}
		catch (Exception e)
		{
			context.getRegisteredObject(WindowsManager.Page.class).displayError(e);
		}
	}
}
