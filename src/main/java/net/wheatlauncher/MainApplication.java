package net.wheatlauncher;

import io.datafx.controller.context.ApplicationContext;
import javafx.application.Application;
import javafx.stage.Stage;
import net.launcher.Bootstrap;
import net.launcher.Logger;
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
		WindowsManager windowsManager = new WindowsManager();
		ApplicationContext.getInstance().register(windowsManager);
		try {Bootstrap.boost(Core.class);}
		catch (Exception e) {windowsManager.addSuppressedException(e);}
		Thread.currentThread().setUncaughtExceptionHandler((t, e) ->
				windowsManager.addSuppressedException((Exception) e));
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
