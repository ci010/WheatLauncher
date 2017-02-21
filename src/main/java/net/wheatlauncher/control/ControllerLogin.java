package net.wheatlauncher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
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
import net.launcher.Logger;
import net.launcher.auth.AuthManager;
import net.launcher.auth.Authorize;
import net.launcher.control.OnlineModeSwitch;
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
	private OnlineModeSwitch onlineMode;

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

	public ResourceBundle resources;

	@PostConstruct
	public void initialize()
	{
		Logger.trace("init");

		btnPane.getChildren().remove(spinner);//remove the spinner

		onlineMode.getSelectionModel().selectedIndexProperty().addListener(observable ->
		{
			account.setText("");
			account.resetValidation();
			password.setText("");
			password.resetValidation();
		});
		onlineMode.isOfflineProperty().addListener(observable ->
		{
			account.setText("");
			account.resetValidation();
			password.setText("");
			password.resetValidation();
		});
		accountMenu = new ContextMenu();
		account.setText(MainApplication.getCore().getAuthManager().getAccount());
		account.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (account.getText().length() == 0)
				accountMenu.hide();
			else
				accountMenu.getItems().setAll(MainApplication.getCore().getAuthManager().
						getHistoryList().stream().filter(s -> s.startsWith(newValue))
						.map(this::createItem).collect(Collectors.toList()));
		});

		accountMenu.getItems().addListener((InvalidationListener) observable ->
		{
			if (accountMenu.getItems().isEmpty()) accountMenu.hide();
			else if (!accountMenu.isShowing()) accountMenu.show(account, Side.BOTTOM, 0, 0);
		});

		accountValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						MainApplication.getCore().getAuthManager().getAuthorizeInstance()::validateUserName,
				MainApplication.getCore().getAuthManager().authorizeProperty()));
		passwordValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						MainApplication.getCore().getAuthManager().getAuthorizeInstance()::validatePassword,
				MainApplication.getCore().getAuthManager().authorizeProperty()));

		account.textProperty().addListener(observable ->
		{
			if (account.validate()) MainApplication.getCore().getAuthManager().setAccount(account.getText());
		});
		password.textProperty().addListener(observable ->
		{
			if (password.isDisable())
				return;
			if (password.validate()) MainApplication.getCore().getAuthManager().setPassword(password.getText());
		});

		login.disableProperty().bind(Bindings.createBooleanBinding(() ->
						accountValid.hasErrorsProperty().get() ||
								(passwordValid.hasErrorsProperty().get() && !password.isDisable()),
				account.textProperty(), password.textProperty(), password.disableProperty()));


		password.disableProperty().bind(Bindings.createBooleanBinding(() ->
						MainApplication.getCore().getAuthManager().isOffline(),
				MainApplication.getCore().getAuthManager().authorizeInstanceProperty()));

		account.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(MainApplication.getCore().getAuthManager().getAuthorizeInstance());
			return resources.getString(id + ".account");
		}, MainApplication.getCore().getAuthManager().authorizeInstanceProperty()));

		password.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(MainApplication.getCore().getAuthManager().getAuthorizeInstance());
			return resources.getString(id + ".password");
		}, MainApplication.getCore().getAuthManager().authorizeInstanceProperty()));
		Logger.trace("onWatch listener to core's launch profile");
	}

	public void login(ActionEvent event)
	{
		btnPane.getChildren().remove(login);
		btnPane.getChildren().add(spinner);
//		onlineMode.setDisable(true);
		MainApplication.getCore().getService().submit(Tasks.builder(() ->
		{
			AuthManager module = MainApplication.getCore().getAuthManager();
			return module.getAuthorizeInstance().auth(module.getAccount(), module.getPassword());
		}).setDone((result) -> Platform.runLater(() ->
		{
			MainApplication.getCore().getAuthManager().setCache(result);
			btnPane.getChildren().remove(spinner);
			btnPane.getChildren().add(login);
			((Consumer) root.getScene().getUserData()).accept("PREVIEW");
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
			onlineMode.setIsOffline(!onlineMode.isOffline());
	}

	public void onGlobalKeyPressed(KeyEvent event)
	{
		switchOnlineMode(event);
	}

	private boolean shouldEnter;

	public void toggleLogin(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
		{
			if (!shouldEnter)
			{
				shouldEnter = true;
				return;
			}
			login.fire();
		}
	}

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

	public void openPopup()
	{

	}
}
