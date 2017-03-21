package api.launcher.module;

import api.launcher.View;

import java.util.List;

/**
 * @author ci010
 */
public interface ComponentProvider
{
	List<Class<?>> getAllComponentTypes();

	<T> View<T> getComponent(Class<T> type);

	boolean saveComponent(View<?> o) throws Exception;
}
