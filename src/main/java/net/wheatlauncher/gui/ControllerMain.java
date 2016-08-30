package net.wheatlauncher.gui;

import com.jfoenix.controls.JFXButton;
import io.datafx.controller.FXMLController;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

/**
 * @author ci010
 */
@FXMLController(value = "/fxml/Main.fxml", title = "Simple Launcher")
public class ControllerMain
{
	public static ControllerMain getController()
	{
		return instance;
	}

	private static ControllerMain instance;

	@FXMLViewFlowContext
	private ViewFlowContext flowContext;

	@FXML
	private Pane top;
	@FXML
	private Pane root;

	@FXML
	private StackPane content;

	@FXML
	private JFXButton close;

	public ViewFlowContext getFlowContext()
	{
		return flowContext;
	}

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

	private String current = "Launch";
	private long lastSwap = System.currentTimeMillis();

	private String switchToNext()
	{
		if (current.equals("Launch"))
			return current = "Setting";
		else
			return current = "Launch";
	}

	private PageSwitcher switcher;

	@PostConstruct
	public void init() throws FlowException, FxmlLoadException
	{
		Flow inner = new Flow(ControllerLogin.class);
		FlowHandler handler = inner.createHandler(flowContext);
		switcher = new PageSwitcher(handler);
		flowContext.register("ContentFlowHandler", handler);
		flowContext.register("ContentFlow", inner);
		flowContext.register(switcher);
		root.getChildren().add(0, handler.start(new AnimatedFlowContainer()));

		switcher.register("login", (ViewContext<? extends ReloadableController>) handler.getCurrentView().getViewContext());
		switcher.register("preview", ControllerPreview.class);

//		loginViewContext = (ViewContext<? extends ReloadableController>) handler.getCurrentView().getViewContext();

//		settingContext = ViewFactory.getInstance().createByController(
//				ControllerSetting.class, null,
//				handler.getViewConfiguration(), handler.getFlowContext());
//		previewContext = ViewFactory.getInstance().createByController(ControllerPreview.class, null,
//				handler.getViewConfiguration(), handler.getFlowContext());

//		root.addEventHandler(ScrollEvent.SCROLL, event -> {
//			long mill = System.currentTimeMillis();
//			if (mill - lastSwap > 100)
//			{
//				if (event.getDeltaY() != 0)
//					try
//					{
//						FlowHandler handler1 = (FlowHandler) flowContext.getRegisteredObject("ContentFlowHandler");
//						String s = switchToNext();
//						if (s.equals("Launch"))
//						{
//							loginViewContext.getController().reload();
//							handler1.setNewView(new FlowView(loginViewContext), false);
//						}
//						else
//						{
//							settingContext.getController().reload();
//							handler1.setNewView(new FlowView(settingContext), false);
//						}
//					}
//					catch (FlowException e)
//					{
//						e.printStackTrace();
//					}
//			}
//			lastSwap = mill;
//		});

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
