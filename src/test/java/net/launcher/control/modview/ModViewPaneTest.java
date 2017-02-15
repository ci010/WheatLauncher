package net.launcher.control.modview;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.launcher.services.curseforge.CurseForgeProject;
import net.launcher.services.curseforge.CurseForgeProjectType;
import net.launcher.services.curseforge.CurseForgeService;
import net.launcher.services.curseforge.CurseForgeServices;
import net.wheatlauncher.MainApplication;

/**
 * @author ci010
 */
public class ModViewPaneTest extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		CurseProjectViewPane pane = new CurseProjectViewPane();
		CurseForgeService curseForgeService = CurseForgeServices.newService(CurseForgeProjectType.Mods);
		CurseForgeService.Cache<CurseForgeProject> view = curseForgeService.view(null);
		pane.setProjectCache(view);
		pane.getScrollControl().requestLayout();

		Scene scene = new Scene(pane, 800, 500);//old 512 380  542, 380
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/common.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
