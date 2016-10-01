package net.wheatlauncher.gui;

import com.jfoenix.controls.*;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.launcher.utils.Logger;
import net.launcher.utils.State;
import net.wheatlauncher.Core;
import net.wheatlauncher.utils.EventListenerUtils;
import net.wheatlauncher.utils.LanguageMap;
import net.wheatlauncher.utils.ValidatorAuth;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
@FXMLController(value = "/fxml/Login.fxml", title = "Simple Launcher")
public class ControllerLogin implements ReloadableController
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private JFXTextField account;

	@FXML
	private JFXPasswordField password;

	@FXML
	private JFXSpinner spinner;

	@FXML
	private JFXToggleNode onlineMode = new JFXToggleNode();

	@FXML
	private JFXButton login;

	@FXML
	private VBox box;

	@FXML
	private StackPane btnPane;

	@FXML
	private ValidatorAuth accountValid, passwordValid;

	@FXML
	private StackPane root;

	@FXML
	private ValidationFacade launchValid;

	private JFXDialog dialog = new JFXDialog();

	@PostConstruct
	public void init()
	{
		Logger.trace("init");
		btnPane.getChildren().remove(spinner);

		onlineMode.setTooltip(new Tooltip(LanguageMap.INSTANCE.translate("online.mode")));
		account.setValidators(accountValid);
		password.setValidators(passwordValid);
		EventHandler<? super KeyEvent> handler = event ->
		{
			if (event.getCode() == KeyCode.ENTER) login.fire();
		};
		this.account.setOnKeyReleased(handler);
		this.password.setOnKeyReleased(handler);

		accountValid.hasErrorsProperty().addListener(observable -> accountValid.validate());
		passwordValid.hasErrorsProperty().addListener(observable -> passwordValid.validate());

		account.textProperty().addListener(observable -> account.validate());
		password.textProperty().addListener(observable -> password.validate());

		login.setOnAction(event -> flowContext.getRegisteredObject(PageManager.class).switchToQuite("preview"));

		Logger.trace("onWatch listener to core's launch profile");
		Core.INSTANCE.selectedProfileProperty().addListener(((observable, oldValue, newValue) ->
		{
			Logger.trace("detect launch profile change");
			if (oldValue != null)
			{
				oldValue.accountProperty().unbind();
				oldValue.passwordProperty().unbind();

				oldValue.loginState().removeListener(stateChangeListener);
				oldValue.settingName().removeListener(settingChangeListener);

				oldValue.accountProperty().state().removeListener(accountValid);
				oldValue.passwordProperty().state().removeListener(passwordValid);

				onlineMode.selectedProperty().unbindBidirectional(oldValue.onlineModeProperty());
			}

			EventListenerUtils.addListenerAndNotify(newValue.loginState(), stateChangeListener);
			EventListenerUtils.addListenerAndNotify(newValue.settingName(), settingChangeListener);

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

	private ChangeListener<State.Values> stateChangeListener = (obv, oldV, newV) ->
	{
		Logger.trace("login state change " + oldV + " -> " + newV);
		if (oldV == State.Values.PENDING)
		{
			btnPane.getChildren().remove(spinner);
			btnPane.getChildren().add(launchValid);
		}
		if (newV == State.Values.PENDING)
		{
			btnPane.getChildren().remove(launchValid);
			btnPane.getChildren().add(spinner);
		}
		else if (newV == State.Values.FAIL)
			login.setDisable(true);
		else if (newV == State.Values.PASS)
			login.setDisable(false);
	};

	private ChangeListener<String> settingChangeListener = (observable, oldValue, newValue) ->
	{
		Logger.trace("Auth Setting change " + oldValue + " -> " + newValue);

		//clear the old data. I don't know why I have to do this twice after I bindBiDirection....
		this.account.clear();
		this.password.clear();

		account.setPromptText(LanguageMap.INSTANCE.translate(newValue + ".account"));
		accountValid.setOnlineType(newValue);
		password.setDisable(!Core.INSTANCE.selectedProfileProperty().get().isPasswordEnable());
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

	@Override
	public void unload()
	{

	}
}
