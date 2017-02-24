package net.wheatlauncher;

import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.launcher.FXEventBus;
import net.launcher.LaunchCore;
import net.launcher.api.ARML;
import net.launcher.control.DefaultTransitions;
import net.launcher.control.SceneTransitionHandler;
import net.launcher.utils.NIOUtils;
import net.wheatlauncher.control.utils.FinalFieldSetter;
import org.to2mbn.jmccc.util.Platform;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author ci010
 */
public class MainApplication extends Application
{
	private LaunchCore core;
	private Path root;

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
		root = loadLocation();
		if (!Files.exists(root)) Files.createDirectories(root);
		ARML instance = ARML.instance();
		for (Field field : instance.getClass().getDeclaredFields())
		{
			if (field.getName().equals("bus"))
				FinalFieldSetter.INSTANCE.set(instance, field, new FXEventBus());
			else if (field.getName().equals("context"))
				FinalFieldSetter.INSTANCE.set(instance, field, core = new Core());
			else if (field.getName().equals("logger"))
				FinalFieldSetter.INSTANCE.set(instance, field, createLogger());
			else if (field.getName().equals("scheduledExecutorService"))
				FinalFieldSetter.INSTANCE.set(instance, field, Executors.newScheduledThreadPool(4));
		}
		//TODO load plugins and collect resource bundle
		ResourceBundle lang;
		try {lang = ResourceBundle.getBundle("assets.lang.lang", Locale.getDefault());}
		catch (Exception e) {lang = ResourceBundle.getBundle("assets.lang.lang", Locale.ENGLISH);}
		bundle = lang;
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

	private Logger createLogger() throws IOException
	{
		Logger logger = Logger.getLogger("ARML");
		Files.createDirectories(root.resolve("logs"));
		FileHandler logs = new FileHandler(root.resolve("logs").resolve("main.log").toAbsolutePath().toString());
		logs.setFormatter(new SimpleFormatter());
		logger.addHandler(logs);
		return logger;
	}

	@Override
	public void start(final Stage stage) throws Exception
	{
		preinit();

		core.init(root);

		FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/fxml/Main.fxml"), bundle);
		StackPane root = fxmlLoader.load();
		fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/fxml/Login.fxml"), bundle);
		loginPage = fxmlLoader.load();
		fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/assets/fxml/Preview.fxml"), bundle);
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
		core.destroy();
		ARML.logger().info("destroy");
	}
}
