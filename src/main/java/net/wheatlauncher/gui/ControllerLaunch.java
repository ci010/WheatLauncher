package net.wheatlauncher.gui;

import com.jfoenix.controls.*;
import io.datafx.controller.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.wheatlauncher.Core;
import net.wheatlauncher.utils.LanguageMap;
import net.wheatlauncher.utils.Logger;
import net.wheatlauncher.utils.StrictProperty;
import net.wheatlauncher.utils.ValidatorAuth;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
@FXMLController(value = "/fxml/Launch.fxml", title = "Simple Launcher")
public class ControllerLaunch implements ReloadableController
{
	@FXML
	private JFXTextField account;

	@FXML
	private JFXPasswordField password;
	private JFXSpinner spinner;

	@FXML
	private JFXToggleNode onlineMode = new JFXToggleNode();
	@FXML
	private JFXButton launch;

	@FXML
	private VBox box;

	@FXML
	private StackPane btnPane;

	private ValidatorAuth
			accountValid = new ValidatorAuth("account"),
			passwordValid = new ValidatorAuth("password");

	private void setupGUI()
	{
		spinner = new JFXSpinner();
		spinner.setStyle("-fx-radius:16");
		spinner.getStyleClass().add("materialDesign-purple, first-spinner");
		spinner.startingAngleProperty().set(-40);
		onlineMode.setSelectedColor(Color.WHEAT);
		onlineMode.setTooltip(new Tooltip(LanguageMap.INSTANCE.translate("online.mode")));
	}

	@PostConstruct
	public void init()
	{
		Logger.trace("init.");
		setupGUI();

		Logger.trace("add listener to core's launch profile");
		Core.INSTANCE.selectLaunchProfile().addListener(((observable, oldValue, newValue) -> {
			Logger.trace("detect launch profile change");
			if (oldValue != null)
			{
				oldValue.accountProperty().unbind();
				oldValue.passwordProperty().unbind();

				oldValue.launchState().removeListener(stateChangeListener);
				oldValue.settingName().removeListener(settingChangeListener);

				oldValue.accountProperty().state().removeListener(accountValid);
				oldValue.passwordProperty().state().removeListener(passwordValid);

				onlineMode.selectedProperty().unbindBidirectional(oldValue.onlineModeProperty());
			}

			newValue.launchState().addListener(stateChangeListener);
			stateChangeListener.changed(newValue.launchState(), null, newValue.launchState().getValue());
			//important... maybe this is a problem of framework design.., I have to refresh this so that it could
			// adapt initial state.

			newValue.settingName().addListener(settingChangeListener);
			settingChangeListener.changed(newValue.settingName(), null, newValue.settingName().getValue());

			newValue.accountProperty().bindBidirectional(account.textProperty());
			newValue.accountProperty().state().addListener(accountValid);

			newValue.passwordProperty().bindBidirectional(password.textProperty());
			newValue.passwordProperty().state().addListener(passwordValid);

			onlineMode.selectedProperty().bindBidirectional(newValue.onlineModeProperty());
		}));
	}

	@FXML
	protected void onClick(MouseEvent event)
	{
		if (event.getButton() == MouseButton.PRIMARY)
			box.requestFocus();
	}

	@FXML
	protected void launchAction(ActionEvent event)
	{
//		LaunchProfile.getCurrent().launch();
	}

	private ChangeListener<StrictProperty.EnumState> stateChangeListener = (obv, oldV, newV) -> {
		Logger.trace("launch state change " + oldV + " -> " + newV);
		if (oldV == StrictProperty.EnumState.PENDING)
		{
			btnPane.getChildren().remove(spinner);
			btnPane.getChildren().add(launch);
		}
		if (newV == StrictProperty.EnumState.PENDING)
		{
			btnPane.getChildren().remove(launch);
			btnPane.getChildren().add(spinner);
		}
		else if (newV == StrictProperty.EnumState.FAIL)
			launch.setDisable(true);
		else if (newV == StrictProperty.EnumState.PASS)
			launch.setDisable(false);
	};
	private ChangeListener<String> settingChangeListener = (observable, oldValue, newValue) -> {
		Logger.trace("Auth Setting change " + oldValue + " -> " + newValue);
		account.setPromptText(LanguageMap.INSTANCE.translate(newValue + ".account"));
		password.setPromptText(LanguageMap.INSTANCE.translate(newValue + ".password"));
		accountValid.setOnlineType(newValue);
		passwordValid.setOnlineType(newValue);
		password.setDisable(!Core.INSTANCE.selectLaunchProfile().get().isPasswordEnable());
	};

	@Override
	public void reload()
	{
	}
}
