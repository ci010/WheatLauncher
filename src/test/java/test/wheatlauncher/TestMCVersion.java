package test.wheatlauncher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.launcher.control.versions.MinecraftVersionPicker;

/**
 * @author ci010
 */
public class TestMCVersion extends Application
{
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		MinecraftVersionPicker minecraftVersionPicker = new MinecraftVersionPicker();
		StackPane main = new StackPane(minecraftVersionPicker);
		Scene scene = new Scene(main);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
