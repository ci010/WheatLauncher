package net.wheatlauncher.control;

import com.jfoenix.controls.JFXSnackbar;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.ViewConfiguration;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.*;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.wheatlauncher.MainApplication;
import net.wheatlauncher.utils.ControlUtils;
import net.wheatlauncher.utils.LanguageMap;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * @author ci010
 */
public class WindowsManager
{
	private ViewConfiguration globalConfig;

	public WindowsManager()
	{
		this.globalConfig = new ViewConfiguration();
		initCfg();
	}

	public static class Page implements Thread.UncaughtExceptionHandler
	{
		private Map<String, ViewContext<? extends ReloadableController>> reg = new TreeMap<>();

		private FlowHandler rootHandler;
		private String current;

		private StackPane root;
		private Stage stage;
		private double xOffset = 0;
		private double yOffset = 0;


		public Page(Stage stage, FlowHandler rootHandler, StackPane parent)
		{
			this.stage = stage;
			this.rootHandler = rootHandler;
			this.root = parent;
			stage.setFullScreen(false);
			if (stage.getStyle() != StageStyle.TRANSPARENT)
				stage.initStyle(StageStyle.TRANSPARENT);
			parent.setOnMousePressed(event ->
			{
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			});
			parent.setOnMouseDragged(event ->
			{
				this.stage.setX(event.getScreenX() - xOffset);
				this.stage.setY(event.getScreenY() - yOffset);
			});
		}


		public void register(Class<? extends ReloadableController> controller) throws FxmlLoadException
		{
			ViewContext<? extends ReloadableController> byController = ViewFactoryReload.getInstance().createByController
					(controller, null, rootHandler.getViewConfiguration(), rootHandler.getFlowContext());
			ControlUtils.setupInnerController(byController.getController(), rootHandler.getFlowContext());
			reg.put(controller.getSimpleName(), byController);
		}

		public Page createSubPage(Class<? extends ReloadableController> clz, FlowContainer<StackPane> flowContainer) throws
				FlowException
		{
			Flow flow = new Flow(clz, rootHandler.getViewConfiguration());
			ViewFlowContext viewFlowContext = new ViewFlowContext();
			FlowHandler handler = new FlowHandler(flow, viewFlowContext, rootHandler.getViewConfiguration());
			Page sub = new Page(stage, handler, flowContainer.getView());
			viewFlowContext.register(flowContainer.getView());
			viewFlowContext.register(sub);
			handler.start(flowContainer);
			sub.current = clz.getSimpleName();
			sub.reg.put(clz.getSimpleName(), (ViewContext<? extends ReloadableController>) handler.getCurrentViewContext());
			return sub;
		}


		public ViewContext<?> load(Class<?> clz) throws FxmlLoadException
		{
			ViewContext<?> context = ViewFactoryReload.getInstance().createByController(clz, null,
					rootHandler.getViewConfiguration(), rootHandler.getFlowContext());
			ControlUtils.setupInnerController(context, rootHandler.getFlowContext());
			return context;
		}

		public Stage getStage()
		{
			return stage;
		}

		public Pane getRoot()
		{
			return root;
		}

		public void switchPage(Class<?> controllerClass)
		{
			try
			{
				forceSwitch(controllerClass);
			}
			catch (FxmlLoadException | FlowException e)
			{
				displayError(e);
			}
		}

		public void forceSwitch(Class<?> controller) throws FxmlLoadException, FlowException
		{
			ViewContext<? extends ReloadableController> viewContext = reg.get(controller.getSimpleName());
			if (viewContext != null)
			{
				if (current != null)
					reg.get(current).getController().unload();
				viewContext.getController().reload();
				rootHandler.setNewView(new FlowView<>(viewContext), false);
				current = controller.getSimpleName();
			}
			else displayError(new IllegalArgumentException(""));
		}

		private JFXSnackbar bar;

		private void _checkState() {if (bar == null) bar = new JFXSnackbar(this.root);}

		public synchronized void displayError(String message)
		{
			_checkState();
			bar.enqueue(new JFXSnackbar.SnackbarEvent(LanguageMap.INSTANCE.translate(message), null, 2500, null));
		}

		public void displayError(Throwable throwable)
		{
			displayError(throwable.getMessage());
			throwable.printStackTrace();
		}

		@Override
		public void uncaughtException(Thread t, Throwable e)
		{
			displayError(e);
		}
	}

	private void initCfg()
	{
		globalConfig = new ViewConfiguration();
		ResourceBundle lang;
		try
		{
			lang = ResourceBundle.getBundle("lang", Locale.getDefault());
		}
		catch (Exception e)
		{
			lang = ResourceBundle.getBundle("lang", Locale.ENGLISH);
		}
		if (lang == null)
			throw new IllegalStateException();
		else
			System.out.println("the lang is not null " + lang);
		globalConfig.setResources(lang);
	}

	public Page createPage(Stage stage, Class<? extends ReloadableController> clz, int xSize, int ySize) throws FlowException,
			FxmlLoadException
	{
		DefaultFlowContainer container = new DefaultFlowContainer();
		Scene scene = new Scene(container.getView(), xSize, ySize);//old 512 380  542, 380
		scene.getStylesheets().add(MainApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());
		scene.getStylesheets().add(MainApplication.class.getResource("/css/common.css").toExternalForm());

		Flow flow = new Flow(clz, globalConfig);
		ViewFlowContext viewFlowContext = new ViewFlowContext();
		FlowHandler handler = new FlowHandler(flow, viewFlowContext, globalConfig);
		Page page = new Page(stage, handler, container.getView());
		viewFlowContext.register(container.getView());
		viewFlowContext.register(page);
		handler.start(container);
		page.current = clz.getSimpleName();
		page.reg.put(page.current, (ViewContext<? extends ReloadableController>) handler.getCurrentViewContext());
		stage.setScene(scene);

		return page;
	}

}
