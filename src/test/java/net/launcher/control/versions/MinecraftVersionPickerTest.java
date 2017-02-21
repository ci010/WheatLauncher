package net.launcher.control.versions;

import javafx.application.Application;
import javafx.stage.Stage;
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


//		minecraftVersionPicker.setRequestUpdate(e ->
//		{
//			downloader.fetchRemoteVersionList(Tasks.adept(e));
//			return null;
//		});
		primaryStage.show();
	}
}
