package net.launcher.control;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.launcher.control.profile.base.ProfileSelector;
import net.launcher.control.profile.base.ProfileTableSelector;
import net.launcher.profile.LaunchProfile;
import net.wheatlauncher.MainApplication;

/**
 * @author ci010
 */
public class ProfileTest extends Application
{
	public static void main(String[] ar)
	{
		launch(ar);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		StackPane pane = new StackPane();
		LaunchProfile a = new LaunchProfile(), b = new LaunchProfile();
		a.setDisplayName("Default");
		b.setDisplayName("1.7.10");

		ProfileSelector profileSelector = new ProfileTableSelector();
		profileSelector.profilesProperty().addAll(a, b);
		pane.getChildren().addAll(profileSelector);

		Scene scene = new Scene(pane, 800, 500);//old 512 380  542, 380
		scene.getStylesheets().add(MainApplication.class.getResource("/assets/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/assets/css/common.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
