package net.wheatlauncher.control;

import io.datafx.controller.FXMLController;
import io.datafx.controller.FxmlLoadException;
import io.datafx.controller.ViewConfiguration;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.context.ViewMetadata;
import io.datafx.core.DataFXUtils;
import io.datafx.core.ExceptionHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This class is a copy of {@link io.datafx.controller.ViewFactory}.
 * The basic propose of this class is fix the bug, forcing controller constructing, when it loads {@code <fx:include>}
 * tag. It just one line fix at line 109.
 *
 * @author ci010
 */
public class ViewFactoryReload
{
	private static ViewFactoryReload instance;

	private ViewFactoryReload() {}

	public static synchronized ViewFactoryReload getInstance()
	{
		if (instance == null)
			instance = new ViewFactoryReload();
		return instance;
	}

	public <T> ViewContext<T> createByController(Class<T> controllerClass) throws FxmlLoadException
	{
		return this.createByController(controllerClass, null);
	}

	public <T> ViewContext<T> createByController(Class<T> controllerClass, String fxmlName) throws FxmlLoadException
	{
		return this.createByController(controllerClass, fxmlName, new ViewConfiguration());
	}

	public <T> ViewContext<T> createByController(Class<T> controllerClass, String fxmlName, ViewConfiguration viewConfiguration, Object... viewContextResources) throws FxmlLoadException
	{
		try
		{
			Object e = controllerClass.newInstance();
			ViewMetadata metadata = new ViewMetadata();
			FXMLController controllerAnnotation = controllerClass.getAnnotation(FXMLController.class);
			if (controllerAnnotation != null && !controllerAnnotation.title().isEmpty())
				metadata.setTitle(controllerAnnotation.title());

			if (controllerAnnotation != null && !controllerAnnotation.iconPath().isEmpty())
				metadata.setGraphic(new ImageView(controllerClass.getResource(controllerAnnotation.iconPath()).toExternalForm()));

			FXMLLoader loader = this.createLoader(e, fxmlName, viewConfiguration);
			Node viewNode = loader.load();
			ViewContext context = new ViewContext<>(viewNode, e, metadata, viewConfiguration, viewContextResources);
			context.register(e);
			context.register("controller", e);
			this.injectFXMLNodes(context);
			context.getResolver().injectResources(e);
			Method[] var11 = e.getClass().getMethods();

			for (Method method : var11)
				if (method.isAnnotationPresent(PostConstruct.class))
					method.invoke(e);
			return context;
		}
		catch (Exception var15)
		{
			throw new FxmlLoadException(var15);
		}
	}

	public void showInStage(Stage stage, Class<?> controllerClass) throws FxmlLoadException
	{
		Scene myScene = new Scene((Parent) this.createByController(controllerClass).getRootNode());
		stage.setScene(myScene);
		stage.show();
	}

	private FXMLLoader createLoader(Object controller, String fxmlName, ViewConfiguration viewConfiguration) throws FxmlLoadException
	{
		Class controllerClass = controller.getClass();
		String foundFxmlName = this.getFxmlName(controllerClass);
		if (fxmlName != null) foundFxmlName = fxmlName;

		if (foundFxmlName == null)
			throw new FxmlLoadException("No FXML File specified!");
		else
		{
			FXMLLoader fxmlLoader = new FXMLLoader(controllerClass.getResource(foundFxmlName));
			fxmlLoader.setBuilderFactory(viewConfiguration.getBuilderFactory());
			fxmlLoader.setCharset(viewConfiguration.getCharset());
			fxmlLoader.setResources(viewConfiguration.getResources());
			fxmlLoader.setController(controller);
			fxmlLoader.setControllerFactory(c ->
			{
				try
				{
					return c.newInstance();
				}
				catch (InstantiationException | IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			});
//			fxmlLoader.setControllerFactory((c) -> controller);
//			To prevent child FXMLLoader construct a same controller...
			return fxmlLoader;
		}
	}

	private String getFxmlName(Class<?> controllerClass)
	{
		String foundFxmlName = null;
		if (controllerClass.getSimpleName().endsWith("Controller"))
		{
			String controllerAnnotation = controllerClass.getSimpleName().substring(0, controllerClass.getSimpleName().length() - "Controller".length()) + ".fxml";
			if (DataFXUtils.canAccess(controllerClass, controllerAnnotation))
				foundFxmlName = controllerAnnotation;
		}

		FXMLController controllerAnnotation1 = controllerClass.getAnnotation(FXMLController.class);
		if (controllerAnnotation1 != null)
			foundFxmlName = controllerAnnotation1.value();

		return foundFxmlName;
	}

	public <T> Tab createTab(Class<T> controllerClass) throws FxmlLoadException
	{
		return this.createTab(this.createByController(controllerClass));
	}

	public <T> Tab createTab(Class<T> controllerClass, ExceptionHandler exceptionHandler) throws FxmlLoadException
	{
		return this.createTab(this.createByController(controllerClass), exceptionHandler);
	}

	public <T> Tab createTab(ViewContext<T> context)
	{
		return this.createTab(context, ExceptionHandler.getDefaultInstance());
	}

	public <T> Tab createTab(ViewContext<T> context, ExceptionHandler exceptionHandler)
	{
		Tab tab = new Tab();
		tab.textProperty().bind(context.getMetadata().titleProperty());
		tab.graphicProperty().bind(context.getMetadata().graphicsProperty());
		tab.setOnClosed((e) ->
		{
			try
			{
				context.destroy();
			}
			catch (Exception var4)
			{
				exceptionHandler.setException(var4);
			}

		});
		tab.setContent(context.getRootNode());
		return tab;
	}

	private <T> void injectFXMLNodes(ViewContext<T> context)
	{
		Object controller = context.getController();
		Node n = context.getRootNode();
		List<Field> fields = DataFXUtils.getInheritedPrivateFields(controller.getClass());

		fields.stream().filter(field -> field.getAnnotation(FXML.class) != null && DataFXUtils.getPrivileged(field, controller) == null && Node.class.isAssignableFrom(field.getType())).forEach(field ->
		{
			Node toInject = n.lookup("#" + field.getName());
			if (toInject != null)
				DataFXUtils.setPrivileged(field, controller, toInject);
		});

	}
}
