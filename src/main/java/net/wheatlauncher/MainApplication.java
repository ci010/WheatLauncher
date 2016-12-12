package net.wheatlauncher;

import io.datafx.controller.context.ApplicationContext;
import javafx.application.Application;
import javafx.stage.Stage;
import net.launcher.Bootstrap;
import net.launcher.utils.Logger;
import net.wheatlauncher.control.ControllerMain;
import net.wheatlauncher.control.utils.WindowsManager;
import net.wheatlauncher.utils.LanguageMap;

/**
 * @author ci010
 */
public class MainApplication extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception
	{
		Bootstrap.boost(Core.class);
		WindowsManager windowsManager = new WindowsManager();
		ApplicationContext.getInstance().register(windowsManager);
		windowsManager.createPage(stage, ControllerMain.class, 800, 550);
		stage.show();
	}

	@Override
	public void stop() throws Exception
	{
		Bootstrap.destroy();
		LanguageMap.INSTANCE.logLostKey(System.out);
		Logger.trace("stop");
	}
}
