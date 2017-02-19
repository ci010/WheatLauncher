package net.launcher.resourcepack;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.fontawesome.Icon;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.launcher.control.ResourcePackCell;
import net.launcher.game.ResourcePack;
import net.launcher.utils.resource.ArchiveRepository;
import net.wheatlauncher.MainApplication;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ci010
 */
public class ResourcePackManagerTest extends Application
{
	@Test
	public void testImport() throws ExecutionException, InterruptedException, IOException
	{
		Path resolve = Paths.get("").resolve("test").resolve("resMan");
		Files.createDirectories(resolve);
		ResourcePackMangerBuilder test = ResourcePackMangerBuilder.create(resolve, Executors.newCachedThreadPool());
		ResourcePackManager build = test.build();
	}

	public static void main(String[] args) {launch(args);}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Path resolve = Paths.get("").resolve("test").resolve("resMan");
		Files.createDirectories(resolve);
		ExecutorService service = Executors.newCachedThreadPool();
		ResourcePackMangerBuilder test = ResourcePackMangerBuilder.create(resolve, service);
		ResourcePackManager build = test.build();

		ArchiveRepository<ResourcePack> repo = test.getArchiveRepository();
		repo.update().get();
		String next = repo.getAllVisiblePaths().iterator().next();
		ArchiveRepository.Resource<ResourcePack> resourcePackResource = repo.getResourceMap().get(next);
		ResourcePack containData = resourcePackResource.getContainData();
		System.out.println(containData);
		VBox container = new VBox();
		container.setSpacing(10);

		for (int i = 0; i < 5; i++)
		{
			ResourcePackCell ce = new ResourcePackCell(null, null);
			ce.setValue(containData);
			ce.setImage(build.getIcon(containData));
			container.getChildren().add(ce);
		}
		StackPane root = new StackPane(container);
		Scene scene = new Scene(root, 512, 380);
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/common.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(event -> service.shutdown());
	}

	private StackPane createPane(ResourcePack resourcePack, Image icon, boolean left)
	{
		StackPane root = new StackPane();
		root.setPadding(new Insets(10));
		StackPane imgContainer = new StackPane();
		imgContainer.setMaxSize(64, 64);
		ImageView view = new ImageView(icon);
		view.setFitHeight(64);
		view.setFitWidth(64);
		HBox btnOverlay = createBtnOverlay(left);
		btnOverlay.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0.4),
				CornerRadii.EMPTY, Insets.EMPTY)));
		btnOverlay.visibleProperty().bind(Bindings.createBooleanBinding(imgContainer::isHover, imgContainer.hoverProperty()));
		btnOverlay.setAlignment(Pos.CENTER);
		imgContainer.getChildren().addAll(view, btnOverlay);
		HBox box = new HBox();
		box.setSpacing(10);

		VBox content = new VBox(new Label(resourcePack.getPackName()), new Label(resourcePack.getDescription()));
		content.setPadding(new Insets(10));
		box.getChildren().addAll(imgContainer, content);
		root.getChildren().addAll(box);
		return root;
	}

	private HBox createBtnOverlay(boolean left)
	{
		HBox btnRoot = new HBox();

		JFXButton choose = new JFXButton();
		Icon icon;
		if (left) icon = new Icon("CARET_RIGHT");
		else icon = new Icon("CARET_LEFT");
		icon.setTextFill(Color.GRAY);
		choose.setGraphic(icon);

		VBox moveBtnPanel = new VBox();
		JFXButton moveUp = new JFXButton(), moveDown = new JFXButton();
		icon = new Icon("CARET_UP");
		icon.setTextFill(Color.GRAY);
		moveUp.setGraphic(icon);
		icon = new Icon("CARET_DOWN");
		icon.setTextFill(Color.GRAY);
		moveDown.setGraphic(icon);

		moveBtnPanel.getChildren().add(moveUp);
		moveBtnPanel.getChildren().add(moveDown);

		if (left)
		{
			btnRoot.getChildren().add(moveBtnPanel);
			btnRoot.getChildren().add(choose);
		}
		else
		{
			btnRoot.getChildren().add(choose);
			btnRoot.getChildren().add(moveBtnPanel);
		}
		return btnRoot;
	}
}
