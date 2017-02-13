package requester;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.launcher.services.curseforge.CurseForgeProject;
import net.launcher.services.curseforge.CurseForgeProjectType;
import net.launcher.services.curseforge.CurseForgeService;
import net.launcher.services.curseforge.CurseForgeServices;

/**
 * @author ci010
 */
public class TestImage extends Application
{
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		StackPane root = new StackPane();

		ImageView imageView = new ImageView();
		StackPane container = new StackPane(imageView);
		container.setPrefWidth(64);
		container.setPrefHeight(64);
		container.setMaxWidth(64);
		container.setMaxHeight(64);
		root.setStyle("-fx-background-color:RED;");
		container.setStyle("-fx-background-color:WHITE;");
		CurseForgeService service = CurseForgeServices.newService(CurseForgeProjectType.Mods);

		CurseForgeService.Cache<CurseForgeProject> filter = service.view(null);
		CurseForgeProject curseForgeProject = filter.getCache().get(0);
		String imageUrl = curseForgeProject.getImageUrl();
		System.out.println(imageUrl);
//		URL url = new URL(imageUrl);

		Image image = new Image(imageUrl, false);
		imageView.setImage(image);
//		imageView.setSmooth(true);
//		double scale = 200D / image.getWidth();
//		imageView.setFitWidth(200);
//		System.out.println(scale);
//		imageView.setFitHeight(image.getHeight() * scale);

		root.getChildren().setAll(container);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
