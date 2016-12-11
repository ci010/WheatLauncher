package net.wheatlauncher.utils;

import com.jfoenix.controls.JFXDialog;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import net.wheatlauncher.control.FXMLInnerController;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ci010
 */
public class ControlUtils
{
	public static void setupInnerController(Object o, ViewFlowContext context)
	{
		if (o == null) return;
		Class<?> clz = o.getClass();
		for (Field field : clz.getFields())
			if (field.isAnnotationPresent(FXMLInnerController.class))
				try
				{
					Object cont = field.get(o);
					if (cont != null) setupController(cont, context);
					else throw new RuntimeException("The inner controller is null.");
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
	}

	public static void setupController(Object o, ViewFlowContext context)
	{
		if (o == null)
			return;
		Class<?> clz = o.getClass();
		for (Field field : clz.getDeclaredFields())
			if (field.isAnnotationPresent(FXMLInnerController.class))
			{
				try
				{
					Object cont = field.get(o);
					if (cont != null) setupController(cont, context);
					else throw new RuntimeException("The inner controller is null. "+ o.getClass());
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			else if (field.isAnnotationPresent(FXMLViewFlowContext.class) && field.getType() == ViewFlowContext.class)
			{
				if (!field.isAccessible()) field.setAccessible(true);
				try
				{
					field.set(o, context);
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		for (Method method : clz.getMethods())
			if (method.isAnnotationPresent(PostConstruct.class) && method.getParameterTypes().length == 0)
			{
				if (!method.isAccessible()) method.setAccessible(true);
				try
				{
					method.invoke(o);
				}
				catch (IllegalAccessException | InvocationTargetException e)
				{
					System.out.println(method);
					e.printStackTrace();
				}
			}
	}

	public static void setDialogHolderBackground(JFXDialog dialog, Background background)
	{
		try
		{
			Field contentHolder = JFXDialog.class.getDeclaredField("contentHolder");
			if (!contentHolder.isAccessible())
				contentHolder.setAccessible(true);
			StackPane holder = (StackPane) contentHolder.get(dialog);
			holder.setBackground(background);
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
