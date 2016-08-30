package net.wheatlauncher.gui;

import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.ViewFactory;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.FlowView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ci010
 */
public class PageSwitcher
{
	private FlowHandler handler;
	private String current;
	private Map<String, ViewContext<? extends ReloadableController>> reg = new HashMap<>();

	public PageSwitcher(FlowHandler handler)
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
		ViewContext<? extends ReloadableController> byController = ViewFactory.getInstance().createByController(controller, null,
				handler.getViewConfiguration(), handler.getFlowContext());
		reg.put(name, byController);
	}
}
