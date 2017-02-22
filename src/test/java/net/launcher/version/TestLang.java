package net.launcher.version;

import javafx.application.Application;
import javafx.stage.Stage;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Asset;
import org.to2mbn.jmccc.version.parsing.Versions;

import java.io.File;
import java.util.Set;

/**
 * @author ci010
 */
public class TestLang extends Application
{
	public static void main(String[] args) {launch(args);}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
//		ScrollPane root = new ScrollPane();

		File file = new File("D:\\Storage\\Desktop\\testminecraft");
		MinecraftDirectory directory = new MinecraftDirectory(file);
		Set<Asset> assets = Versions.resolveAssets(directory, "1.7.10");
		assets.stream().filter(asset -> asset.getVirtualPath().endsWith(".lang"))
				.filter(asset -> asset.getVirtualPath().startsWith("minecraft/lang")).forEach(System.out::println);
//		Scene scene = new Scene(root, 512, 380);
//		scene.getStylesheets().add(MainApplication.class.getResource("/assets.css/jfoenix-main-demo.assets.css").toExternalForm());
//		scene.getStylesheets().add(MainApplication.class.getResource("/assets.css/common.assets.css").toExternalForm());
//		primaryStage.setScene(scene);
//		primaryStage.show();
	}
}
