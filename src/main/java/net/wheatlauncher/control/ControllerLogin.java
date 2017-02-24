package net.wheatlauncher.control;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
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
import net.launcher.api.ARML;
import net.launcher.auth.AuthManager;
import net.launcher.auth.Authorize;
import net.launcher.control.OnlineModeSwitch;
import net.launcher.utils.Tasks;
import net.wheatlauncher.MainApplication;
import net.wheatlauncher.control.utils.ValidatorDelegate;
import org.to2mbn.jmccc.auth.yggdrasil.core.RemoteAuthenticationException;

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

	@FXML
	private JFXSpinner spinner;

	public ValidatorDelegate accountValid, passwordValid;

	@FXML
	private VBox box;

	@FXML
	private StackPane btnPane;

	@FXML
	private StackPane root;

	private ContextMenu accountMenu;

	public ResourceBundle resources;

	public void initialize()
	{
		btnPane.getChildren().remove(spinner);//remove the spinner

		InvalidationListener listener = observable ->
		{
			String account;
			ObservableList<String> historyList = ARML.core().getAuthManager().getHistoryList();
			if (historyList == null || historyList.isEmpty()) account = "";
			else account = historyList.get(0);

			this.account.setText(account);
			this.account.resetValidation();
			this.password.setText("");
			this.password.resetValidation();
		};
		onlineMode.getSelectionModel().selectedIndexProperty().addListener(listener);
		onlineMode.isOfflineProperty().addListener(listener);
		accountMenu = new ContextMenu();
		account.setText(ARML.core().getAuthManager().getAccount());
		account.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (account.getText().length() == 0)
				accountMenu.hide();
			else
				accountMenu.getItems().setAll(ARML.core().getAuthManager().
						getHistoryList().stream().filter(s -> s.startsWith(newValue))
						.map(this::createItem).collect(Collectors.toList()));
		});

		accountMenu.getItems().addListener((InvalidationListener) observable ->
		{
			if (accountMenu.getItems().isEmpty()) accountMenu.hide();
			else if (!accountMenu.isShowing()) accountMenu.show(account, Side.BOTTOM, 0, 0);
		});

		accountValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						ARML.core().getAuthManager().getAuthorizeInstance()::validateUserName,
				ARML.core().getAuthManager().authorizeProperty()));
		passwordValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						ARML.core().getAuthManager().getAuthorizeInstance()::validatePassword,
				ARML.core().getAuthManager().authorizeProperty()));

		account.textProperty().addListener(observable ->
		{
			if (account.validate()) ARML.core().getAuthManager().setAccount(account.getText());
		});
		password.textProperty().addListener(observable ->
		{
			if (password.isDisable())
				return;
			if (password.validate()) ARML.core().getAuthManager().setPassword(password.getText());
		});

		login.disableProperty().bind(Bindings.createBooleanBinding(() ->
						accountValid.hasErrorsProperty().get() ||
								(passwordValid.hasErrorsProperty().get() && !password.isDisable()),
				account.textProperty(), password.textProperty(), password.disableProperty()));


		password.disableProperty().bind(Bindings.createBooleanBinding(() ->
						ARML.core().getAuthManager().isOffline(),
				ARML.core().getAuthManager().authorizeInstanceProperty()));

		account.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(ARML.core().getAuthManager().getAuthorizeInstance());
			return resources.getString(id + ".account");
		}, ARML.core().getAuthManager().authorizeInstanceProperty()));

		password.promptTextProperty().bind(Bindings.createStringBinding(() ->
		{
			String id = Authorize.getID(ARML.core().getAuthManager().getAuthorizeInstance());
			return resources.getString(id + ".password");
		}, ARML.core().getAuthManager().authorizeInstanceProperty()));
	}

	public void login(ActionEvent event)
	{
		btnPane.getChildren().remove(login);
		btnPane.getChildren().add(spinner);
//		onlineMode.setDisable(true);
		ARML.async().submit(Tasks.builder(() ->
		{
			AuthManager module = ARML.core().getAuthManager();
			return module.getAuthorizeInstance().auth(module.getAccount(), module.getPassword());
		}).setDone((result) -> Platform.runLater(() ->
		{
			ARML.core().getAuthManager().setCache(result);
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
}
