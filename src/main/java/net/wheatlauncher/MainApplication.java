package net.wheatlauncher;

import api.launcher.ARML;
import api.launcher.event.LauncherInitEvent;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.launcher.*;
import net.launcher.control.DefaultTransitions;
import net.launcher.control.SceneTransitionHandler;
import net.launcher.utils.NIOUtils;
import net.wheatlauncher.control.utils.FinalFieldSetter;
import org.to2mbn.jmccc.util.Platform;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author ci010
 */
public class MainApplication extends Application
{
	private LaunchCore core;
	private PluginLoader loader;
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

		Map<String, Object> langMap = new HashMap<>();

		ResourceBundle lang;
		try {lang = ResourceBundle.getBundle("assets.lang.lang", Locale.getDefault());}
		catch (Exception e) {lang = ResourceBundle.getBundle("assets.lang.lang", Locale.ENGLISH);}

		collectBundle(lang, langMap);

		loader = new PluginLoader(root);
		loader.reload();
		for (PluginContainer pluginContainer : loader.getContainers())
			collectBundle(pluginContainer.getBundle(), langMap);

		bundle = new MapResourceBundle(langMap);

//		for (PluginContainer container : loader.getContainers())
//			container.getPlugin().onLoad(ARML.instance());
	}

	private void collectBundle(ResourceBundle bundle, Map<String, Object> map)
	{
		for (String s : bundle.keySet()) map.put(s, bundle.getString(s));
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
		Formatter formatter = new Formatter()
		{
			String simpleFormat = "[%1$tl:%1$tM:%1$tS %1$Tp] [%2$s] [%4$s]: %5$s%6$s%n";
			private final Date dat = new Date();

			@Override
			public synchronized String format(LogRecord record)
			{
				dat.setTime(record.getMillis());
				String source;
				if (record.getSourceClassName() != null)
				{
					source = record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf('.') + 1);
					if (record.getSourceMethodName() != null)
						source += "::" + record.getSourceMethodName();
				}
				else
					source = record.getLoggerName();
				String message = formatMessage(record);
				String throwable = "";
				if (record.getThrown() != null)
				{
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					pw.println();
					record.getThrown().printStackTrace(pw);
					pw.close();
					throwable = sw.toString();
				}
				return String.format(simpleFormat,
						dat,
						source,
						record.getLoggerName(),
						record.getLevel().getLocalizedName(),
						message,
						throwable);
			}
		};
		logs.setFormatter(formatter);
		logger.addHandler(logs);

		return logger;
	}

	@Override
	public void start(final Stage stage) throws Exception
	{
		preinit();

		core.init(root);

		for (PluginContainer container : loader.getContainers())
			container.getPlugin().onLoad(ARML.instance());

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
		if (stage.getStyle() != StageStyle.TRANSPARENT) stage.initStyle(StageStyle.TRANSPARENT);
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

		ARML.bus().postEvent(new LauncherInitEvent.Post(loginPage, previewPage));

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
		ARML.async().shutdown();
		ARML.logger().info("destroy");
	}
}
