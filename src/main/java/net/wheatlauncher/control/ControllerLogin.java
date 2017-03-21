package net.wheatlauncher.control;

import api.launcher.Shell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
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
import net.launcher.control.OnlineModeSwitch;
import net.wheatlauncher.control.utils.ValidatorDelegate;

import java.util.List;
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
//		getItems().addAll(ARML.core().getAuthManager().getAuthorizeMap().keySet());
//		getSelectionModel().select(0);
//		getSelectionModel().selectedItemProperty().addListener(observable ->
//		{
//			isOffline.set(false);
//			ARML.core().getAuthManager().setAuthorize(getValue());
//		});
//		isOffline.addListener(observable ->
//		{
//			boolean offline = isOffline();
//			if (offline) ARML.core().getAuthManager().setNoAuthorize();
//			else ARML.core().getAuthManager().setAuthorize(this.getValue());
//		});
		InvalidationListener listener = observable ->
		{
			String account;
			List<String> historyList = Shell.instance().getAuthorizeProxy().getAccountHistory();
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
		account.setText(Shell.instance().getAuthorizeProxy().getAccount());
		account.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (account.getText().length() == 0)
				accountMenu.hide();
			else
				accountMenu.getItems().setAll(Shell.instance().getAuthorizeProxy().getAccountHistory()
						.stream().filter(s -> s.startsWith(newValue))
						.map(this::createItem).collect(Collectors.toList()));
		});

		accountMenu.getItems().addListener((InvalidationListener) observable ->
		{
			if (accountMenu.getItems().isEmpty()) accountMenu.hide();
			else if (!accountMenu.isShowing()) accountMenu.show(account, Side.BOTTOM, 0, 0);
		});

		accountValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						Shell.instance().getAuthorizeProxy()::setAccount,
				Shell.instance().getAuthorizeProxy().idProperty()));
		passwordValid.delegateProperty().bind(Bindings.createObjectBinding(() ->
						Shell.instance().getAuthorizeProxy()::updatePassword,
				Shell.instance().getAuthorizeProxy().idProperty()));
		account.textProperty().addListener(observable -> account.validate());
		password.textProperty().addListener(observable -> password.validate());

		login.disableProperty().bind(Bindings.createBooleanBinding(() ->
						accountValid.hasErrorsProperty().get() ||
								(passwordValid.hasErrorsProperty().get() && !password.isDisable()),
				account.textProperty(), password.textProperty(), password.disableProperty()));
		password.disableProperty().bind(Bindings.createBooleanBinding(() ->
						Shell.instance().getAuthorizeProxy().getId().equals("offline"),
				Shell.instance().getAuthorizeProxy().idProperty()));

		account.promptTextProperty().bind(Bindings.createStringBinding(() ->
						resources.getString(Shell.instance().getAuthorizeProxy().getId() + ".account"),
				Shell.instance().getAuthorizeProxy().idProperty()));
		password.promptTextProperty().bind(Bindings.createStringBinding(() ->
						resources.getString(Shell.instance().getAuthorizeProxy().getId() + ".password"),
				Shell.instance().getAuthorizeProxy().idProperty()));
	}

	public void login()
	{
		btnPane.getChildren().remove(login);
		btnPane.getChildren().add(spinner);
		Task<?> task = Shell.instance().buildTask("login", account.getText(), password.getText());
//		Task<AuthInfo> task = ARML.taskCenter().runTask(new Task<AuthInfo>()
//		{
//			{updateTitle("Login");}
//
//			@Override
//			protected AuthInfo call() throws Exception
//			{
//				try
//				{
//					AuthManager module = ARML.core().getAuthManager();
//					return module.getAuthorizeInstance().auth(module.getAccount(), module.getPassword());
//				}
//				catch (Exception e)
//				{
//					Throwable ex = e.getCause() == null ? e : e.getCause();
//					if (ex instanceof RemoteAuthenticationException)
//						if (ex.getMessage().equals("ForbiddenOperationException: Invalid credentials. Invalid username or password."))
//							throw new AuthenticationException("login.invalid.credentials");
//						else throw e;
//					else if (ex.getCause() instanceof UnknownHostException)
//						throw new UnknownHostException("login.network.error");
//					else throw e;
//				}
//			}
//		});
		task.setOnSucceeded(e ->
		{
//			AuthInfo value = (AuthInfo) e.getSource().getValue();
//			ARML.core().getAuthManager().setCache(value);
			btnPane.getChildren().remove(spinner);
			btnPane.getChildren().add(login);
			((Consumer) root.getScene().getUserData()).accept("PREVIEW");
		});
		task.setOnFailed(e ->
		{
			btnPane.getChildren().remove(spinner);
			btnPane.getChildren().add(login);
		});
		Shell.instance().execute(task);
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
