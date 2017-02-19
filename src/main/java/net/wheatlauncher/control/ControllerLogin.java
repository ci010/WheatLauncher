package net.wheatlauncher.control;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.launcher.AuthProfile;
import net.launcher.Logger;
import net.launcher.auth.Authorize;
import net.launcher.auth.AuthorizeFactory;
import net.launcher.utils.Tasks;
import net.wheatlauncher.MainApplication;
import net.wheatlauncher.control.utils.ValidatorDelegate;
import org.to2mbn.jmccc.auth.yggdrasil.core.RemoteAuthenticationException;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ControllerLogin
{
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

	public ValidatorDelegate accountValid, passwordValid;

	/*Controls parents*/
	@FXML
	private VBox box;

	@FXML
	private StackPane btnPane;

	@FXML
	private StackPane root;

	private ContextMenu accountMenu;
	private boolean shouldEnter;

	private MenuItem createItem(String s)
	{
		MenuItem item = new MenuItem(s);
		item.setOnAction(event ->
		{
			account.setText(item.getText());
			shouldEnter = false;
		});
		return item;
	}

	public ResourceBundle resources;

	@PostConstruct
	public void initialize()
	{
		Logger.trace("init");

		btnPane.getChildren().remove(spinner);//remove the spinner

		EventHandler<? super KeyEvent> fireLogin = event ->
		{
			if (event.getCode() == KeyCode.ENTER)
			{
				if (!shouldEnter)
				{
					shouldEnter = true;
					return;
				}
				login.fire();
			}
		};
		accountMenu = new ContextMenu();
		account.setText(MainApplication.getCore().getAuthProfile().getAccount());
		account.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (account.getText().length() == 0)
				accountMenu.hide();
			else
				accountMenu.getItems().setAll(MainApplication.getCore().getAuthProfile().getHistoryList().stream().filter(s -> s.startsWith(newValue))
						.map(this::createItem).collect(Collectors.toList()));
		});

		accountMenu.getItems().addListener((InvalidationListener) observable ->
		{
			if (accountMenu.getItems().isEmpty()) accountMenu.hide();
			else if (!accountMenu.isShowing()) accountMenu.show(account, Side.BOTTOM, 0, 0);
		});
		account.setOnKeyReleased(fireLogin);
		password.setOnKeyReleased(fireLogin);

		accountValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						MainApplication.getCore().getAuthProfile().getAuthorize()::validateUserName,
				MainApplication.getCore().getAuthProfile().authorizeProperty()));
		passwordValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						MainApplication.getCore().getAuthProfile().getAuthorize()::validatePassword,
				MainApplication.getCore().getAuthProfile().authorizeProperty()));

		account.textProperty().addListener(observable ->
		{
			if (account.validate()) MainApplication.getCore().getAuthProfile().setAccount(account.getText());
		});
		password.textProperty().addListener(observable ->
		{
			if (password.isDisable())
				return;
			if (password.validate()) MainApplication.getCore().getAuthProfile().setPassword(password.getText());
		});

		login.disableProperty().bind(Bindings.createBooleanBinding(() ->
						accountValid.hasErrorsProperty().get() ||
								(passwordValid.hasErrorsProperty().get() && !password.isDisable()),
				account.textProperty(),
				password.textProperty(), password.disableProperty()));

		AuthProfile authModule = MainApplication.getCore().getAuthProfile();
		//set up online forge
		Authorize selected = authModule.getAuthorize();
		if (selected == AuthorizeFactory.ONLINE)
			onlineMode.selectedProperty().setValue(true);
		else if (selected == AuthorizeFactory.OFFLINE)
			onlineMode.selectedProperty().setValue(false);
		else onlineMode.setDisable(true);

		onlineMode.selectedProperty().addListener((observable, o, n) ->
		{
			AuthProfile module = MainApplication.getCore().getAuthProfile();
			module.setAuthorize(n ? AuthorizeFactory.ONLINE : AuthorizeFactory.OFFLINE);
			account.setText("");
			account.resetValidation();
			password.setText("");
			password.resetValidation();
		}); //Common

		password.disableProperty().bind(Bindings.createBooleanBinding(() ->
						MainApplication.getCore().getAuthProfile().getAuthorize() == AuthorizeFactory.OFFLINE,
				MainApplication.getCore().getAuthProfile().authorizeProperty()));

		account.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(MainApplication.getCore().getAuthProfile().getAuthorize());
			return resources.getString(id + ".account");
		}, MainApplication.getCore().getAuthProfile().authorizeProperty()));

		password.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(MainApplication.getCore().getAuthProfile().getAuthorize());
			return resources.getString(id + ".password");
		}, MainApplication.getCore().getAuthProfile().authorizeProperty()));
		Logger.trace("onWatch listener to core's launch profile");
	}

	public void login(ActionEvent event)
	{
		btnPane.getChildren().remove(login);
		btnPane.getChildren().add(spinner);
		onlineMode.setDisable(true);
		MainApplication.getCore().getService().submit(Tasks.builder(() ->
		{
			AuthProfile module = MainApplication.getCore().getAuthProfile();
			return module.getAuthorize().auth(module.getAccount(), module.getPassword());
		}).setDone((result) -> Platform.runLater(() ->
		{
			MainApplication.getCore().getAuthProfile().setCache(result);
			btnPane.getChildren().remove(spinner);
			btnPane.getChildren().add(login);
			((Consumer) root.getScene().getUserData()).accept("PREVIEW");
			onlineMode.setDisable(false);
		})).setException((e) ->
				Platform.runLater(() ->
				{
					Throwable ex = e.getCause() == null ? e : e.getCause();
					if (ex instanceof RemoteAuthenticationException)
						if (ex.getMessage().equals("ForbiddenOperationException: Invalid credentials. Invalid username or password."))
							MainApplication.displayError(root.getScene(), "login.invalid.credentials");
						else
							MainApplication.displayError(root.getScene(), e);
					else if (ex.getCause() instanceof UnknownHostException)
						MainApplication.displayError(root.getScene(), "login.network.error");
					else
						MainApplication.displayError(root.getScene(), e);
					btnPane.getChildren().remove(spinner);
					btnPane.getChildren().add(login);
					onlineMode.setDisable(false);
				})).build());

	}

	@FXML
	protected void onClick(MouseEvent event)
	{
		if (event.getButton() == MouseButton.PRIMARY)
			box.requestFocus();
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
