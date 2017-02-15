package test.wheatlauncher;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.wheatlauncher.MainApplication;

/**
 * @author ci010
 */
public class JFXDrawerTest extends Application
{
	public static void main(String[] args) {launch(args);}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		StackPane root = new StackPane();

		JFXButton button = new JFXButton("TEST");
		root.getChildren().add(button);

		StackPane content = new StackPane(new Label("GGGGG"));
		JFXDrawer drawer = new JFXDrawer();
		StackPane drawerPane = new StackPane();
		drawerPane.getStyleClass().add("blue-400");
		drawerPane.getChildren().add(new Label("GGGGG"));
		drawer.setDirection(JFXDrawer.DrawerDirection.TOP);
		drawer.setDefaultDrawerSize(150);
		drawer.setSidePane(drawerPane);
		drawer.setOverLayVisible(false);
		drawer.setResizableOnDrag(true);

		JFXDrawersStack stack = new JFXDrawersStack();
		stack.setContent(root);
		drawer.setId("RIGHT");

		button.setOnAction(e -> stack.toggle(drawer));

		final Scene scene = new Scene(stack, 512, 380);
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/common.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
