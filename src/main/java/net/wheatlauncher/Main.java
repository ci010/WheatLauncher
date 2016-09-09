package net.wheatlauncher;

import io.datafx.controller.ViewConfiguration;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.wheatlauncher.gui.ControllerMain;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author ci010
 */
public class Main extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	private double xOffset = 0;
	private double yOffset = 0;

	@Override
	public void start(final Stage stage) throws Exception
	{
		ViewConfiguration viewConfiguration = new ViewConfiguration();
		ResourceBundle lang;
		try
		{
			lang = ResourceBundle.getBundle("lang", Locale.getDefault());
		}
		catch (Exception e)
		{
			lang = ResourceBundle.getBundle("lang", Locale.ENGLISH);
		}
		viewConfiguration.setResources(lang);
//		viewConfiguration.setBuilderFactory(new LayerStack());
		Flow flow = new Flow(ControllerMain.class, viewConfiguration);

		stage.setFullScreen(false);
		DefaultFlowContainer container = new DefaultFlowContainer();
		flowContext = new ViewFlowContext();
		flowContext.register(stage);
		flowContext.register(viewConfiguration);
		FlowHandler flowHandler = flow.createHandler(flowContext);
		flowHandler.start(container);

		final Scene scene = new Scene(container.getView(), 512, 380);
		stage.initStyle(StageStyle.TRANSPARENT);
		scene.getStylesheets().add(Main.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(Main.class.getResource("/css/common.css").toExternalForm());
		container.getView().setOnMousePressed(event -> {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});
		container.getView().setOnMouseDragged(event -> {
			stage.setX(event.getScreenX() - xOffset);
			stage.setY(event.getScreenY() - yOffset);
		});
		stage.setScene(scene);
		stage.show();

		Core.INSTANCE.onInit();
	}

}
