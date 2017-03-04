import javafx.application.Application;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author ci010
 */
public class TestWeb extends Application
{

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		WebEngine engine = new WebEngine();
		engine.load("https://minecraft.curseforge.com/texture-packs");
		engine.setUserDataDirectory(new File("D:\\Storage\\Desktop\\Data"));
		primaryStage.show();
	}
}
