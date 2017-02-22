package net.wheatlauncher;

import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import net.launcher.LaunchCore;
import net.launcher.control.DefaultTransitions;
import net.launcher.control.SceneTransitionHandler;
import net.launcher.utils.NIOUtils;
import org.to2mbn.jmccc.util.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author ci010
 */
public class MainApplication extends Application
{
	private static LaunchCore current;
	private static Logger logger;

	private ReentrantLock lock = new ReentrantLock();
	private Path repositoryLocation;

	public static void main(String[] args)
	{
		launch(args);
	}

	private static Path loadLocation() throws IOException
	{
		Path root;
		switch (Platform.CURRENT)
		{
			case WINDOWS:
				String appdata = System.getenv("APPDATA");
				root = Paths.get(appdata == null ? System.getProperty("user.home", ".") : appdata);
				break;
			case LINUX:
				root = Paths.get(System.getProperty("user.home", "."));
				break;
			case OSX:
				root = Paths.get("Library/Application Support/");
				break;
			default:
				root = Paths.get(System.getProperty("user.home", ".") + "/");
		}
		Path redirect = root.resolve("arml.loc");
		if (Files.exists(redirect))
		{
			String s = NIOUtils.readToString(redirect);
			try {return Paths.get(s);}
			catch (Exception e) {return root.resolve("arml");}
		}
		return root.resolve(".launcher");
	}

	private void preinit() throws Exception
	{
		repositoryLocation = loadLocation();
		logger = Logger.getLogger("ARML");
		Files.createDirectories(repositoryLocation.resolve("logs"));
		FileHandler logs = new FileHandler(repositoryLocation.resolve("logs").resolve("main.log").toAbsolutePath().toString());
		logs.setFormatter(new SimpleFormatter());
		logger.addHandler(logs);
	}

	private void boost(Class<? extends LaunchCore> clz, Stage stage) throws Exception
	{
		lock.lock();
		if (current != null)
		{
			lock.unlock();
			throw new IllegalStateException();
		}
		LaunchCore launchCore = clz.newInstance();
		current = launchCore;
		launchCore.init(repositoryLocation, stage);
		lock.unlock();
	}

	private void destroy() throws Exception
	{
		lock.lock();
		if (current == null)
			throw new IllegalStateException();
		current.destroy();
		current = null;
		lock.unlock();
	}

	public static LaunchCore getCore()
	{
		Objects.requireNonNull(current);
		return current;
	}

	private SceneTransitionHandler handler;
	private double xOffset = 0;
	private double yOffset = 0;
	private Pane loginPage;
	private Pane previewPage;

	private static ResourceBundle bundle;

	public static ResourceBundle getLanguageBundle()
	{
		return bundle;
	}

	public static Logger getLogger()
	{
		return logger;
	}

	public static void reportError(Scene scene, String message)
	{
		javafx.application.Platform.runLater(() -> displayError(scene, message));
	}

	public static void reportError(Scene scene, Throwable message)
	{
		javafx.application.Platform.runLater(() -> displayError(scene, message));
	}

	public static void displayError(Scene scene, String message)
	{
		new JFXSnackbar((Pane) scene.getRoot()).enqueue(new JFXSnackbar.SnackbarEvent(message));
	}

	public static void displayError(Scene scene, Throwable message)
	{
		new JFXSnackbar((Pane) scene.getRoot()).enqueue(new JFXSnackbar.SnackbarEvent(message.getMessage()));
	}

	@Override
	public void start(final Stage stage) throws Exception
	{
		preinit();
		Exception suppressed;
		try {boost(Core.class, stage);}
		catch (Exception e) {}//TODO well... i forgot that the throwing exception will break the flow... maybe get
		// rid of this design....

		ResourceBundle lang;
		try {lang = ResourceBundle.getBundle("assets.lang.lang", Locale.getDefault());}
		catch (Exception e) {lang = ResourceBundle.getBundle("assets.lang.lang", Locale.ENGLISH);}
		bundle = lang;
		Callback<Class<?>, Object> controllerFactory = param ->
		{
			try
			{
				logger.info("Instantiate " + param.getSimpleName());
				return param.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return null;
		};
		FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/fxml/Main.fxml"), lang
				, null, controllerFactory);
		StackPane root = fxmlLoader.load();
		fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/fxml/Login.fxml"), lang, null, controllerFactory);
		loginPage = fxmlLoader.load();
		fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/fxml/Preview.fxml"), lang, null, controllerFactory);
		previewPage = fxmlLoader.load();

		StackPane base = new StackPane(loginPage);
		root.getChildren().add(0, base);
		handler = new SceneTransitionHandler(base, DefaultTransitions.FADE);

		stage.setFullScreen(false);
		if (stage.getStyle() != StageStyle.TRANSPARENT)
			stage.initStyle(StageStyle.TRANSPARENT);
		root.setOnMousePressed(event ->
		{
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});
		root.setOnMouseDragged(event ->
		{
			stage.setX(event.getScreenX() - xOffset);
			stage.setY(event.getScreenY() - yOffset);
		});
		Scene scene = new Scene(root, 800, 550);
		scene.setUserData((Consumer<Object>) o -> switchTo(o.toString()));
		scene.getStylesheets().add(MainApplication.class.getResource("/assets/css/common.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

	private void switchTo(String id)
	{
		switch (id)
		{
			case "PREVIEW":
				handler.transition(previewPage);
				break;
			case "LOGIN":
				handler.transition(loginPage);
				break;
		}
	}

	@Override
	public void stop() throws Exception
	{
		destroy();
		logger.info("stop");
	}
}
