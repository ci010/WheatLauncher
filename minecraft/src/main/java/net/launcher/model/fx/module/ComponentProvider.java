package net.launcher.model.fx.module;

import javafx.concurrent.Task;
import net.launcher.model.fx.View;

import java.util.List;

/**
 * @author ci010
 */
public interface ComponentProvider
{
	List<Class<?>> getAllComponentTypes();

	<T> View<T> getComponent(Class<T> type);

	Task<Void> saveComponentTask(View<?> o);
}
