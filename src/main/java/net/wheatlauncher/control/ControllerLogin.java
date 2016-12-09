package net.wheatlauncher.control;

import com.jfoenix.controls.*;
import io.datafx.controller.FXMLController;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.context.ApplicationContext;
import io.datafx.controller.flow.FlowException;
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
import net.wheatlauncher.utils.LanguageMap;
import org.to2mbn.jmccc.auth.AuthInfo;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;

import javax.annotation.PostConstruct;

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
	private JFXTextField account;

	@FXML
	private JFXPasswordField password;

	@FXML
	private JFXToggleNode onlineMode = new JFXToggleNode();

	@FXML
	private JFXButton login;

	/*Side controls*/
	@FXML
	private JFXSpinner spinner;

	@FXML
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
						Bootstrap.getCore().getAuthModule().getAuthorize()::validateUserName,
				Bootstrap.getCore().getAuthModule().authorizeProperty()));
		passwordValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						Bootstrap.getCore().getAuthModule().getAuthorize()::validatePassword,
				Bootstrap.getCore().getAuthModule().authorizeProperty()));

		account.textProperty().addListener(observable ->
		{
			if (account.validate()) Bootstrap.getCore().getAuthModule().setAccount(account.getText());
		});
		password.textProperty().addListener(observable ->
		{
			if (password.isDisable())
				return;
			if (password.validate()) Bootstrap.getCore().getAuthModule().setPassword(password.getText());
		});

		account.validate();
		password.validate();
		login.disableProperty().bind(Bindings.createBooleanBinding(() ->
						accountValid.hasErrorsProperty().get() ||
								(passwordValid.hasErrorsProperty().get() && !password.isDisable()),
				account.textProperty(), password.textProperty(), password.disableProperty()));

		AuthProfile authModule = Bootstrap.getCore().getAuthModule();
		//set up online mod
		Authorize selected = authModule.getAuthorize();
		if (selected == AuthorizeFactory.ONLINE)
			onlineMode.selectedProperty().setValue(true);
		else if (selected == AuthorizeFactory.OFFLINE)
			onlineMode.selectedProperty().setValue(false);
		else onlineMode.setDisable(true);

		onlineMode.selectedProperty().addListener((observable, o, n) ->
		{
			AuthProfile module = Bootstrap.getCore().getAuthModule();
			module.setAuthorize(n ?
					AuthorizeFactory.ONLINE :
					AuthorizeFactory.OFFLINE);
			account.setText("");
			password.setText("");
		}); //Common

		password.disableProperty().bind(Bindings.createBooleanBinding(() ->
						Bootstrap.getCore().getAuthModule().getAuthorize() == AuthorizeFactory.OFFLINE,
				Bootstrap.getCore().getAuthModule().authorizeProperty()));

		account.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(Bootstrap.getCore().getAuthModule().getAuthorize());
			return LanguageMap.INSTANCE.translate(id + ".account");
		}, Bootstrap.getCore().getAuthModule().authorizeProperty()));

		password.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(Bootstrap.getCore().getAuthModule().getAuthorize());
			return LanguageMap.INSTANCE.translate(id + ".password");
		}, Bootstrap.getCore().getAuthModule().authorizeProperty()));
		Logger.trace("onWatch listener to core's launch profile");
	}

	public void login(ActionEvent event)
	{
		btnPane.getChildren().remove(login);
		btnPane.getChildren().add(spinner);
		Bootstrap.getCore().getService().submit(CallbacksOption.wrap(() ->
		{
			AuthProfile module = Bootstrap.getCore().getAuthModule();
			return module.getAuthorize().auth(module.getAccount(), module.getPassword());
		}, new CallbackAdapter<AuthInfo>()
		{
			@Override
			public void done(AuthInfo result)
			{
				Platform.runLater(() ->
				{
					Bootstrap.getCore().getAuthModule().setCache(result);
					btnPane.getChildren().remove(spinner);
					btnPane.getChildren().add(login);
					switchToPreview();
				});
			}

			@Override
			public void failed(Throwable e)
			{
				Platform.runLater(() ->
				{
					flowContext.getRegisteredObject(WindowsManager.Page.class).displayError(e);
					btnPane.getChildren().remove(spinner);
					btnPane.getChildren().add(login);
				});
			}
		}));
	}

	private void switchToPreview()
	{
		WindowsManager manager = ApplicationContext.getInstance().getRegisteredObject(WindowsManager.class);
		WindowsManager.Page page = flowContext.getRegisteredObject(WindowsManager.Page.class);
		try
		{
			manager.createPage(page.getStage(), ControllerPreview.class, 600, 400);
		}
		catch (FlowException | FxmlLoadException e)
		{
			e.printStackTrace();
		}
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
}
