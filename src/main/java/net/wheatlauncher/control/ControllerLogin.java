package net.wheatlauncher.control;

import com.jfoenix.controls.*;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.launcher.AuthProfile;
import net.launcher.Bootstrap;
import net.launcher.auth.Authorize;
import net.launcher.auth.AuthorizeFactory;
import net.launcher.utils.CallbacksOption;
import net.launcher.utils.Logger;
import net.wheatlauncher.Core;
import net.wheatlauncher.control.utils.ReloadableController;
import net.wheatlauncher.control.utils.ValidatorDelegate;
import net.wheatlauncher.control.utils.WindowsManager;
import net.wheatlauncher.utils.LanguageMap;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.auth.yggdrasil.core.RemoteAuthenticationException;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;

/**
 * @author ci010
 */
@FXMLController(value = "/fxml/Login.fxml", title = "Simple Launcher")
public class ControllerLogin
		implements ReloadableController
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	/*MainApplication controls*/
	@FXML
	private JFXComboBox<String> account;

	@FXML
	private JFXPasswordField password;

	@FXML
	private JFXToggleNode onlineMode = new JFXToggleNode();

	@FXML
	private JFXButton login;

	/*Side controls*/
	@FXML
	private JFXSpinner spinner;

	public ValidatorDelegate accountValid, passwordValid;

	/*Controls parents*/
	@FXML
	private VBox box;

	@FXML
	private StackPane btnPane;

	@FXML
	private StackPane root;

	@PostConstruct
	public void init()
	{
		Logger.trace("init");

		btnPane.getChildren().remove(spinner);//remove the spinner

		EventHandler<? super KeyEvent> fireLogin = event ->
		{
			if (event.getCode() == KeyCode.ENTER) login.fire();
		};
		account.setOnKeyReleased(fireLogin);
		password.setOnKeyReleased(fireLogin);

		accountValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						Bootstrap.getCore().getAuthProfile().getAuthorize()::validateUserName,
				Bootstrap.getCore().getAuthProfile().authorizeProperty()));
		passwordValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						Bootstrap.getCore().getAuthProfile().getAuthorize()::validatePassword,
				Bootstrap.getCore().getAuthProfile().authorizeProperty()));

		account.getJFXEditor().textProperty().addListener(observable ->
		{
			if (ValidationFacade.validate(account))
				Bootstrap.getCore().getAuthProfile().setAccount(account.getJFXEditor().getText());
		});
		account.itemsProperty().bind(Bindings.createObjectBinding(() ->
						Core.getInstance().getAuthProfile().getHistoryList(),
				Core.getInstance().getAuthProfile().authorizeProperty()));
		password.textProperty().addListener(observable ->
		{
			if (password.isDisable())
				return;
			if (password.validate()) Bootstrap.getCore().getAuthProfile().setPassword(password.getText());
		});

		login.disableProperty().bind(Bindings.createBooleanBinding(() ->
						accountValid.hasErrorsProperty().get() ||
								(passwordValid.hasErrorsProperty().get() && !password.isDisable()),
				account.valueProperty(), account.jfxEditorProperty().get().textProperty(),
				password.textProperty(), password.disableProperty()));

		AuthProfile authModule = Bootstrap.getCore().getAuthProfile();
		//set up online mod
		Authorize selected = authModule.getAuthorize();
		if (selected == AuthorizeFactory.ONLINE)
			onlineMode.selectedProperty().setValue(true);
		else if (selected == AuthorizeFactory.OFFLINE)
			onlineMode.selectedProperty().setValue(false);
		else onlineMode.setDisable(true);

		onlineMode.selectedProperty().addListener((observable, o, n) ->
		{
			AuthProfile module = Bootstrap.getCore().getAuthProfile();
			module.setAuthorize(n ? AuthorizeFactory.ONLINE : AuthorizeFactory.OFFLINE);
			account.setValue("");
			ValidationFacade.reset(account);
			password.setText("");
			password.reset();
		}); //Common

		password.disableProperty().bind(Bindings.createBooleanBinding(() ->
						Bootstrap.getCore().getAuthProfile().getAuthorize() == AuthorizeFactory.OFFLINE,
				Bootstrap.getCore().getAuthProfile().authorizeProperty()));

		account.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(Bootstrap.getCore().getAuthProfile().getAuthorize());
			return LanguageMap.INSTANCE.translate(id + ".account");
		}, Bootstrap.getCore().getAuthProfile().authorizeProperty()));

		password.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(Bootstrap.getCore().getAuthProfile().getAuthorize());
			return LanguageMap.INSTANCE.translate(id + ".password");
		}, Bootstrap.getCore().getAuthProfile().authorizeProperty()));
		Logger.trace("onWatch listener to core's launch profile");
	}

	public void login(ActionEvent event)
	{
		btnPane.getChildren().remove(login);
		btnPane.getChildren().add(spinner);
		onlineMode.setDisable(true);
		Bootstrap.getCore().getService().submit(CallbacksOption.wrap(() ->
		{
			AuthProfile module = Bootstrap.getCore().getAuthProfile();
			return module.getAuthorize().auth(module.getAccount(), module.getPassword());
		}, new CallbackAdapter<AuthInfo>()
		{
			@Override
			public void done(AuthInfo result)
			{
				Platform.runLater(() ->
				{
					Bootstrap.getCore().getAuthProfile().setCache(result);
					btnPane.getChildren().remove(spinner);
					btnPane.getChildren().add(login);
					switchToPreview();
					onlineMode.setDisable(false);
				});
			}

			@Override
			public void failed(Throwable e)
			{
				Platform.runLater(() ->
				{
					Throwable ex = e.getCause() == null ? e : e.getCause();
					if (ex instanceof RemoteAuthenticationException)
						if (ex.getMessage().equals("ForbiddenOperationException: Invalid credentials. Invalid username or password."))
							flowContext.getRegisteredObject(WindowsManager.Page.class).displayError("login.invalid.credentials");
						else
							flowContext.getRegisteredObject(WindowsManager.Page.class).displayError(e);
					else if (ex.getCause() instanceof UnknownHostException)
						flowContext.getRegisteredObject(WindowsManager.Page.class).displayError("login.network.error");
					else
						flowContext.getRegisteredObject(WindowsManager.Page.class).displayError(e);
					btnPane.getChildren().remove(spinner);
					btnPane.getChildren().add(login);
					onlineMode.setDisable(false);
				});
			}
		}));
	}

	private void switchToPreview()
	{
		flowContext.getRegisteredObject(WindowsManager.Page.class).switchPage(ControllerPreview.class);
	}

	@FXML
	protected void onClick(MouseEvent event)
	{
		if (event.getButton() == MouseButton.PRIMARY)
			box.requestFocus();
	}

	@Override
	public void reload()
	{

	}

	@Override
	public void unload()
	{

	}

	public void switchOnlineMode(KeyEvent event)
	{
		if (event.isShiftDown() && event.isControlDown() && event.getCode() == KeyCode.TAB)
			onlineMode.fire();
	}

	public void onGlobalKeyPressed(KeyEvent event)
	{
		switchOnlineMode(event);
	}
}
