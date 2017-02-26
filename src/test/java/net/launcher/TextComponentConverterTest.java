package net.launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import net.launcher.game.ServerInfoBase;
import net.launcher.game.ServerStatus;
import net.launcher.services.MinecraftServerPingService;
import net.launcher.services.MinecraftServerPingServiceBuilder;

/**
 * @author ci010
 */
public class TextComponentConverterTest extends Application
{
	public static void main(String[] arg)
	{
		launch(arg);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		StackPane pane = new StackPane();

		MinecraftServerPingService minecraftServerPingService = MinecraftServerPingServiceBuilder.buildDefault();
		ServerStatus test = minecraftServerPingService.fetchInfoAndWaitPing(new ServerInfoBase("test", "crafter.me"), null).get();

		minecraftServerPingService.shutdown();
		System.out.println(test.getServerMOTD());
		TextFlow convert = TextComponentConverter.convert(test.getServerMOTD());

		pane.getChildren().addAll(convert);
		Scene scene = new Scene(pane, 500, 500);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
