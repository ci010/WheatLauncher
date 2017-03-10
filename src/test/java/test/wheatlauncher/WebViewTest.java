package test.wheatlauncher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.wheatlauncher.MainApplication;

/**
 * @author ci010
 */
public class WebViewTest extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		WebView browser = new WebView();
		WebEngine webEngine = browser.getEngine();
		webEngine.load("https://minecraft.curseforge.com/projects/");
		webEngine.setConfirmHandler(param ->
		{
			System.out.println(param);
			return true;
		});
		final Scene scene = new Scene(browser, 1000, 600);
		scene.getStylesheets().add(MainApplication.class.getResource("/assets/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/assets/css/common.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}
}
