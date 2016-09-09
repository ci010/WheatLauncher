package net.wheatlauncher.gui;

import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.FlowView;
import javafx.scene.layout.Pane;
import net.wheatlauncher.utils.ControlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Such a fast utils class. Internal use...
 *
 * @author ci010
 */
public class PageManager //implements BuilderFactory
{
	private FlowHandler handler;
	private String current;
	private Map<String, ViewContext<? extends ReloadableController>> reg = new HashMap<>();

	public PageManager(FlowHandler handler)
	{
		this.handler = handler;
	}

	public String getCurrentController()
	{
		return current;
	}

	public boolean switchToQuite(String name)
	{
		ViewContext<? extends ReloadableController> viewContext = reg.get(name);
		if (viewContext != null)
		{
			try
			{
				viewContext.getController().reload();
				handler.setNewView(new FlowView<>(viewContext), false);
				current = name;
				return true;
			}
			catch (FlowException ignored) {}
		}
		return false;
	}

	public Pane getCurrentSurfaceContainer()
	{
		LayerStack layerStack = handler.getFlowContext().getRegisteredObject(LayerStack.class);
		Pane currentSurfaceLayer = layerStack.getCurrentSurfaceLayer();
		if (currentSurfaceLayer != null)
			return currentSurfaceLayer;
		return (Pane) handler.getCurrentView().getViewContext().getRootNode();
	}

	public void switchTo(String name) throws FlowException
	{
		ViewContext<? extends ReloadableController> viewContext = reg.get(name);
		if (viewContext != null)
		{
			viewContext.getController().reload();
			handler.setNewView(new FlowView<>(viewContext), false);
			current = name;
		}
	}

	public void register(String name, ViewContext<? extends ReloadableController> context)
	{
		reg.put(name, context);
	}

	public void register(String name, Class<? extends ReloadableController> controller) throws FxmlLoadException
	{
		ViewContext<? extends ReloadableController> byController = ViewFactoryReload.getInstance().createByController
				(controller, null, handler.getViewConfiguration(), handler.getFlowContext());
		ControlUtils.setupInnerController(byController.getController(), handler.getFlowContext());
		byController.getController();
		reg.put(name, byController);
	}
}
