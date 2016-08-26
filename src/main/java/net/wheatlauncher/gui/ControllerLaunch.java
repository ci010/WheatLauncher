package net.wheatlauncher.gui;

import com.jfoenix.controls.*;
import com.jfoenix.validation.ValidationFacade;
import com.jfoenix.validation.base.ValidatorBase;
import io.datafx.controller.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.wheatlauncher.Core;
import net.wheatlauncher.utils.*;

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

	@FXML
	private ValidationFacade launchValid;
	private void setupGUI()
	{
		launchValid.setValidators(new ValidatorBase() {
			@Override
			protected void eval()
			{
			}
		});
		launchValid.setOnMouseClicked(event -> {
			ValidationFacade.validate(launch);
		});
		spinner = new JFXSpinner();
		spinner.setStyle("-fx-radius:16");
		spinner.getStyleClass().add("materialDesign-purple, first-spinner");
		spinner.startingAngleProperty().set(-40);
		onlineMode.setSelectedColor(Color.WHEAT);
		onlineMode.setTooltip(new Tooltip(LanguageMap.INSTANCE.translate("online.mode")));
		account.setValidators(accountValid);
		password.setValidators(passwordValid);
	}

	@PostConstruct
	public void init()
	{
		Logger.trace("init");
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

			ListenerUtils.addListenerAndNotify(newValue.launchState(), stateChangeListener);
			ListenerUtils.addListenerAndNotify(newValue.settingName(), settingChangeListener);

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
		Core.INSTANCE.tryLaunch();
	}

	private ChangeListener<StrictProperty.EnumState> stateChangeListener = (obv, oldV, newV) -> {
		Logger.trace("launch state change " + oldV + " -> " + newV);
		if (oldV == StrictProperty.EnumState.PENDING)
		{
			btnPane.getChildren().remove(spinner);
			btnPane.getChildren().add(launchValid);
		}
		if (newV == StrictProperty.EnumState.PENDING)
		{
			btnPane.getChildren().remove(launchValid);
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
		accountValid.setOnlineType(newValue);
		password.setDisable(!Core.INSTANCE.selectLaunchProfile().get().isPasswordEnable());
		if (!password.isDisable())
		{
			password.setPromptText(LanguageMap.INSTANCE.translate(newValue + ".password"));
			passwordValid.setOnlineType(newValue);
		}
	};

	@Override
	public void reload()
	{
	}
}
