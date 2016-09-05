package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.FXMLController;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.ViewConfiguration;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;

/**
 * @author ci010
 */
@FXMLController(value = "/fxml/Main.fxml", title = "Simple Launcher")
public class ControllerMain
{
	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private Pane root;

	@FXML
	private JFXButton close;

	private Function<AnimatedFlowContainer, List<KeyFrame>> function = c -> null;
//	private Function<PortAnimatedFlowContainer, List<KeyFrame>> animationProducer = new Function<PortAnimatedFlowContainer, List<KeyFrame>>()
//	{
//		@Override
//		public List<KeyFrame> apply(PortAnimatedFlowContainer c)
//		{
//			KeyFrame[] keyFrames = {
//					new KeyFrame(Duration.ZERO,
//							new KeyValue(c.getView().translateYProperty(), c.getView().getHeight(), Interpolator.LINEAR),
//							new KeyValue(c.getPlaceholder().translateYProperty(), -c.getView().getHeight(), Interpolator.LINEAR)),
//					new KeyFrame(c.getDuration(),
//							new KeyValue(c.getView().translateYProperty(), 0, Interpolator.LINEAR),
//							new KeyValue(c.getPlaceholder().translateYProperty(), -c.getView().getHeight(), Interpolator
//									.LINEAR))};
//			return Arrays.asList(keyFrames);
//		}
//	};

	private long lastSwap = System.currentTimeMillis();

	@PostConstruct
	public void init() throws FlowException, FxmlLoadException
	{
		Flow inner = new Flow(ControllerLogin.class, flowContext.getRegisteredObject(ViewConfiguration.class));
		FlowHandler handler = inner.createHandler(flowContext);
		PageSwitcher switcher = new PageSwitcher(handler);
		flowContext.register(switcher);
		flowContext.register(root);
		root.getChildren().add(0, handler.start(new AnimatedFlowContainer()));

		switcher.register("login", (ViewContext<? extends ReloadableController>) handler.getCurrentView().getViewContext());
		switcher.register("preview", ControllerPreview.class);

		close.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				Platform.exit();
		});
	}


	@FXML
	protected void onClick(MouseEvent event)
	{
		if (event.getButton() == MouseButton.PRIMARY)
			root.requestFocus();
	}
}
