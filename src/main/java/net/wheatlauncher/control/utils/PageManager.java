package net.wheatlauncher.control.utils;

import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.FlowView;
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
				if (current != null)
					reg.get(current).getController().unload();
				viewContext.getController().reload();
				handler.setNewView(new FlowView<>(viewContext), false);
				current = name;
				return true;
			}
			catch (FlowException ignored) {}
		}
		return false;
	}

	public void forceSwitch(Class<?> controller) throws FxmlLoadException, FlowException
	{
		ViewContext<?> byController = ViewFactoryReload.getInstance().createByController
				(controller, null, handler.getViewConfiguration(), handler.getFlowContext());
		ControlUtils.setupInnerController(byController.getController(), handler.getFlowContext());
		byController.getController();
		handler.setNewView(new FlowView<>(byController), false);
	}

	public void switchTo(String name) throws FlowException
	{
		ViewContext<? extends ReloadableController> viewContext = reg.get(name);
		if (viewContext != null)
		{
			if (current != null)
				reg.get(current).getController().unload();
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
