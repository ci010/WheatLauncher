package net.launcher.control.versions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.wheatlauncher.MainApplication;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;

/**
 * @author ci010
 */
public class MinecraftVersionPickerTest extends Application
{

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		MinecraftDownloader downloader = MinecraftDownloaderBuilder.buildDefault();

		MinecraftVersionPicker minecraftVersionPicker = new MinecraftVersionPicker();
		StackPane main = new StackPane(minecraftVersionPicker);

//		minecraftVersionPicker.setRequestUpdate(e ->
//		{
//			downloader.fetchRemoteVersionList(Tasks.adept(e));
//			return null;
//		});
		Scene scene = new Scene(main);
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-components.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/common.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
