package test.wheatlauncher;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.wheatlauncher.MainApplication;

/**
 * @author ci010
 */
public class TestGUI extends Application
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
		Flow flow = new Flow(ControllerTest.class);

		stage.setFullScreen(false);
		DefaultFlowContainer container = new DefaultFlowContainer();
		flowContext = new ViewFlowContext();
		flowContext.register("Stage", stage);
		flowContext.register("Root", container.getView());
		FlowHandler flowHandler = flow.createHandler(flowContext);
		flowContext.register("FlowHandler", flowHandler);
		flowHandler.start(container);

		final Scene scene = new Scene(container.getView(), 512, 380);
		stage.initStyle(StageStyle.TRANSPARENT);
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/common.css").toExternalForm());
		container.getView().setOnMousePressed(event ->
		{
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});
		container.getView().setOnMouseDragged(event ->
		{
			stage.setX(event.getScreenX() - xOffset);
			stage.setY(event.getScreenY() - yOffset);
		});
		stage.setScene(scene);
		stage.show();
	}

}
